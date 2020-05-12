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
