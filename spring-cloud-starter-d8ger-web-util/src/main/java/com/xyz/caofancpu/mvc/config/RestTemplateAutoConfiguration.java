package com.xyz.caofancpu.mvc.config;

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
 * RestTemplate统一配置
 *
 * @author D8GER
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(name = "spring.cloud.d8ger.enabled", matchIfMissing = true)
@EnableConfigurationProperties(RestTemplateProperties.class)
@Slf4j
public class RestTemplateAutoConfiguration {

    @Resource
    RestTemplateBuilder restTemplateBuilder;
    @Resource
    private RestTemplateProperties restTemplateProperties;

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
                .setConnectionManager(connectionManager).evictIdleConnections(restTemplateProperties.getMaxIdleTime(), TimeUnit.SECONDS)
                .setConnectionManagerShared(true)
                .build();
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate;
    }
}
