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

package com.xyz.caofancpu.mvc.configuration;

import com.xyz.caofancpu.constant.D8gerConstants;
import com.xyz.caofancpu.property.RestTemplateProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

/**
 * RestTemplate统一配置
 *
 * @author D8GER
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(name = D8gerConstants.D8_ENABLE, matchIfMissing = true)
@EnableConfigurationProperties(RestTemplateProperties.class)
@Slf4j
public class RestTemplateConfiguration {

    @Resource
    private RestTemplateProperties restTemplateProperties;

    /**
     * 测试环境忽略https请求证书的问题
     *
     * @return
     */
    @Bean(name = "restTemplate")
    @ConditionalOnProperty(name = D8gerConstants.D8_REST_TEMPLATE_ENABLE, matchIfMissing = true)
    public RestTemplate restTemplate()
            throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        log.info("D8GER....执行RestTemplate初始化");
        // https
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
                new SSLContextBuilder()
                        .loadTrustMaterial(null, (X509Certificate[] x509Certificates, String s) -> true)
                        .build(),
                new String[]{"SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.2"},
                null,
                NoopHostnameVerifier.INSTANCE
        );
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", new PlainConnectionSocketFactory())
                .register("https", socketFactory)
                .build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
        connectionManager.setDefaultMaxPerRoute(restTemplateProperties.getMaxPerRoute());
        connectionManager.setMaxTotal(restTemplateProperties.getMaxTotal());
        connectionManager.setValidateAfterInactivity(restTemplateProperties.getValidateAfterInactivity());
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setSSLSocketFactory(socketFactory)
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectionRequestTimeout(restTemplateProperties.getConnectionRequestTimeout())
                        .setConnectTimeout(restTemplateProperties.getConnectTimeout())
                        .setSocketTimeout(restTemplateProperties.getSocketTimeout())
                        .build()
                )
                .setConnectionManager(connectionManager).evictIdleConnections(restTemplateProperties.getMaxIdleTime(), TimeUnit.MILLISECONDS)
                .setConnectionManagerShared(true)
                .build();
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        log.info("D8GER....[restTemplate]初始化完成");
        return restTemplate;
    }
}
