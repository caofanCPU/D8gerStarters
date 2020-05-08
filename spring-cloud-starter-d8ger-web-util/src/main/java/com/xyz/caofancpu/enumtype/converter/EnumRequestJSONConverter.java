package com.xyz.caofancpu.enumtype.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.xyz.caofancpu.constant.IEnum;
import com.xyz.caofancpu.result.GlobalErrorInfoRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Objects;

/**
 * 请求字段对应枚举类型序列化处理器
 * 如果发现是自定义IEnum的子类, 走自定义枚举转换: value -> name -> viewName
 * 否则, 走默认枚举转换: Enum.original() -> Enum.name(), 参见: StringToEnumIgnoringCaseConverterFactory
 *
 * @author D8GER
 */
@Slf4j
public class EnumRequestJSONConverter<E extends Enum<E>> extends JsonDeserializer<E> implements
        ContextualDeserializer {

    private Class<E> enumType;

    public EnumRequestJSONConverter() {
    }

    private EnumRequestJSONConverter(Class<E> enumType) {
        this.enumType = enumType;
    }

    @Override
    public E deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        String source = jsonParser.getText();
        if (StringUtils.isBlank(source)) {
            log.warn("请求传参枚举字段为空, 则目标枚举类[{}]转换为null", this.enumType.getSimpleName());
            return null;
        }
        return IEnum.class.isAssignableFrom(enumType) ? this.customEnumParse(enumType, source) : this.originEnumParse(enumType, source);
    }

    @Override
    @SuppressWarnings("unchecked")
    public JsonDeserializer<?> createContextual(DeserializationContext deserializationContext, BeanProperty beanProperty)
            throws JsonMappingException {
        Class<?> rawClass = deserializationContext.getContextualType().getRawClass();
        return new EnumRequestJSONConverter(rawClass);
    }

    /**
     * 转换顺序: value -> name -> viewName
     *
     * @param enumType
     * @param source
     * @return
     */
    private E customEnumParse(Class<E> enumType, String source) {
        Integer value = null;
        E resultEnum = null;
        try {
            value = Integer.parseInt(source);
        } catch (Exception e) {
            // do nothing
        }
        E[] enumConstants = enumType.getEnumConstants();
        for (E enumConstant : enumConstants) {
            if (!(enumConstant instanceof IEnum)) {
                continue;
            }
            IEnum temp = (IEnum) enumConstant;
            if ((Objects.nonNull(value) && value.equals(temp.getValue())
                    || source.equals(temp.getName())
                    || source.equals(temp.getViewName()))) {
                resultEnum = enumConstant;
            }
        }
        if (Objects.isNull(resultEnum)) {
            log.error("请求传参枚举转换错误, 原因: 传值[{}], 目标枚举类[{}]", source, this.enumType.getSimpleName());
            throw new GlobalErrorInfoRuntimeException("参数非法, [" + this.enumType.getSimpleName() + "]不存在枚举值[" + source + "]");
        }
        return resultEnum;
    }

    private E originEnumParse(Class<E> enumType, String source) {
        if (source.isEmpty()) {
            return null;
        }
        source = source.trim();
        try {
            return Enum.valueOf(enumType, source);
        } catch (Exception ex) {
            return findEnum(enumType, source);
        }
    }

    private E findEnum(Class<E> enumType, String source) {
        String name = getLettersAndDigits(source);
        for (E candidate : EnumSet.allOf(enumType)) {
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
