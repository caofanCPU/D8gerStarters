package com.xyz.caofancpu.enumtype;

import com.xyz.caofancpu.enumtype.converter.EnumRequestJSONConverter;
import com.xyz.caofancpu.enumtype.converter.StringToEnumConverterFactory;
import com.xyz.caofancpu.enumtype.converter.ValueToEnumConverterFactory;
import org.springframework.core.convert.converter.ConverterFactory;

/**
 * 枚举请求转换器
 *
 * @author D8GER
 */
public class EnumRequestJSONConverterUtil {

    @Deprecated
    public static ConverterFactory buildValueToEnumConverterFactory() {
        return new ValueToEnumConverterFactory();
    }

    @Deprecated
    public static ConverterFactory buildStringToEnumConverterFactory() {
        return new StringToEnumConverterFactory();
    }

    @SuppressWarnings({"unchecked", "rawTypes"})
    public static EnumRequestJSONConverter build() {
        return new EnumRequestJSONConverter();
    }

}
