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

package com.xyz.caofancpu.mvc.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.xyz.caofancpu.core.CollectionFunUtil;
import com.xyz.caofancpu.core.JSONUtil;
import com.xyz.caofancpu.extra.ReflectionUtil;
import com.xyz.caofancpu.mvc.annotation.Check;
import com.xyz.caofancpu.result.CustomerErrorInfo;
import com.xyz.caofancpu.result.D8Response;
import com.xyz.caofancpu.result.GlobalErrorInfoException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

/**
 * 参数校验拦截器
 *
 * @author D8GER
 */
@Aspect
@Component
@Order(2)
@Slf4j
public class CheckParamAspect {
    // -====================== 常量 =========================

    private static final String SEPARATOR = ":";
    private static final Pattern moneyPattern = Pattern.compile("^\\d+(\\.\\d{1,2})?$");
    private static final Pattern mailPattern = Pattern.compile("^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$");

    /**
     * 是否不为空
     *
     * @param value       字段值
     * @param operatorNum 操作数，这里不需要，只是为了参数统一
     * @return
     */
    @SuppressWarnings("unchecked")
    private static <E> Boolean isNotNull(Object value, String operatorNum) {
        if (value instanceof String) {
            return StringUtils.isNotBlank((String) value);
        }
        if (value instanceof Collection) {
            return CollectionFunUtil.isNotEmpty((Collection<E>) value);
        }
        return Objects.nonNull(value);
    }

    /**
     * 是否为数字
     *
     * @param value       字段值
     * @param operatorNum 操作数，这里不需要，只是为了参数统一
     * @return
     */
    private static Boolean isNumber(Object value, String operatorNum) {
        return Objects.nonNull(value) && value instanceof Number;
    }

    @Deprecated
    private static Boolean isBoolStr(Object value, String operatorNum) {
        return Objects.nonNull(value) && value instanceof Boolean;
    }

    /**
     * 是否为金额
     *
     * @param value       字段值
     * @param operatorNum 操作数，这里不需要，只是为了参数统一
     * @return
     */
    private static Boolean isMoney(Object value, String operatorNum) {
        return Objects.nonNull(value) && moneyPattern.matcher(value.toString()).matches();
    }

    // -=================== 对不同类型的值进行校验 起 =======================

    /**
     * 是否为邮箱
     *
     * @param value       字段值
     * @param operatorNum 操作数，这里不需要，只是为了参数统一
     * @return
     */
    private static Boolean isMail(Object value, String operatorNum) {
        return Objects.nonNull(value) && mailPattern.matcher(value.toString()).matches();
    }

    /**
     * 是否大于
     *
     * @param value       字段值
     * @param operatorNum 操作数
     * @return 是否大于
     */
    @SuppressWarnings("unchecked")
    private static <E> Boolean isGreaterThan(Object value, String operatorNum) {
        return Objects.nonNull(value) && judge(value, operatorNum, Operator.GREATER_THAN);
    }

    /**
     * 是否大于等于
     *
     * @param value       字段值
     * @param operatorNum 操作数
     * @return 是否大于等于
     */
    private static Boolean isGreaterThanEqual(Object value, String operatorNum) {
        return Objects.nonNull(value) && judge(value, operatorNum, Operator.GREATER_THAN_EQUAL);
    }

    /**
     * 是否少于
     *
     * @param value       字段值
     * @param operatorNum 操作数
     * @return 是否少于
     */
    private static Boolean isLessThan(Object value, String operatorNum) {
        return Objects.nonNull(value) && judge(value, operatorNum, Operator.LESS_THAN);
    }

    /**
     * 是否少于等于
     *
     * @param value       字段值
     * @param operatorNum 操作数
     * @return 是否少于等于
     */
    private static Boolean isLessThanEqual(Object value, String operatorNum) {
        return Objects.nonNull(value) && judge(value, operatorNum, Operator.LESS_THAN_EQUAL);
    }

    @SuppressWarnings("unchecked")
    private static <E> boolean judge(@NonNull Object value, @NonNull String operatorNum, Operator operator) {
        Double a = null;
        Double b = null;
        if (value instanceof String) {
            a = (double) value.toString().length();
            b = Double.parseDouble(operatorNum);
        }

        if (value instanceof Integer || value instanceof Long || value instanceof Short || value instanceof Float || value instanceof Double) {
            a = Double.parseDouble(value.toString());
            b = Double.parseDouble(operatorNum);
        }

        if (value instanceof Collection) {
            a = (double) ((Collection<E>) value).size();
        }

        return Objects.nonNull(a) && Objects.nonNull(b) && compare(a, b, operator);
    }

