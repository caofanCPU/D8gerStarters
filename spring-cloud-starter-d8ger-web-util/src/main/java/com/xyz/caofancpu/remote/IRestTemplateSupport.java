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

package com.xyz.caofancpu.remote;

import com.xyz.caofancpu.constant.SymbolConstantUtil;
import com.xyz.caofancpu.core.CollectionUtil;
import com.xyz.caofancpu.mvc.common.HttpStaticHandleUtil;
import com.xyz.caofancpu.result.GlobalErrorInfoEnum;
import lombok.NonNull;
import org.apache.commons.codec.CharEncoding;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.validation.constraints.NotBlank;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Objects;

/**
 * HTTP远程过程调用接口
 *
 * @author D8GER
 */
public interface IRestTemplateSupport {

    default HttpEntity<String> loadUrlEncodeHttpEntity(@NonNull AbstractD8BasicRemoteRequest<?> req) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        return new HttpEntity<>(loadUrlEncodeRequestParam(req), httpHeaders);
    }

    default String loadUrlEncodeRequestParam(@NonNull AbstractD8BasicRemoteRequest<?> req) {
        Map<String, Object> nonNullParamMap = HttpStaticHandleUtil.extractNonNullParam(req);
        String originParamStr = CollectionUtil.join(CollectionUtil.transToList(nonNullParamMap.entrySet(), entry -> entry.getKey() + SymbolConstantUtil.EQUAL + entry.getValue()), SymbolConstantUtil.AND);
        try {
            return URLEncoder.encode(originParamStr, CharEncoding.UTF_8);
        } catch (UnsupportedEncodingException e) {
            // do nothing
            return originParamStr;
        }
    }

    default HttpEntity<Map<String, Object>> loadHttpEntity(@NonNull AbstractD8BasicRemoteRequest<?> req) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return new HttpEntity<>(HttpStaticHandleUtil.extractNonNullParam(req), httpHeaders);
    }

    default HttpEntity<Map<String, Object>> loadCustomHttpEntity(@NonNull AbstractD8BasicRemoteRequest<?> req, HttpHeaders httpHeaders) {
        if (Objects.isNull(httpHeaders)) {
            httpHeaders = new HttpHeaders();
        }
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return new HttpEntity<>(HttpStaticHandleUtil.extractNonNullParam(req), httpHeaders);
    }

    default String loadCompleteAccessUrl(@NotBlank final String domain, @NonNull AbstractD8BasicRemoteRequest<?> req) {
        return domain + File.separator + req.getAccessUri();
    }

    default boolean isSuccess(@NonNull String code) {
        return code.equals(GlobalErrorInfoEnum.SUCCESS.getCode()) || code.equals(GlobalErrorInfoEnum.REMOTE_INVOKE_SUCCESS.getCode());
    }

}
