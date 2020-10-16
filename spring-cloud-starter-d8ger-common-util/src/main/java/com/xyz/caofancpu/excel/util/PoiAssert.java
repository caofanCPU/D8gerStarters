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

package com.xyz.caofancpu.excel.util;

import com.xyz.caofancpu.excel.exception.ExcelException;

import java.util.Collection;
import java.util.Map;

/**
 * 断言
 *
 * @author D8GER
 */
public class PoiAssert {
    /**
     * 验证参数是不是空的.
     * 实现中会根据object的实际类型做校验.
     * 目前支持String、Collection、Map
     *
     * @param object       验证对象
     * @param errorMessage 自定义错误消息
     */
    public static void isEmpty(Object object, String errorMessage) {
        isTrue(null != object, errorMessage);
        if (object instanceof String) {
            isTrue(!object.toString().isEmpty(), errorMessage);
        } else if (object instanceof Collection) {
            isTrue(!((Collection) object).isEmpty(), errorMessage);
        } else if (object instanceof Map) {
            isTrue(!((Map) object).isEmpty(), errorMessage);
        }
    }

    /**
     * 验证传入的表达式是否为真.
     *
     * @param expression 逻辑表达式
     */
    public static void isTrue(boolean expression) {
        isTrue(expression, "校验失败");
    }

    /**
     * 验证传入的表达式是否为真.
     *
     * @param expression   逻辑表达式
     * @param errorMessage 自定义错误消息
     */
    public static void isTrue(boolean expression, String errorMessage) {
        if (!expression) {
            throw new ExcelException(errorMessage);
        }
    }
}