    private static boolean compare(@NonNull Double a, @NonNull Double b, Operator operator) {
        switch (operator) {
            case GREATER_THAN:
                return a > b;
            case GREATER_THAN_EQUAL:
                return a >= b;
            case LESS_THAN:
                return a < b;
            case LESS_THAN_EQUAL:
                return a <= b;
            case NOT_EQUAL:
                return !a.equals(b);
            default:
                throw new IllegalArgumentException("操作符参数异常");
        }
    }

    /**
     * 是否不等于
     *
     * @param value       字段值
     * @param operatorNum 操作数
     * @return 是否不等于
     */
    private static Boolean isNotEqual(Object value, String operatorNum) {
        return Objects.nonNull(value) && judge(value, operatorNum, Operator.NOT_EQUAL);
    }

    @Around("@annotation(com.xyz.caofancpu.mvc.annotation.Check)")
    public Object check(ProceedingJoinPoint point)
            throws Throwable {
        // 参数校验
        String msg = doCheck(point);
        if (StringUtils.isNotEmpty(msg)) {
            // 校验未通过, 打印日志, 封装异常抛出
            StringBuilder sb = new StringBuilder();
            sb.append("\n[后台响应结果]:\n"
                    + "响应耗时[0ms]" + "\n"
                    + "响应数据结果:\n"
                    + JSONUtil.formatStandardJSON(JSONObject.toJSONString(D8Response.fail(msg))));
            log.info(sb.toString());
            // 这里可以返回自己封装的返回类
            CustomerErrorInfo errInfo = new CustomerErrorInfo("501", msg);
            throw new GlobalErrorInfoException(errInfo);
        }
        // 通过校验，继续执行原有方法
        return point.proceed();
    }

    /**
     * 参数校验
     *
     * @param point ProceedingJoinPoint
     * @return 错误信息
     */
    private String doCheck(ProceedingJoinPoint point) {
        // 获取方法参数值
        Object[] arguments = point.getArgs();
        // 获取方法
        Method method = getMethod(point);
        // 默认的错误信息
        String methodInfo = StringUtils.isBlank(method.getName()) ? "" : " 调用方法 " + method.getName();
        String msg = "";
        if (isCheck(method, arguments)) {
            Check annotation = method.getAnnotation(Check.class);
            String[] fields = annotation.value();
            // 只支持对第一个参数进行校验
            Object vo = arguments[0];
            if (vo == null) {
                msg = "入参不能为空!";
            } else {
                for (String field : fields) {
                    if (StringUtils.isNotEmpty(msg)) {
                        break;
                    }
                    // 解析字段
                    FieldInfo info = resolveField(field, methodInfo);
                    // 获取字段的值
                    Object value = ReflectionUtil.invokeGetter(vo, info.field);
                    // 执行校验规则
                    Boolean isValid = info.optEnum.fun.apply(value, info.operatorNum);
                    msg = isValid ? msg : info.innerMsg;
                }
            }
        }
        return msg;
    }

    private Method getMethod(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        if (method.getDeclaringClass().isInterface()) {
            try {
                method = joinPoint
                        .getTarget()
                        .getClass()
                        .getDeclaredMethod(joinPoint.getSignature().getName(), method.getParameterTypes());
            } catch (SecurityException | NoSuchMethodException e) {
                log.error("反射获取方法失败，{}" + e.getMessage());
            }
        }
        return method;
    }

