package com.xyz.caofancpu.mvc.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.google.common.collect.Lists;
import com.xyz.caofancpu.core.DateUtil;
import com.xyz.caofancpu.enumtype.AutoHttpEnumConvertUtil;
import org.springframework.boot.jackson.JsonComponentModule;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * 自定义消息转换器工具类
 * 1.过滤null字段
 * 2.自定义响应枚举转换 + 请求枚举转换
 * 3.时间处理(包含时区): "2019-12-31 13:14:15"
 * 当前限定只支持LocalXXX, 首推LocalDateTime
 * 也可考虑使用Instant
 * 4.jackson底层默认UTF8编码
 *
 * @author D8GER
 */
public class MappingJackson2HttpMessageConverterUtil {

    @SuppressWarnings("unchecked")
    public static MappingJackson2HttpMessageConverter build() {
        return new MappingJackson2HttpMessageConverter(
                Jackson2ObjectMapperBuilder
                        .json()
                        .serializationInclusion(JsonInclude.Include.NON_NULL)
                        .modules(Lists.newArrayList(
                                new Jdk8Module(),
                                new ParameterNamesModule(),
                                new JavaTimeModule()
                                        .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DateUtil.DATETIME_FORMAT_SIMPLE)))
                                        .addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DateUtil.DATE_FORMAT_SIMPLE)))
                                        .addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(DateUtil.TIME_FORMAT_SIMPLE)))
                                        .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DateUtil.DATETIME_FORMAT_SIMPLE)))
                                        .addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DateUtil.DATE_FORMAT_SIMPLE)))
                                        .addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DateUtil.TIME_FORMAT_SIMPLE))),
                                // 请求枚举类型的反序列化转换器 + 响应枚举类型的序列化转换器
                                new SimpleModule().addDeserializer(Enum.class, AutoHttpEnumConvertUtil.buildRequestJSONConverter())
                                        .addSerializer(Enum.class, AutoHttpEnumConvertUtil.buildResponseJSONConverter()),
                                new JsonComponentModule()
                                )
                        )
                        .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                        .featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                        .featuresToEnable(MapperFeature.PROPAGATE_TRANSIENT_MARKER)
                        .build()
                        .setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()))
        );
    }
}
