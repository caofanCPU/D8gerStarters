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

package com.xyz.caofancpu.mvc.test;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.xyz.caofancpu.constant.HttpTypeEnum;
import com.xyz.caofancpu.core.JSONUtil;
import com.xyz.caofancpu.extra.NormalUseForTestUtil;
import com.xyz.caofancpu.property.SpringConfigProperties;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 用来创建测试环境
 * 提供POST请求 @RequestBody 传对象
 * 提供POST请求 @RequestParam 传参数
 * 提供 Get请求 @RequestParam 传参数
 * 将接口结果JSON化打印
 * TODO: support custom
 *
 * @author D8GER
 */
@Component
@Slf4j
public class SpringBootJunitTestUtil {

    private MockMvc mvc;

    @Resource
    private WebApplicationContext context;

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private SpringConfigProperties springConfigProperties;


    /**
     * 默认就执行
     * 设置context
     */
    @PostConstruct
    public void setupMockMvc() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    /**
     * @param requestData  请求数据: @RequestParam || @RequestBody
     * @param url          接口API地址(不要域名和端口的URI信息)
     * @param httpHeaders  请求头
     * @param httpTypeEnum 请求类型
     * @param cookies      请求携带的cookies
     * @return
     * @throws Exception
     */
    public void execute(Object requestData, String url, HttpHeaders httpHeaders, HttpTypeEnum httpTypeEnum, Cookie... cookies)
            throws Exception {
        doHttp(requestData, url, httpHeaders, httpTypeEnum, cookies);
    }

    /**
     * @param requestData  请求数据: @RequestParam || @RequestBody
     * @param url          接口API地址(不要域名和端口的URI信息)
     * @param httpHeaders  请求头
     * @param httpTypeEnum 请求类型
     * @param cookies      请求携带的cookies
     * @return
     * @throws Exception
     */
    public String doHttp(Object requestData, String url, HttpHeaders httpHeaders, HttpTypeEnum httpTypeEnum, Cookie... cookies)
            throws Exception {
        return mvc.perform(loadMockHttpServletRequestBuilder(requestData, url, httpHeaders, httpTypeEnum, cookies))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    private MockHttpServletRequestBuilder loadMockHttpServletRequestBuilder(Object requestData, String url, HttpHeaders httpHeaders, HttpTypeEnum httpTypeEnum, Cookie... cookies)
            throws JsonProcessingException {
        switch (httpTypeEnum) {
            case POST_BODY:
                // 模拟POST发送RequestBody请求
                return MockMvcRequestBuilders.post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(httpHeaders)
                        .cookie(cookies)
                        .content(new ObjectMapper().writeValueAsString(requestData));
            case POST_PARAM:
                // 模拟POST发送RequestParam请求
                MultiValueMap<String, String> multiValueMap = convertRequestParam(JSONObject.parseObject(JSONObject.toJSONString(requestData)));
                return MockMvcRequestBuilders.post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(httpHeaders)
                        .cookie(cookies)
                        .params(multiValueMap);
            case GET_PARAM:
                // 模拟GET发送RequestParam请求
                multiValueMap = convertRequestParam(JSONObject.parseObject(JSONObject.toJSONString(requestData)));
                return MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(httpHeaders)
                        .cookie(cookies)
                        .params(multiValueMap);
            default:
                throw new RuntimeException("暂不支持的Http请求方式: " + httpTypeEnum.getName());
        }
    }

    public HttpHeaders generateRequestHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
        return headers;
    }

    /**
     * 构建MockHttpServletRequest类型的cookie
     *
     * @return
     */
    public Cookie[] buildMockHttpServletRequestCookie() {
        return Lists.newArrayList(new Cookie("Cookie", "empty")).toArray(new Cookie[0]);
    }

    /**
     * 上传文件, 将文件上传至其他系统
     *
     * @param file        要上传的文件
     * @param httpHeaders 请求头
     * @return
     * @throws Exception
     */
    public String executeUploadFileToOtherSystem(@NonNull File file, HttpHeaders httpHeaders)
            throws Exception {
        if (!file.exists() || !file.isFile()) {
            throw new RuntimeException("文件不存在, 请检查: " + file.getAbsolutePath());
        }
        // 上传文件固定参数, 文件必须用FileSystemResource包装起来, 否则报400错误
        FileSystemResource fileSystemResource = new FileSystemResource(file);
        MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("fileUsageType", 3);
        paramMap.add("file", fileSystemResource);

        // 上传文件固定文件类型
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(paramMap, httpHeaders);
        ResponseEntity<JSONObject> uploadFileResponseEntity = restTemplate.postForEntity(springConfigProperties.fileAccessUrl + "/api/upload/file", requestEntity, JSONObject.class);
        JSONObject responseJSONObject = uploadFileResponseEntity.getBody();
        if (Objects.isNull(responseJSONObject)) {
            throw new RuntimeException("上传文件出错, 响应为NULL");
        }
        NormalUseForTestUtil.out(JSONUtil.formatStandardJSON(responseJSONObject.toJSONString()));
        // 返回文件key
        return JSONObject.parseObject(JSONObject.toJSONString(responseJSONObject.get("data"))).get("fileKey").toString();
    }