    /**
     * 解析字段
     *
     * @param fieldStr   字段字符串
     * @param methodInfo 方法信息
     * @return 字段信息实体类
     */
    private FieldInfo resolveField(String fieldStr, String methodInfo) {
        FieldInfo fieldInfo = new FieldInfo();
        String innerMsg = "";
        // 解析提示信息
        if (fieldStr.contains(SEPARATOR)) {
            innerMsg = fieldStr.split(SEPARATOR)[1];
            fieldStr = fieldStr.split(SEPARATOR)[0];
        }
        // 解析操作符
        if (fieldStr.contains(Operator.GREATER_THAN_EQUAL.value)) {
            fieldInfo.optEnum = Operator.GREATER_THAN_EQUAL;
        } else if (fieldStr.contains(Operator.LESS_THAN_EQUAL.value)) {
            fieldInfo.optEnum = Operator.LESS_THAN_EQUAL;
        } else if (fieldStr.contains(Operator.GREATER_THAN.value)) {
            fieldInfo.optEnum = Operator.GREATER_THAN;
        } else if (fieldStr.contains(Operator.LESS_THAN.value)) {
            fieldInfo.optEnum = Operator.LESS_THAN;
        } else if (fieldStr.contains(Operator.NOT_EQUAL.value)) {
            fieldInfo.optEnum = Operator.NOT_EQUAL;
        } else if (fieldStr.contains(Operator.IS_NUMBER.value)) {
            fieldInfo.optEnum = Operator.IS_NUMBER;
        } else if (fieldStr.contains(Operator.IS_BOOL_STR.value)) {
            fieldInfo.optEnum = Operator.IS_BOOL_STR;
        } else if (fieldStr.contains(Operator.IS_MONEY.value)) {
            fieldInfo.optEnum = Operator.IS_MONEY;
        } else if (fieldStr.contains(Operator.IS_MAIL.value)) {
            fieldInfo.optEnum = Operator.IS_MAIL;
        } else {
            fieldInfo.optEnum = Operator.NOT_NULL;
        }
        // 不等于空，直接赋值字段
        if (fieldInfo.optEnum == Operator.NOT_NULL) {
            fieldInfo.field = fieldStr.substring(0, (fieldStr.indexOf(Operator.NOT_NULL.value) - 1));
            fieldInfo.operatorNum = "";
        } else if (fieldInfo.optEnum == Operator.IS_NUMBER) {
            fieldInfo.field = fieldStr.substring(0, (fieldStr.indexOf(Operator.IS_NUMBER.value) - 1));
            fieldInfo.operatorNum = "";
        } else if (fieldInfo.optEnum == Operator.IS_BOOL_STR) {
            fieldInfo.field = fieldStr.substring(0, (fieldStr.indexOf(Operator.IS_BOOL_STR.value) - 1));
            fieldInfo.operatorNum = "";
        } else if (fieldInfo.optEnum == Operator.IS_MONEY) {
            fieldInfo.field = fieldStr.substring(0, (fieldStr.indexOf(Operator.IS_MONEY.value) - 1));
            fieldInfo.operatorNum = "";
        } else if (fieldInfo.optEnum == Operator.IS_MAIL) {
            fieldInfo.field = fieldStr.substring(0, (fieldStr.indexOf(Operator.IS_MAIL.value) - 1));
            fieldInfo.operatorNum = "";
        }
        // 其他操作符，需要分离出字段和操作数
        else {
            fieldInfo.field = fieldStr.split(fieldInfo.optEnum.value)[0];
            fieldInfo.operatorNum = fieldStr.split(fieldInfo.optEnum.value)[1];
        }
        fieldInfo.operator = fieldInfo.optEnum.value;
        // 处理错误信息
        String defaultMsg = fieldInfo.field + " must " + fieldInfo.operator + " " + fieldInfo.operatorNum + methodInfo;
        fieldInfo.innerMsg = StringUtils.isBlank(innerMsg) ? defaultMsg : innerMsg;
        return fieldInfo;
    }


    // ==================== 对不同类型的值进行校验 =======================

    /**
     * 判断是否符合参数规则
     *
     * @param method    方法
     * @param arguments 方法参数
     * @return 是否符合
     */
    private Boolean isCheck(Method method, Object[] arguments) {
        return method.isAnnotationPresent(Check.class) && CollectionFunUtil.isNotEmpty(arguments);
    }

    /**
     * 获取方法
     *
     * @param joinPoint ProceedingJoinPoint
     * @return 方法
     */


    /**
     * 操作枚举，封装操作符和对应的校验规则
     */
    enum Operator {
        /**
         * 大于
         */
        GREATER_THAN(">", CheckParamAspect::isGreaterThan),
        /**
         * 大于等于
         */
        GREATER_THAN_EQUAL(">=", CheckParamAspect::isGreaterThanEqual),
        /**
         * 小于
         */
        LESS_THAN("<", CheckParamAspect::isLessThan),
        /**
         * 小于等于
         */
        LESS_THAN_EQUAL("<=", CheckParamAspect::isLessThanEqual),
        /**
         * 不等于
         */
        NOT_EQUAL("!=", CheckParamAspect::isNotEqual),
        /**
         * 不为空(包含null || "" || "   ")
         */
        NOT_NULL("not empty", CheckParamAspect::isNotNull),
        /**
         * 金额
         */
        IS_MONEY("is money", CheckParamAspect::isMoney),
        /**
         * 邮箱
         */
        IS_MAIL("is mail", CheckParamAspect::isMail),
        /**
         * 数字
         */
        IS_NUMBER("is number", CheckParamAspect::isNumber),

        /**
         * 布尔型字符串
         * Spring框架已对此做了参数验证
         */
        @Deprecated
        IS_BOOL_STR("is bool string", CheckParamAspect::isBoolStr);

        private final String value;

        /**
         * BiFunction：接收字段值(Object)和操作数(String)，返回是否符合规则(Boolean)
         */
        private final BiFunction<Object, String, Boolean> fun;

        Operator(String value, BiFunction<Object, String, Boolean> fun) {
            this.value = value;
            this.fun = fun;
        }
    }

    /**
     * 字段信息
     */
    class FieldInfo {
        /**
         * 字段
         */
        String field;
        /**
         * 提示信息
         */
        String innerMsg;
        /**
         * 操作符
         */
        String operator;
        /**
         * 操作数
         */
        String operatorNum;
        /**
         * 操作枚举
         */
        Operator optEnum;
    }


}
