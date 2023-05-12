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
import com.xyz.caofancpu.core.CollectionFunUtil;
import com.xyz.caofancpu.logger.LoggerUtil;
import com.xyz.caofancpu.result.GlobalErrorInfoRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.core.io.InputStreamSource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.stream.Stream;

/**
 * HTTP属性处理工具
 *
 * @author D8GER
 */
@Slf4j
public class HttpStaticHandleUtil {

    /**
     * 转换HTTP请求参数为字符串
     *
     * @param request
     * @return
     */
    public static Map<String, Object> getParameterMap(HttpServletRequest request) {
        Map<String, String[]> properties = request.getParameterMap();
        return CollectionFunUtil.transToMap(properties.entrySet(),
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
        return CollectionFunUtil.removeSpecifiedElement(resultMap, new Class[]{File.class, InputStreamSource.class});
    }

    /**
     * 下载文件
     */
    public static void downloadExcel(HttpServletRequest request, HttpServletResponse response, String filePath) {
        LoggerUtil.info(log, "下载excel开始", "filePath", filePath);
        File file = new File(filePath);
        try (OutputStream out = response.getOutputStream(); BufferedInputStream input = new BufferedInputStream(new FileInputStream(file))) {
            response.reset();
            response.setHeader("Content-Disposition", "attachment;fileName=" + encodeFilename(request, file.getName()));
            response.addHeader("Content-Length", "" + file.length());
            response.setContentType("application/octet-stream;charset=UTF-8");
            IOUtils.copy(input, out);
            out.flush();
        } catch (IOException e) {
            LoggerUtil.error(log, "下载excel发生错误", e, "filePath", filePath);
        }
    }

    public static void downloadExcel(HttpServletRequest request, HttpServletResponse response, Workbook wb, String fileName) {
        LoggerUtil.info(log, "下载excel开始", "fileName", fileName);
        response.setHeader("Content-Disposition", "attachment;fileName=" + encodeFilename(request, fileName));
        response.setHeader("Pragma", "No-cache");
        response.setContentType("application/vnd.ms-excel");
        try (OutputStream outputStream = response.getOutputStream()) {
            wb.write(outputStream);
            outputStream.flush();
        } catch (IOException e) {
            LoggerUtil.error(log, "下载excel发生错误", e, "fileName", fileName);
        }
    }


    /**
     * 设置下载文件中文件的名称
     *
     * @param fileName
     * @return
     */
    public static String encodeFilename(HttpServletRequest request, String fileName) {
        String agent = request.getHeader("USER-AGENT");
        if (agent == null) {
            return fileName;
        }
        try {
            if (agent.contains("Trident") || agent.contains("MSIE")) {
                // IE
                fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
            } else if (agent.contains("Firefox")) {
                // Firefox
                fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
            } else if (agent.contains("Chrome")) {
                fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
            } else if (agent.contains("Safari")) {
                // Safari
                fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
            } else {
                fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
            }
        } catch (UnsupportedEncodingException ignored) {
        }
        return fileName;
    }
}
