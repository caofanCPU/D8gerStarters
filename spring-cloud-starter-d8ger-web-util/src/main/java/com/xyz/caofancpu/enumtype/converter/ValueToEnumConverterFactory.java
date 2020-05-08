package com.xyz.caofancpu.enumtype.converter;

import com.google.common.collect.Maps;
import com.xyz.caofancpu.commonoperateutils.enumtype.IEnum;
import com.xyz.caofancpu.enumtype.EnumUtil;
import com.xyz.caofancpu.result.GlobalErrorInfoRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.Objects;

/**
 *
 */
@Slf4j
@Deprecated
public class ValueToEnumConverterFactory<E extends Enum> implements ConverterFactory<Integer, E> {
    private final Map<Class, Converter> valueToEnumConverterMap = Maps.newHashMap();

    @Override
    @SuppressWarnings("unchecked")
    public <T extends E> Converter<Integer, T> getConverter(Class<T> targetType) {
        Converter<Integer, T> converter = valueToEnumConverterMap.get(targetType);
        if (Objects.nonNull(converter)) {
            return converter;
        }
        if (IEnum.class.isAssignableFrom(targetType)) {
            converter = new ValueToEnumConverter(targetType);
        } else {
            converter = this.getDefaultConverter(targetType);
        }
        valueToEnumConverterMap.put(targetType, converter);
        return converter;
    }

    @SuppressWarnings("unchecked")
    public <T extends Enum> Converter<Integer, T> getDefaultConverter(Class<T> targetType) {
        Class<?> enumType = targetType;
        while (enumType != null && !enumType.isEnum()) {
            enumType = enumType.getSuperclass();
        }
        Assert.notNull(enumType, () -> "The target type " + targetType.getName() + " does not refer to an enum");
        return new IntegerToEnum(targetType);
    }

    private class ValueToEnumConverter<E extends IEnum> implements Converter<Integer, E> {
        private final Class<E> enumType;

        public ValueToEnumConverter(Class<E> enumType) {
            this.enumType = enumType;
        }

        @Override
        public E convert(Integer source) {
            E valueEnum = EnumUtil.getEnum(this.enumType, IEnum::getValue, source);
            if (Objects.isNull(valueEnum)) {
                log.error("接口传参枚举转换错误, 原因: 传值[{}], 目标枚举类[{}]", source, this.enumType.getSimpleName());
                throw new GlobalErrorInfoRuntimeException("参数非法, [" + this.enumType.getSimpleName() + "]不存在枚举值[" + source + "]");
            }
            return valueEnum;
        }
    }

    private class IntegerToEnum<T extends Enum> implements Converter<Integer, T> {

        private final Class<T> enumType;

        IntegerToEnum(Class<T> enumType) {
            this.enumType = enumType;
        }

        @Override
        public T convert(Integer source) {
            return this.enumType.getEnumConstants()[source];
        }
    }
}
