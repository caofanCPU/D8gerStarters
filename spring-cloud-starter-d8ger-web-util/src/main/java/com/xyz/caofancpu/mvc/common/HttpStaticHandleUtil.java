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

package com.xyz.caofancpu.mvc.common;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.common.collect.Maps;
import com.xyz.caofancpu.constant.SymbolConstantUtil;
import com.xyz.caofancpu.core.CollectionUtil;
import com.xyz.caofancpu.result.GlobalErrorInfoRuntimeException;
import org.springframework.core.io.InputStreamSource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.stream.Stream;

/**
 * HTTP属性处理工具
 *
 * @author D8GER
 */
public class HttpStaticHandleUtil {

    /**
     * 转换HTTP请求参数为字符串
     *
     * @param request
     * @return
     */
    public static Map<String, Object> getParameterMap(HttpServletRequest request) {
        Map<String, String[]> properties = request.getParameterMap();
        return CollectionUtil.transToMap(properties.entrySet(),
                Map.Entry::getKey,
                entry -> Objects.isNull(entry.getValue()) ? SymbolConstantUtil.EMPTY : entry.getValue()[0]
        );
    }

    /**
     * 加载请求对象
     *
     * @return
     * @throws GlobalErrorInfoRuntimeException
     */
    public static HttpServletRequest loadRequest()
            throws GlobalErrorInfoRuntimeException {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.nonNull(attributes)) {
            return attributes.getRequest();
        }
        throw new GlobalErrorInfoRuntimeException("无法解析请求信息: ServletRequestAttributes居然为空!");
    }

    /**
     * 从req中提取请求参数
     * 剔除null字段, 剔除文件类型字段
     *
     * @param req
     * @return
     */
    public static <T> Map<String, Object> extractNonNullParam(T req) {
        Stack<Class> classStack = new Stack<>();
        Class clazz = req.getClass();
        while (clazz != Object.class) {
            classStack.push(clazz);
            clazz = clazz.getSuperclass();
        }

        Map<String, Object> resultMap = Maps.newTreeMap();
        classStack.forEach(clazzItem -> {
            Field[] fields = clazzItem.getDeclaredFields();
            Stream.of(fields).forEach(field -> {
                Object value = null;
                try {
                    field.setAccessible(true);
                    value = field.get(req);
                } catch (IllegalAccessException e) {
                    // just ignore
                }
                if (Objects.isNull(value)) {
                    return;
                }
                try {
                    JSONField annotation = field.getAnnotation(JSONField.class);
                    String fieldKey = Objects.nonNull(annotation) ? annotation.name() : field.getName();
                    resultMap.put(fieldKey, value);
                } catch (Throwable t) {
                    // just ignore
                }
            });
        });
        // 对于文件类型字段, 直接移除, 交由后续流程特殊处理
        return CollectionUtil.removeSpecifiedElement(resultMap, new Class[]{File.class, InputStreamSource.class});
    }
}
