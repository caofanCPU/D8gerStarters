package com.xyz.caofancpu.enumtype;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.xyz.caofancpu.enumtype.converter.EnumResponseJSONConverter;

/**
 * 枚举响应转换器
 *
 * @author D8GER
 */
public class EnumResponseJSONConverterUtil {

    public static JsonSerializer build() {
        return new EnumResponseJSONConverter();
    }
}
