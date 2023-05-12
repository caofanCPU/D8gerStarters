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
import com.xyz.caofancpu.mvc.common.MappingJackson2HttpMessageConverterUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WEB配置
 * 可增加拦截器、异步/跨域支持
 * <p>
 * 注意： 1.@EnableWebMvc + implements WebMvcConfigurer
 * 2.extends WebMvcConfigurationSupport
 * 都会覆盖@EnableAutoConfiguration关于WebMvcAutoConfiguration的配置
 * 例如: 请求参数时间格式/响应字段为NULL剔除
 * 因此, 推荐使用 implements WebMvcConfigurer方式, 保留原有配置
 * 3.自定义消息转换器, 推荐直接使用注册Bean
 * 也可使用复写extendMessageConverters()方法,
 * 但是注意: 不要使用configureMessageConverters, 该方法要么不起作用, 要么关闭了默认配置
 *
 * @author D8GER
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(name = D8gerConstants.D8_ENABLE, matchIfMissing = true)
@Slf4j
public class StandardHTTPMessageConfiguration implements WebMvcConfigurer {

    /**
     * 剔除响应对象中为NULL的字段
     * 请求枚举类转换: value -> name -> viewName
     * 响应枚举类转换: viewName -> name
     */
    @Bean(name = "customerMappingJackson2HttpMessageConverter")
    @ConditionalOnProperty(name = D8gerConstants.D8_HTTP_MESSAGE_CONVERT_ENABLE, matchIfMissing = true)
    public HttpMessageConverter customerMappingJackson2HttpMessageConverter() {
        log.info("D8GER....执行枚举请求&&响应转换器初始化");
        MappingJackson2HttpMessageConverter httpMessageConverter = MappingJackson2HttpMessageConverterUtil.build();
        log.info("D8GER....[customerMappingJackson2HttpMessageConverter]枚举请求&&响应转换器初始化完成!");
        return httpMessageConverter;
    }

}
