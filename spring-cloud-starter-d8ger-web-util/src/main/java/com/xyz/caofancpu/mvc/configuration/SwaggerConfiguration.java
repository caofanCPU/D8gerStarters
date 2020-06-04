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

import com.github.xiaoymin.knife4j.spring.annotations.EnableSwaggerBootstrapUi;
import com.google.common.collect.Lists;
import com.xyz.caofancpu.annotation.AttentionDoc;
import com.xyz.caofancpu.constant.D8gerConstants;
import com.xyz.caofancpu.core.CollectionUtil;
import com.xyz.caofancpu.property.SwaggerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.Resource;
import java.util.List;

/**
 * Swagger配置
 *
 * @author D8GER
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(name = D8gerConstants.D8_ENABLE, matchIfMissing = true)
@EnableConfigurationProperties(SwaggerProperties.class)
@EnableSwagger2
@EnableSwaggerBootstrapUi
@Slf4j
public class SwaggerConfiguration {

    @Resource
    private SwaggerProperties swaggerProperties;

    /**
     * 创建Swagger
     *
     * @return
     */
    @Bean(name = "swaggerDocket")
    @ConditionalOnProperty(name = D8gerConstants.D8_SWAGGER_ENABLE, matchIfMissing = true)
    @ConditionalOnMissingBean(value = Docket.class)
    @AttentionDoc("当容器中不存在Docket才执行创建")
    public Docket swaggerDocket() {
        log.info("D8GER....执行SwaggerApi初始化");
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .enable(swaggerProperties.isShowApi())
                .globalOperationParameters(setDefaultHeaderParameter())
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .paths(PathSelectors.any())
                .build();
        log.info("D8GER....[swaggerDocket]初始化完成!");
        return docket;
    }

    /**
     * 文档主页信息
     *
     * @return
     */
    private ApiInfo apiInfo() {
        SwaggerProperties.D8Project project = swaggerProperties.getProject();
        return new ApiInfoBuilder()
                .title(project.getTitle())
                .description(project.getDescription())
                .contact(new Contact(project.getContactName(), project.getContactUrl(), project.getContactEMail()))
                .version(project.getVersion())
                .build();
    }

    /**
     * 统一设置请求头
     *
     * @return
     */
    private List<Parameter> setDefaultHeaderParameter() {
        if (!swaggerProperties.isShowApi() || CollectionUtil.isEmpty(swaggerProperties.getHeaderParameters())) {
            return Lists.newArrayList();
        }
        return CollectionUtil.transToList(swaggerProperties.getHeaderParameters(),
                headerParameter -> new ParameterBuilder()
                        .name(headerParameter.getName())
                        .defaultValue(headerParameter.getDefaultValue())
                        .description(headerParameter.getDescription())
                        .modelRef(new ModelRef(headerParameter.getTypeShortName()))
                        .parameterType("header")
                        .required(headerParameter.isRequired())
                        .build()
        );
    }

}
