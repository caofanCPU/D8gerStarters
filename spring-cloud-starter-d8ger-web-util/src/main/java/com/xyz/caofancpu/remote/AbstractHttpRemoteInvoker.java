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

import com.xyz.caofancpu.mvc.common.HttpStaticHandleUtil;
import com.xyz.caofancpu.result.GlobalErrorInfoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;

/**
 * HTTP远程过程调用RestTemplate工具类
 *
 * @author D8GER
 */
@Slf4j
public abstract class AbstractHttpRemoteInvoker implements IRestTemplateSupport {

    /**
     * 交给子类获取RestTemplate
     *
     * @return
     */
    public abstract RestTemplate loadRestTemplate();

    /**
     * 不同的远程调用, 域名可能不一致, 交给子类个性化
     *
     * @return
     */
    public abstract String loadDomain();

    /**
     * 最常用的请求
     * POST传Body
     *
     * @param req
     * @return
     * @throws GlobalErrorInfoException
     */
    public <T> T execute(AbstractD8BasicRemoteRequest<T> req)
            throws GlobalErrorInfoException {
        return doHttp(loadCompleteAccessUrl(req), loadHttpEntity(req), HttpMethod.POST, req.getRemoteResponseType());
    }

    /**
     * 一些老的应用, 可能使用{@link MediaType#APPLICATION_FORM_URLENCODED_VALUE}格式
     * 请求参数是拼在URL上的, 且参数需要使用{@link java.net.URLEncoder#encode(String, String)}编码
     *
     * @param req
     * @return
     * @throws GlobalErrorInfoException
     */
    public <T> T executeUrlEncode(AbstractD8BasicRemoteRequest<T> req)
            throws GlobalErrorInfoException {
        return doHttp(loadCompleteAccessUrl(req), loadUrlEncodeHttpEntity(req), HttpMethod.POST, req.getRemoteResponseType());
    }

    /**
     * 针对上传文件, 需要特殊处理
     *
     * @param req
     * @return
     * @throws GlobalErrorInfoException
     */
    public <T> T executeForHandleFile(AbstractD8BasicRemoteRequest<T> req, String fileKey, InputStreamSource attachment)
            throws GlobalErrorInfoException {
        Map<String, Object> paramMap = HttpStaticHandleUtil.extractNonNullParam(req);
        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        paramMap.forEach(multiValueMap::add);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        // 单独处理文件
        multiValueMap.add(fileKey, attachment);
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(multiValueMap, headers);
        return doHttp(loadCompleteAccessUrl(req), entity, HttpMethod.POST, req.getRemoteResponseType());
    }

    /**
     * 通用Http调用
     * 开放给子类, 能拓展其他方式的请求, 例如GET
     *
     * @param completeAccessUrl
     * @param entity
     * @param method
     * @return
     * @throws GlobalErrorInfoException
     */
    public <T> T doHttp(String completeAccessUrl, HttpEntity<?> entity, HttpMethod method, ParameterizedTypeReference<D8BasicRemoteResponse<T>> typeReference)
            throws GlobalErrorInfoException {
        D8BasicRemoteResponse<T> response;
        RestTemplate restTemplate = loadRestTemplate();
        try {
            log.info("远程调用开始");
            response = restTemplate.exchange(completeAccessUrl, method, entity, typeReference).getBody();
            log.info("远程调用结束");
        } catch (Throwable t) {
            log.error("远程调用异常", t);
            throw new GlobalErrorInfoException("远程调用异常");
        }
        if (Objects.isNull(response)) {
            throw new GlobalErrorInfoException("远程调用出错[空]");
        }
        if (isSuccess(response.getCode())) {
            throw new GlobalErrorInfoException(String.format("远程调用出错:%s", response.getMsg()));
        }
        return response.getData();
    }

    /**
     * 获取接口完整访问路径
     *
     * @param req
     * @return
     */
    private String loadCompleteAccessUrl(AbstractD8BasicRemoteRequest req) {
        return IRestTemplateSupport.super.loadCompleteAccessUrl(loadDomain(), req);
    }

}
