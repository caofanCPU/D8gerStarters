package com.xyz.caofancpu.enumtype.converter;

import com.google.common.collect.Maps;
import com.xyz.caofancpu.constant.IEnum;
import com.xyz.caofancpu.enumtype.EnumUtil;
import com.xyz.caofancpu.result.GlobalErrorInfoRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.util.Assert;

import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author D8GER
 */
@Slf4j
@Deprecated
public class StringToEnumConverterFactory<E extends Enum> implements ConverterFactory<String, E> {
    private final Map<Class, Converter> nameToEnumConverterMap = Maps.newHashMap();

    @Override
    @SuppressWarnings("unchecked")
    public <T extends E> Converter<String, T> getConverter(Class<T> targetType) {
        Converter<String, T> converter = nameToEnumConverterMap.get(targetType);
        if (Objects.nonNull(converter)) {
            return converter;
        }
        if (IEnum.class.isAssignableFrom(targetType)) {
            converter = new StringToEnumConverter(targetType);
        } else {
            converter = this.getDefaultConverter(targetType);
        }
        nameToEnumConverterMap.put(targetType, converter);
        return converter;
    }

    @SuppressWarnings("unchecked")
    private <T extends Enum> Converter<String, T> getDefaultConverter(Class<T> targetType) {
        Class<?> enumType = targetType;
        while (enumType != null && !enumType.isEnum()) {
            enumType = enumType.getSuperclass();
        }
        Assert.notNull(enumType, () -> "The target type " + targetType.getName() + " does not refer to an enum");
        return new StringToEnum(targetType);
    }

    private class StringToEnumConverter<E extends IEnum> implements Converter<String, E> {
        private final Class<E> enumType;

        StringToEnumConverter(Class<E> enumType) {
            this.enumType = enumType;
        }

        /**
         * 转换顺序: value -> name -> viewName
         *
         * @param source
         * @return
         */
        @Override
        public E convert(String source) {
            try {
                int value = Integer.parseInt(source);
                return EnumUtil.getEnum(this.enumType, IEnum::getValue, value);
            } catch (Exception e) {
                // do nothing
            }
            E nameEnum = EnumUtil.getEnum(this.enumType, IEnum::getName, source);
            if (Objects.isNull(nameEnum)) {
                nameEnum = EnumUtil.getEnum(this.enumType, IEnum::getViewName, source);
                if (Objects.isNull(nameEnum)) {
                    log.error("接口传参枚举转换错误, 原因: 传值[{}], 目标枚举类[{}]", source, this.enumType.getSimpleName());
                    throw new GlobalErrorInfoRuntimeException("参数非法, [" + this.enumType.getSimpleName() + "]不存在枚举值[" + source + "]");
                }
            }
            return nameEnum;
        }
    }

    @SuppressWarnings("unchecked")
    private class StringToEnum<T extends Enum> implements Converter<String, T> {

        private final Class<T> enumType;

        StringToEnum(Class<T> enumType) {
            this.enumType = enumType;
        }

        @Override
        public T convert(String source) {
            if (source.isEmpty()) {
                return null;
            }
            source = source.trim();
            try {
                return (T) Enum.valueOf(this.enumType, source);
            } catch (Exception ex) {
                return findEnum(source);
            }
        }

        private T findEnum(String source) {
            String name = getLettersAndDigits(source);
            for (T candidate : (Set<T>) EnumSet.allOf(this.enumType)) {
                if (getLettersAndDigits(candidate.name()).equals(name)) {
                    return candidate;
                }
            }
            throw new IllegalArgumentException("No enum constant "
                    + this.enumType.getCanonicalName() + "." + source);
        }

        private String getLettersAndDigits(String name) {
            StringBuilder canonicalName = new StringBuilder(name.length());
            name.chars().map((c) -> (char) c).filter(Character::isLetterOrDigit)
                    .map(Character::toLowerCase).forEach(canonicalName::append);
            return canonicalName.toString();
        }

    }
}


