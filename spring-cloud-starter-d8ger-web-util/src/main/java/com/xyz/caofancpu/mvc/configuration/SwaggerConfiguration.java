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

import com.fasterxml.classmate.ResolvedType;
import com.github.xiaoymin.knife4j.spring.annotations.EnableSwaggerBootstrapUi;
import com.google.common.collect.Lists;
import com.xyz.caofancpu.annotation.AttentionDoc;
import com.xyz.caofancpu.constant.D8gerConstants;
import com.xyz.caofancpu.constant.IEnum;
import com.xyz.caofancpu.constant.SymbolConstantUtil;
import com.xyz.caofancpu.core.CollectionUtil;
import com.xyz.caofancpu.property.SwaggerProperties;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ModelPropertyBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.Annotations;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.schema.ApiModelProperties;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.Resource;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE + 98)
    public ModelPropertyBuilderPlugin builderPlugin() {
        return new Swagger2ModelPropertyPlugin();
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

    /**
     * Swagger2枚举类对象属性解析插件
     *
     * @author ht-caofan
     */
    private static class Swagger2ModelPropertyPlugin implements ModelPropertyBuilderPlugin {
        /**
         * 字段描述名称标识KEY
         */
        private static final String MODEL_DESCRIPTION_KEY = "description";

        /**
         * 校验规则标题
         */
        public static final String VALIDATE_TITLE_KEY = "校验规则: ";

        @Override
        public void apply(ModelPropertyContext context) {
            String fieldDescription = SymbolConstantUtil.EMPTY;
            try {
                Field mField = ModelPropertyBuilder.class.getDeclaredField(MODEL_DESCRIPTION_KEY);
                mField.setAccessible(true);
                fieldDescription += mField.get(context.getBuilder());
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            Optional<ApiModelProperty> annotation = Optional.empty();
            if (context.getAnnotatedElement().isPresent()) {
                annotation = Optional.of(annotation.orElseGet(ApiModelProperties.findApiModePropertyAnnotation(context.getAnnotatedElement().get())::get));
            }
            if (context.getBeanPropertyDefinition().isPresent()) {
                annotation = Optional.of(annotation.orElseGet(Annotations.findPropertyAnnotation(context.getBeanPropertyDefinition().get(), ApiModelProperty.class)::get));
            }
            final Class<?> rawPrimaryType = context.getBeanPropertyDefinition().get().getRawPrimaryType();
            // 处理枚举类型
            if (annotation.isPresent() && IEnum.class.isAssignableFrom(rawPrimaryType)) {
                List<String> displayValueList = CollectionUtil.transToList(Arrays.asList(rawPrimaryType.getEnumConstants()), item -> {
                    IEnum iEnum = (IEnum) item;
                    return iEnum.getValue() + SymbolConstantUtil.ENGLISH_COLON + iEnum.getName();
                });
                String enumDescription = SymbolConstantUtil.SPACE + SymbolConstantUtil.ENGLISH_LEFT_BRACKET + String.join(SymbolConstantUtil.ENGLISH_SEMICOLON + SymbolConstantUtil.SPACE, displayValueList) + SymbolConstantUtil.ENGLISH_RIGHT_BRACKET;
                fieldDescription += enumDescription;
                final ResolvedType resolvedType = context.getResolver().resolve(int.class);
                context.getBuilder().type(resolvedType);
            }
            String validateMessage = normalValidateMessage(context);
            if (StringUtils.isNotBlank(validateMessage)) {
                fieldDescription += SymbolConstantUtil.ERROR_SHOW + VALIDATE_TITLE_KEY + validateMessage + SymbolConstantUtil.ERROR_SHOW;
            }
            context.getBuilder().description(fieldDescription);
        }

        @SuppressWarnings("Guava")
        private String normalValidateMessage(ModelPropertyContext context) {
            // 处理校验注解
            List<String> validAnnotationMessageList = Lists.newArrayList();
            if (context.getBeanPropertyDefinition().isPresent()) {
                com.google.common.base.Optional<NotNull> notNull = Annotations.findPropertyAnnotation(context.getBeanPropertyDefinition().get(), NotNull.class);
                if (notNull.isPresent()) {
                    validAnnotationMessageList.add(notNull.get().message());
                }
                com.google.common.base.Optional<NotBlank> notBlank = Annotations.findPropertyAnnotation(context.getBeanPropertyDefinition().get(), NotBlank.class);
                if (notBlank.isPresent()) {
                    validAnnotationMessageList.add(notBlank.get().message());
                }
                com.google.common.base.Optional<NotEmpty> notEmpty = Annotations.findPropertyAnnotation(context.getBeanPropertyDefinition().get(), NotEmpty.class);
                if (notEmpty.isPresent()) {
                    validAnnotationMessageList.add(notEmpty.get().message());
                }
                com.google.common.base.Optional<Digits> digits = Annotations.findPropertyAnnotation(context.getBeanPropertyDefinition().get(), Digits.class);
                if (digits.isPresent()) {
                    validAnnotationMessageList.add(digits.get().message());
                }
                com.google.common.base.Optional<DecimalMax> decimalMax = Annotations.findPropertyAnnotation(context.getBeanPropertyDefinition().get(), DecimalMax.class);
                if (decimalMax.isPresent()) {
                    validAnnotationMessageList.add(decimalMax.get().message());
                }
                com.google.common.base.Optional<DecimalMin> decimalMin = Annotations.findPropertyAnnotation(context.getBeanPropertyDefinition().get(), DecimalMin.class);
                if (decimalMin.isPresent()) {
                    validAnnotationMessageList.add(decimalMin.get().message());
                }
                com.google.common.base.Optional<Min> min = Annotations.findPropertyAnnotation(context.getBeanPropertyDefinition().get(), Min.class);
                if (min.isPresent()) {
                    validAnnotationMessageList.add(min.get().message());
                }
                com.google.common.base.Optional<Max> max = Annotations.findPropertyAnnotation(context.getBeanPropertyDefinition().get(), Max.class);
                if (max.isPresent()) {
                    validAnnotationMessageList.add(max.get().message());
                }
                com.google.common.base.Optional<Size> size = Annotations.findPropertyAnnotation(context.getBeanPropertyDefinition().get(), Size.class);
                if (size.isPresent()) {
                    validAnnotationMessageList.add(size.get().message());
                }
            }
            return CollectionUtil.join(validAnnotationMessageList, SymbolConstantUtil.ENGLISH_SEMICOLON + SymbolConstantUtil.SPACE);
        }

        @Override
        public boolean supports(DocumentationType documentationType) {
            return true;
        }
    }

}
