/*
 * Copyright 2016-2020 the original author
 *
 * @D8GER(https://github.com/caofanCPU).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xyz.caofancpu.excel.xml;

import com.xyz.caofancpu.excel.enums.Symbol;
import com.xyz.caofancpu.excel.exception.ExcelException;
import com.xyz.caofancpu.excel.tmp.Tmp;
import com.xyz.caofancpu.excel.util.PoiAssert;
import com.xyz.caofancpu.excel.xml.util.XStringUtil;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 表达式解析器
 * 加减乘除没有优先级,请加括号
 *
 * @author D8GER
 * @author guanxiaochen
 */
public class ExpressionParser implements ISymbolParser {

    /**
     * 点
     */
    private static final char SPOT = '.';

    private static final Pattern FORMAT_PATTERN = Pattern.compile("\\{\\{(.*?)\\}\\}");
    private final ThreadLocal<List<Object>> tmp = new ThreadLocal<>();
    protected Map<String, Object> context;

    public ExpressionParser() {
        this(new HashMap<>());
    }


    public ExpressionParser(Map<String, Object> context) {
        this.context = context;
    }

    public Object put(String key, Object value) {
        return context.put(key, value);
    }


    public String formatStr(String str) {
        if (str == null || str.length() < 5) {
            return str;
        }
        Matcher matcher = FORMAT_PATTERN.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, Matcher.quoteReplacement(getString(matcher.group(1))));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public Object get(String eval, Object param) {
        try {
            setTmp0(param);
            //noinspection unchecked
            return parseExpression(replaceString(eval));
        } finally {
            tmp.remove();
        }
    }

    public Object get(String eval) {
        return get(eval, null);
    }

    public String getString(String key) {
        return getString(key, "");
    }

    public String getString(String key, String defaultValue) {
        Object value = get(key);
        if (value == null) {
            return defaultValue;
        }
        return value.toString();
    }


    /**
     * 替换字符串和数值
     */
    private String replaceString(String eval) {
        int start = eval.indexOf("'");
        if (start < 0) {
            return eval;
        }

        // 替换静态站位
        StringBuilder resultSb = new StringBuilder();

        int end = -1;
        String str = "";
        while (start > -1) {
            resultSb.append(eval, end + 1, start);
            end = eval.indexOf('\'', start + 1);
            if (end < 0) {
                throw new ExcelException("表达式错误:[" + eval + "]");
            }
            while ('\\' == eval.charAt(end - 1)) {
                str += eval.substring(start + 1, end - 1);
                start = end - 1;
                end = eval.indexOf('\'', start + 2);
                if (end < 0) {
                    throw new ExcelException("表达式错误:[" + eval + "]");
                }
            }
            str += eval.substring(start + 1, end);

            resultSb.append("$$").append(addTmp(str));

            start = eval.indexOf('\'', end + 1);
            str = "";
        }
        if (end < eval.length() - 1) {
            resultSb.append(eval.substring(end + 1));
        }
        return resultSb.toString();
    }

    @Override
    public Object parseExpression(String key) {
        if (key == null) {
            return null;
        }
        key = key.trim();
        if (key.isEmpty()) {
            return null;
        }

        // 简单运算 '+', '-', '*', '/', '%', '&', '|', '^', '〜', '!', '>', '<', '=', '?'
        int wildCardIndex = XStringUtil.lastIndexOfAny(key, ISymbol.EXPRESSION);
        if (wildCardIndex > -1) {
            return this.parse(key, wildCardIndex);
        }

        char ch = key.charAt(0);

        // 大写,静态方法
        if (Character.isUpperCase(ch)) {
            return invokeStaticMethod(key);
        }

        // 数字
        if (Character.isDigit(ch)) {
            Tmp<Integer, Number> numTmp = XStringUtil.getNum(key);
            if (numTmp.getKey() < key.length() - 1) {
                return getProperty(numTmp.getValue(), key.substring(numTmp.getKey() + 1));
            }
            return numTmp.getValue();
        }

        // 点, 属性方法
        int spotIndex = key.indexOf(SPOT);
        if (spotIndex > 0) {
            Object value = parseExpression(key.substring(0, spotIndex).trim());
            if (value == null) {
                return null;
            }
            return getProperty(value, key.substring(spotIndex + 1).trim());
        }

        //todo guan 数组 有问题,待调整,暂不支持
        int indexStart = key.indexOf('[');
        if (indexStart > -1) {
            int indexEnd = key.indexOf(']');

            Object valueG = parseExpression(key.substring(0, indexStart));
            if (valueG == null || !valueG.getClass().isArray()) {
                return null;
            }

            Object valueH = parseExpression(key.substring(indexStart + 1, indexEnd));
            if (valueH instanceof Integer) {
                return ((Object[]) valueG)[(int) valueH];
            }
            return null;
        }

        // 占位对象替换
        if ('$' == ch && '$' == key.charAt(1)) {
            if (key.length() == 2) {
                return getTmp(0);
            }
            return getTmp(Integer.parseInt(key.substring(2)));
        }

        if ("true".equals(key)) {
            return true;
        }

        if ("false".equals(key)) {
            return false;
        }

        if ("null".equals(key)) {
            return null;
        }

        return context.get(key);
    }

    /**
     * 解析简单运算
     */
    private Object parse(String key, int index) {
        if (index >= 0) {
            Symbol symbol = Symbol.create(key.charAt(index));
            return symbol.parse(this, key, index);
        }
        return parseExpression(key);
    }


