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

package com.xyz.caofancpu.core;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.xyz.caofancpu.constant.IEnum;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * JSON工具类
 *
 * @author D8GER
 */
@Slf4j
public class JSONUtil {

    public static String toJSONStringWithDateFormat(Object data) {
        return JSONObject.toJSONStringWithDateFormat(data, DateUtil.DATETIME_FORMAT_SIMPLE);
    }

    public static String toJSONStringWithDateFormatAndEnumToString(Object data) {
        return JSONObject.toJSONStringWithDateFormat(data, DateUtil.DATETIME_FORMAT_SIMPLE, SerializerFeature.WriteEnumUsingToString);
    }

    /**
     * 对象的标准JSON字符串
     *
     * @param source
     * @return
     */
    public static String formatStandardJSON(@NonNull Object source) {
        return formatStandardJSON(toJSONStringWithDateFormatAndEnumToString(source));
    }

    /**
     * 格式化JSON字符串
     *
     * @param source
     * @return
     */
    public static String formatStandardJSON(@NonNull String source) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement element = JsonParser.parseString(source);
        return gson.toJson(element);
    }

    /**
     * 对象序列化为JSONString
     *
     * @param object
     * @return
     */
    public static <T extends Serializable> String serializeJSON(T object) {
        return JSONObject.toJSONString(object, SerializerFeature.WriteClassName);
    }

    /**
     * 对象序列化为JSONString
     *
     * @param object
     * @return
     */
    public static String serializeJSON(Object object) {
        return JSONObject.toJSONString(object, SerializerFeature.WriteClassName);
    }

    /**
     * 枚举IEnum子类序列化为JSON
     *
     * @return
     */
    public static String serializeIEnumJSON(IEnum iEnum) {
        Map<String, Object> resultMap = new LinkedHashMap<>(4, 0.5f);
        resultMap.put(IEnum.IENUM_VALUE_KEY, iEnum.getValue());
        String name = iEnum.getViewName();
        if (StringUtils.isBlank(name)) {
            name = iEnum.getName();
        }
        if (iEnum instanceof Enum && StringUtils.isBlank(name)) {
            name = ((Enum) iEnum).name();
        }
        resultMap.put(IEnum.IENUM_NAME_KEY, name);
        return JSONObject.toJSONString(resultMap);
    }

    /**
     * 反序列化对象
     *
     * @param jsonStr
     * @param clazz
     * @return
     */
    public static <T extends Serializable> T deserializeJSON(String jsonStr, Class<T> clazz) {
        return JSONObject.parseObject(jsonStr, clazz);
    }

    /**
     * 利用序列化深拷贝复杂对象
     *
     * @param t
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T deepCloneBySerialization(T t) {
        return deserializeJSON(serializeJSON(t), (Class<T>) t.getClass());
    }

    /**
     * 对象序列化为标准格式的JSONString
     *
     * @param object
     * @return
     */
    public static String serializeStandardJSON(Object object) {
        String jsonString = JSONObject.toJSONString(object, SerializerFeature.WriteClassName);
        return formatStandardJSON(jsonString);
    }

    /**
     * Object转List, 用Bean接收
     *
     * @param jsonArrayText
     * @param clazz
     * @return
     */
    public static <T> List<T> convertToList(String jsonArrayText, Class<T> clazz) {
        return JSONObject.parseArray(jsonArrayText, clazz);
    }

    /**
     * 复制源对象属性到目标类
     * 根据属性名称匹配, 支持map转bean, bean之间互转, bean中嵌套集合转化
     *
     * @param sourceObject
     * @param clazz
     * @param <T>
     * @return
     * @throws RuntimeException
     */
    public static <T> T copyProperties(Object sourceObject, Class<T> clazz) {
        if (Objects.isNull(sourceObject)) {
            log.error("属性复制, 源数据不能为空!");
            throw new IllegalArgumentException("源数据不能为空!");
        }
        if (Objects.isNull(clazz)) {
            log.error("属性复制, 目标类对象不能为空!");
            throw new IllegalArgumentException("目标类对象不能为空!");
        }
        return JSONObject.parseObject(JSONObject.toJSONString(sourceObject), clazz);
    }
}
