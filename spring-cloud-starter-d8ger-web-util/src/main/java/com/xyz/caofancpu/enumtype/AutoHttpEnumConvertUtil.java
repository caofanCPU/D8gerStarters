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

package com.xyz.caofancpu.enumtype;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.xyz.caofancpu.enumtype.converter.EnumRequestJSONConverter;
import com.xyz.caofancpu.enumtype.converter.EnumResponseJSONConverter;

/**
 * Http请求响应IEnum枚举转换工具类
 *
 * @author D8GER
 */
public class AutoHttpEnumConvertUtil {

    /**
     * HTTP请求枚举转换器
     *
     * @return
     */
    public static EnumRequestJSONConverter buildRequestJSONConverter() {
        return new EnumRequestJSONConverter();
    }

    /**
     * HTTP响应枚举转换器
     *
     * @return
     */
    public static JsonSerializer buildResponseJSONConverter() {
        return new EnumResponseJSONConverter();
    }
}
