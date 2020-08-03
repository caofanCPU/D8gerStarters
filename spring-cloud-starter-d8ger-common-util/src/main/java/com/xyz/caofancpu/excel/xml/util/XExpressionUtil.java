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

package com.xyz.caofancpu.excel.xml.util;

import com.xyz.caofancpu.excel.exception.ExcelException;

import java.math.BigDecimal;

/**
 * 表达式工具类
 */
public class XExpressionUtil {

    /**
     * 获取boolean值
     */
    public static Boolean getBoolean(Object object) {
        if (object == null) {
            throw new NullPointerException();
        }
        if (object instanceof Boolean) {
            return (Boolean) object;
        }
        throw new ClassCastException(object.getClass() + " Cannot convert Boolean");
    }

    /**
     * 加法运算
     *
     * @param v1 被加数
     * @param v2 加数
     */
    public static Object add(Object v1, Object v2) {
        if (null == v1) {
            return v2;
        }
        if (null == v2) {
            return v1;
        }
        if (v1 instanceof String) {
            return v1 + String.valueOf(v2);
        }
        if (v2 instanceof String) {
            return String.valueOf(v1) + v2;
        }
        if (!(v1 instanceof Number && v2 instanceof Number)) {
            throw new ExcelException("运算类型错误");
        }
        if (v1 instanceof Double || v2 instanceof Double) {
            Number value1 = (Number) v1;
            Number value2 = (Number) v2;
            return value1.doubleValue() + value2.doubleValue();
        }
        if (v1 instanceof Float || v2 instanceof Float) {
            Number value1 = (Number) v1;
            Number value2 = (Number) v2;
            return value1.floatValue() + value2.floatValue();
        }
        Number value1 = (Number) v1;
        Number value2 = (Number) v2;
        return value1.intValue() + value2.intValue();
    }


    /**
     * 减法运算
     *
     * @param v1 被减数
     * @param v2 减数
     */
    public static Object sub(Object v1, Object v2) {
        // 新支持：有且仅有一个为null，返回null
        if ((v1 == null && v2 != null) || (v1 != null && v2 == null)) {
            return null;
        }
        if (null == v2) {
            return v1;
        }
        if (null == v1) {
            if (!(v2 instanceof Number)) {
                throw new ExcelException("运算类型错误");
            }
            Number value2 = (Number) v2;
            if (value2 instanceof Double) {
                return -value2.doubleValue();
            }
            if (value2 instanceof Float) {
                return -value2.floatValue();
            }
            return -value2.intValue();
        }
        if (!(v1 instanceof Number && v2 instanceof Number)) {
            throw new ExcelException("运算类型错误");
        }
        if (v1 instanceof Double || v2 instanceof Double) {
            Number value1 = (Number) v1;
            Number value2 = (Number) v2;
            return value1.doubleValue() - value2.doubleValue();
        }
        if (v1 instanceof Float || v2 instanceof Float) {
            Number value1 = (Number) v1;
            Number value2 = (Number) v2;
            return value1.floatValue() - value2.floatValue();
        }
        Number value1 = (Number) v1;
        Number value2 = (Number) v2;
        return value1.intValue() - value2.intValue();
    }

    /**
     * 乘法运算
     *
     * @param v1 被乘数
     * @param v2 乘数
     */
    public static Object mul(Object v1, Object v2) {
        if (null == v1) {
            v1 = BigDecimal.ONE;
        }
        if (null == v2) {
            v2 = BigDecimal.ONE;
        }
        if (!(v1 instanceof Number && v2 instanceof Number)) {
            throw new ExcelException("运算类型错误");
        }
        if (v1 instanceof Double || v2 instanceof Double) {
            Number value1 = (Number) v1;
            Number value2 = (Number) v2;
            return value1.doubleValue() * value2.doubleValue();
        }
        if (v1 instanceof Float || v2 instanceof Float) {
            Number value1 = (Number) v1;
            Number value2 = (Number) v2;
            return value1.floatValue() * value2.floatValue();
        }
        Number value1 = (Number) v1;
        Number value2 = (Number) v2;
        return value1.intValue() * value2.intValue();
    }

    /**
     * 除法运算
     *
     * @param v1 被除数
     * @param v2 除数
     */
    public static Object div(Object v1, Object v2) {
        if (null == v1) {
            return BigDecimal.ZERO;
        }
        if (null == v2) {
            v2 = BigDecimal.ONE;
        }
        if (!(v1 instanceof Number && v2 instanceof Number)) {
            throw new ExcelException("运算类型错误");
        }

        if (((Number) v2).doubleValue() == 0) {
            throw new ExcelException("除数不能为0");
        }

        if (v1 instanceof Double || v2 instanceof Double) {
            Number value1 = (Number) v1;
            Number value2 = (Number) v2;
            return value1.doubleValue() / value2.doubleValue();
        }
        if (v1 instanceof Float || v2 instanceof Float) {
            Number value1 = (Number) v1;
            Number value2 = (Number) v2;
            return value1.floatValue() / value2.floatValue();
        }
        Number value1 = (Number) v1;
        Number value2 = (Number) v2;
        return value1.intValue() / value2.intValue();
    }

    /**
     * 余数运算
     *
     * @param v1 被除数
     * @param v2 除数
     */
    public static Object mod(Object v1, Object v2) {
        if (null == v1) {
            return BigDecimal.ZERO;
        }
        if (null == v2) {
            v2 = BigDecimal.ONE;
        }

        if (!(v1 instanceof Number && v2 instanceof Number)) {
            throw new ExcelException("运算类型错误");
        }

        if (((Number) v2).doubleValue() == 0) {
            throw new ExcelException("除数不能为0");
        }


        if (v1 instanceof Double || v2 instanceof Double) {
            Number value1 = (Number) v1;
            Number value2 = (Number) v2;
            return value1.doubleValue() % value2.doubleValue();
        }
        if (v1 instanceof Float || v2 instanceof Float) {
            Number value1 = (Number) v1;
            Number value2 = (Number) v2;
            return value1.floatValue() % value2.floatValue();
        }
        Number value1 = (Number) v1;
        Number value2 = (Number) v2;
        return value1.intValue() % value2.intValue();
    }

}
