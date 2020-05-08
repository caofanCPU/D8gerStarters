package com.xyz.caofancpu.mvc.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableSwaggerBootstrapUi;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

@Configuration
@EnableSwagger2
@EnableSwaggerBootstrapUi
public class Swagger2Config {

    /**
     * 需要认证的接口路径正则表达式
     */
    @Value("${swagger.authPathRegex}")
    private String authPathRegex;

    @Value("${swagger.authorizationKey}")
    private String authKey;

    /**
     * 是否展示API文档
     */
    @Value("${swagger.showApi}")
    private Boolean showApi;

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(showApi)
                .globalOperationParameters(setHeaderToken())
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                /**
                 * 需要token验证时
                 * .paths(PathSelectors.regex("^(?!auth).*$"))
                 */
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts())
                ;
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Swagger自动API文档")
                .description("利用Swagger-UI自动生成接口文档")
                .contact(new Contact("DebuggerKing", "https://github.com/caofanCPU", "2804238230@qq.com"))
                .version("1.0.0")
                .build();
    }

    private List<ApiKey> securitySchemes() {
        return Lists.newArrayList(new ApiKey(authKey, authKey, "header"));
    }

    private List<SecurityContext> securityContexts() {
        return Lists.newArrayList(SecurityContext.builder()
                .securityReferences(defaultAuth())
                .build());
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Lists.newArrayList(new SecurityReference(authKey, authorizationScopes));
    }

    private List<Parameter> setHeaderToken() {
        return Lists.newArrayList(
                new ParameterBuilder()
                        .name("X-Auth-Token")
                        .defaultValue("Authorization")
                        .description(authKey)
                        .modelRef(new ModelRef("string"))
                        .parameterType("header")
                        .required(false)
                        .build()
        );
    }

}
