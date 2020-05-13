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

import com.xyz.caofancpu.constant.SymbolConstantUtil;
import com.xyz.caofancpu.core.CollectionUtil;
import com.xyz.caofancpu.result.GlobalErrorInfoRuntimeException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

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
}