    /**
     * 上传文件, 注意使用该方法的前提是当前系统本身具有上传文件的接口
     *
     * @param mockMultipartFileList 要上传的文件
     * @param httpHeaders           请求头
     * @return
     * @throws Exception
     */
    @Deprecated
    public String executeUploadFileToSelfSystem(List<MockMultipartFile> mockMultipartFileList, HttpHeaders httpHeaders)
            throws Exception {
        mockMultipartFileList.forEach(mockMultipartFile -> {
            if (mockMultipartFile.isEmpty()) {
                throw new RuntimeException("上传文件为空, 请检查: " + mockMultipartFile.getOriginalFilename());
            }
        });

        Map<String, Object> paramMap = new HashMap<>(2, 0.5f);
        paramMap.put("fileUsageType", 3);
        Cookie[] cookies = buildMockHttpServletRequestCookie();
        // 模拟POST发送RequestParam请求
        MultiValueMap<String, String> multiValueMap = convertRequestParam(JSONObject.parseObject(JSONObject.toJSONString(paramMap)));

        // 多个文件上传
        MockMultipartHttpServletRequestBuilder mockMultipartHttpServletRequestBuilder = MockMvcRequestBuilders.multipart("/multiUpload");
        mockMultipartFileList.forEach(mockMultipartHttpServletRequestBuilder::file);
        MockHttpServletRequestBuilder fileUploadMockHttpServletRequestBuilder = mockMultipartHttpServletRequestBuilder.headers(httpHeaders)
                .cookie(cookies)
                .params(multiValueMap);
        String responseJsonString = mvc.perform(fileUploadMockHttpServletRequestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        NormalUseForTestUtil.out(JSONUtil.formatStandardJSON(responseJsonString));
        return responseJsonString;
    }

    /**
     * 下载文件, 注意使用该方法的前提是当前系统本身具有下载文件的接口
     *
     * @param fileName    要下载的文件
     * @param httpHeaders 请求头
     * @return
     * @throws Exception
     */
    @Deprecated
    public void executeDownloadFileFromSelfSystem(String fileName, HttpHeaders httpHeaders)
            throws Exception {
        Map<String, Object> paramMap = new HashMap<>(2, 0.5f);
        paramMap.put("fileName", fileName);
        Cookie[] cookies = buildMockHttpServletRequestCookie();
        // 模拟GET发送RequestParam请求
        MultiValueMap<String, String> multiValueMap = convertRequestParam(JSONObject.parseObject(JSONObject.toJSONString(paramMap)));
        MockHttpServletRequestBuilder fileDownloadMockHttpServletRequestBuilder = MockMvcRequestBuilders.get("/download")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .headers(httpHeaders)
                .cookie(cookies)
                .params(multiValueMap);
        MockHttpServletResponse mockHttpServletResponse = mvc.perform(fileDownloadMockHttpServletRequestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse();
        byte[] fileContentByteArray = mockHttpServletResponse.getContentAsByteArray();
        if (fileContentByteArray.length == 0) {
            log.error("下载文件内容为空, 请检查!");
        } else {
            IOUtils.write(fileContentByteArray, new FileOutputStream(springConfigProperties.localOSSDownloadRoot + File.separator + fileName));
            log.info("文件下载已完成, 请查看文件路径: {}", springConfigProperties.localOSSDownloadRoot + File.separator + fileName);
        }
    }

    /**
     * 下载文件, 从其他系统下载文件
     *
     * @param fileKey     要下载的文件
     * @param httpHeaders 请求头
     * @return
     * @throws Exception
     */
    public void executeDownloadFileFromOtherSystem(String fileKey, HttpHeaders httpHeaders)
            throws Exception {
        HttpEntity<Map> requestEntity = new HttpEntity<>(httpHeaders);
        String requestUrl = springConfigProperties.fileAccessUrl + "/api/download/file" + "?fileKey=" + fileKey;
        ResponseEntity<byte[]> downloadFileResponseEntity = restTemplate.exchange(requestUrl, HttpMethod.GET, requestEntity, byte[].class);
        byte[] fileContentByteArray = downloadFileResponseEntity.getBody();
        if (fileContentByteArray == null || fileContentByteArray.length == 0) {
            log.error("下载文件内容为空, 请检查!");
        } else {
            String fileName = springConfigProperties.localOSSDownloadRoot + File.separator + fileKey;
            IOUtils.write(fileContentByteArray, new FileOutputStream(fileName));
            log.info("文件下载已完成, 请查看文件路径: {}", fileName);
        }
    }

    /**
     * SpringBoot模拟Rest请求时,传参数时需接收 MultiValueMap
     * 该方法只做转换, 将Map转为MultiValueMap
     *
     * @param paramMap
     * @return
     */
    private MultiValueMap<String, String> convertRequestParam(@NonNull Map<String, Object> paramMap) {
        MultiValueMap<String, String> convertResult = new LinkedMultiValueMap<>();
        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
            if (Objects.isNull(entry.getValue())) {
                continue;
            }
            convertResult.put(entry.getKey(), Lists.newArrayList(entry.getValue().toString()));
        }
        return convertResult;
    }

}