    private Object invokeMethod(Object object, String eval) {
        int indexStart = eval.indexOf("(");
        int indexEnd = XStringUtil.getParenthesesEnd(eval, indexStart);
        String paramsStr = eval.substring(indexStart + 1, indexEnd).trim();

        try {
            Object[] paramValues = getParamValues(paramsStr);
            Object value = invokeMethod(object, eval.substring(0, indexStart), paramValues);
            if (value != null && eval.length() > indexEnd + 1) {
                return getProperty(value, eval.substring(indexEnd + 1).trim());
            }
            return value;
        } catch (Exception e) {
            throw new ExcelException("获取对象属性失败[" + eval + "], class:" + object.getClass(), e);
        }
    }

    /**
     * 解析方法参数
     */
    private Object[] getParamValues(String paramsStr) {
        if (paramsStr != null && !paramsStr.isEmpty()) {
            String[] params = paramsStr.split(",");
            Object[] paramValues = new Object[params.length];
            for (int i = 0; i < params.length; i++) {
                String trim = params[i].trim();
                if ("$$".equals(trim)) {
                    paramValues[i] = getTmp(0);
                } else {
                    paramValues[i] = parseExpression(trim);
                }
            }
            return paramValues;
        }
        return null;
    }

    private Object invokeMethod(Object object, String methodName, Object[] paramValues)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if ("get".equals(methodName)) {
            if (object instanceof Map) {
                return ((Map) object).get(paramValues[0]);
            }
            if (object instanceof List) {
                int paramValue;
                if (paramValues[0] instanceof Number) {
                    paramValue = ((Number) paramValues[0]).intValue();
                } else {
                    paramValue = Integer.parseInt((String) paramValues[0]);
                }
                return ((List) object).get(paramValue);
            }
        }
        return MethodUtils.invokeMethod(object, methodName, paramValues);
    }

    @Override
    public Object invokeStaticMethod(String eval) {
        Function function = FunctionManager.FUNCTION_MAP.get(eval);
        if (function != null) {
            //noinspection unchecked
            return function.apply(getTmp(0));
        }

        int indexStart = eval.indexOf("(");
        if (indexStart > 0) {
            String classMethodStr = eval.substring(0, indexStart);
            int indexOf = classMethodStr.lastIndexOf(".");
            PoiAssert.isTrue(indexOf > 0, "invokeStaticMothod error");

            String className = classMethodStr.substring(0, indexOf);
            Class<?> aClass = FunctionManager.getClassName(className);
            if (aClass == null) {
                throw new ExcelException("类不存在[" + className + "]");
            }

            int indexEnd = XStringUtil.getParenthesesEnd(eval, indexStart);
            String paramsStr = eval.substring(indexStart + 1, indexEnd);

            Object[] paramValues = getParamValues(paramsStr);
            try {
                Object value = MethodUtils.invokeStaticMethod(aClass, classMethodStr.substring(indexOf + 1), paramValues);
                if (eval.length() > indexEnd + 1) {
                    return getProperty(value, eval.substring(indexEnd + 1).trim());
                }
                return value;
            } catch (Exception e) {
                throw new ExcelException("获取对象属性失败[" + eval + "]", e);
            }
        } else {
            int indexOf = eval.lastIndexOf(".");
            PoiAssert.isTrue(indexOf > 0, "invokeStaticMothod error");

            String className = eval.substring(0, indexOf);
            Class<?> aClass = FunctionManager.getClassName(className);
            if (aClass == null) {
                return null;
            }

            try {
                return MethodUtils.invokeStaticMethod(aClass, eval.substring(indexOf + 1), null);
            } catch (Exception e) {
                throw new ExcelException("获取对象属性失败[" + eval + "]", e);
            }
        }
    }

    /**
     * 反射获取对象属性
     *
     * @param object
     * @param eval
     */
    @Override
    public Object getProperty(Object object, String eval) {
        if (object == null) {
            return null;
        }
        int indexOf = eval.indexOf(".");
        while (indexOf == 0) {
            eval = eval.substring(1);
            indexOf = eval.indexOf(".");
        }
        if (indexOf > 0) {
            int indexStart = eval.indexOf("(");
            if (indexStart > 0 && indexStart < indexOf) {
                return invokeMethod(object, eval);
            }

            Object value = getProperty(object, eval.substring(0, indexOf).trim());
            return getProperty(value, eval.substring(indexOf + 1));
        } else if (eval.indexOf("(") > 0) {
            return invokeMethod(object, eval);
        }

        try {
            return PropertyUtils.getProperty(object, eval);
        } catch (Exception e) {
            throw new ExcelException("获取对象属性失败[" + eval + "], class:" + object.getClass(), e);
        }
    }


    /**
     * 添加线程临时属性
     */
    private int addTmp(Object object) {
        List<Object> list = tmp.get();
        if (list == null) {
            list = new ArrayList<>();
            list.add(null);
            tmp.set(list);
        }
        list.add(object);
        return list.size() - 1;
    }

    /**
     * 添加线程临时属性
     */
    private void setTmp0(Object object) {
        List<Object> list = tmp.get();
        if (list == null) {
            list = new ArrayList<>();
            list.add(object);
            tmp.set(list);
        } else {
            list.set(0, object);
        }
    }

    /**
     * 获取线程临时属性
     */
    private Object getTmp(int index) {
        List<Object> list = tmp.get();
        if (list == null) {
            return null;
        }
        if (list.size() > index) {
            return list.get(index);
        }
        return null;
    }
}
