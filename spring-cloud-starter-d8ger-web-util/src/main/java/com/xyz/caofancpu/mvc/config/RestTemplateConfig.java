package com.xyz.caofancpu.mvc.config;

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
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
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
 * FileName: RestTemplateConfig
 * Author:   caofanCPU
 * Date:     2018/11/17 16:54
 */
@Configuration
public class RestTemplateConfig {

    @Resource
    RestTemplateBuilder restTemplateBuilder;
    @Resource
    private CommonConfigValueService commonConfigValueService;

    /**
     * 注意：@LoadBalanced注解，使用该注解，则调用其他服务时，必须使用服务名称[http://SERVICE-XXX]，而非IP:port
     *
     * @return
     */
    @Bean(name = "restTemplate")
    @LoadBalanced
    RestTemplate restTemplate() {
        return restTemplateBuilder.build();
    }

    /**
     * 通过IP:port访问服务
     *
     * @return
     */
    @Bean(name = "zuulRestTemplate")
    RestTemplate zuulRestTemplate() {
        return restTemplateBuilder.build();
    }

    /**
     * 测试环境忽略https请求证书的问题
     *
     * @return
     */
    @Bean(name = "ignoreHttpsRestTemplate")
    RestTemplate ignoreHttpsRestTemplate()
            throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
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
        connectionManager.setDefaultMaxPerRoute(commonConfigValueService.maxPerRoute);
        connectionManager.setMaxTotal(commonConfigValueService.maxTotal);
        connectionManager.setValidateAfterInactivity(commonConfigValueService.validateAfterInactivity);
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setSSLSocketFactory(socketFactory)
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectionRequestTimeout(commonConfigValueService.connectionRequestTimeout)
                        .setConnectTimeout(commonConfigValueService.connectTimeout)
                        .setSocketTimeout(commonConfigValueService.socketTimeout)
                        .build()
                )
                .setConnectionManager(connectionManager).evictIdleConnections(commonConfigValueService.maxIdleTime, TimeUnit.SECONDS)
                .setConnectionManagerShared(true)
                .build();
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate;
    }
}
