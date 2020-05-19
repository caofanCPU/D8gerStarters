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
import com.xyz.caofancpu.constant.IEnum;
import com.xyz.caofancpu.constant.SymbolConstantUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        String nonWhiteCharStr = VerbalExpressionUtil.cleanJSONWhiteChar(source);
        int level = 0;
        StringBuilder resultBuilder = new StringBuilder();
        // 循环遍历每一个字符
        int doubleQuoteCount = 0;
        for (int i = 0; i < nonWhiteCharStr.length(); i++) {
            // 获取当前字符
            char piece = nonWhiteCharStr.charAt(i);
            // 如果上一个字符是断行, 则在本行开始按照level数值添加标记符, 排除第一行
            if (i != 0 && '\n' == resultBuilder.charAt(resultBuilder.length() - 1)) {
                for (int k = 0; k < level; k++) {
                    resultBuilder.append(SymbolConstantUtil.TAB);
                }
            }

            if (piece == '"') {
                doubleQuoteCount++;
            }

            switch (piece) {
                case '{':
                case '[':
                    // 如果字符是{或者[, 则断行, level加1
                    resultBuilder.append(piece);
                    if (doubleQuoteCount % 2 == 0) {
                        resultBuilder.append(SymbolConstantUtil.NEXT_LINE);
                        level++;
                    }
                    break;
                case ',':
                    resultBuilder.append(piece);
                    // 如果是",", 则断行
                    if (doubleQuoteCount % 2 == 0) {
                        resultBuilder.append(SymbolConstantUtil.NEXT_LINE);
                    }
                    break;
                case '}':
                case ']':
                    // 如果是"}"或者"]", 则断行, level减1
                    if (doubleQuoteCount % 2 == 0) {
                        resultBuilder.append(SymbolConstantUtil.NEXT_LINE);
                        level--;
                        for (int k = 0; k < level; k++) {
                            resultBuilder.append(SymbolConstantUtil.TAB);
                        }
                    }
                    resultBuilder.append(piece);
                    break;
                case ':':
                    // 增加单个空格美化显示
                    resultBuilder.append(piece).append(SymbolConstantUtil.SPACE);
                    break;
                default:
                    resultBuilder.append(piece);
                    break;
            }
        }
        return VerbalExpressionUtil.beautyNextLine(resultBuilder.toString());
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
    public static Object deserializeJSON(String jsonStr, Class<? extends Serializable> clazz) {
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
        return (T) deserializeJSON(serializeJSON(t), t.getClass());
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
     *
     * @return
     */
    public static <T> List<T> convertToList(String jsonArrayText, Class<T> clazz) {
        return JSONObject.parseArray(jsonArrayText, clazz);
    }
}
