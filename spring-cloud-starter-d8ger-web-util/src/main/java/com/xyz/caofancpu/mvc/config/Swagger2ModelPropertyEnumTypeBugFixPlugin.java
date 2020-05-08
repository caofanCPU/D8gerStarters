package com.xyz.caofancpu.mvc.config;

import com.xyz.caofancpu.constant.IEnum;
import com.xyz.caofancpu.core.CollectionUtil;
import com.xyz.caofancpu.core.JSONUtil;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.schema.Annotations;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;
import springfox.documentation.swagger.schema.ApiModelProperties;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


/**
 * Swagger2枚举类对象属性解析插件
 *
 * @author D8GER
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 98)
public class Swagger2ModelPropertyEnumTypeBugFixPlugin implements ModelPropertyBuilderPlugin {

    @Override
    public void apply(ModelPropertyContext context) {
        Optional<ApiModelProperty> annotation = Optional.empty();

        if (context.getAnnotatedElement().isPresent()) {
            annotation = Optional.of(annotation.orElseGet(ApiModelProperties.findApiModePropertyAnnotation(context.getAnnotatedElement().get())::get));
        }
        if (context.getBeanPropertyDefinition().isPresent()) {
            annotation = Optional.of(annotation.orElseGet(Annotations.findPropertyAnnotation(context.getBeanPropertyDefinition().get(), ApiModelProperty.class)::get));
        }
        final Class<?> rawPrimaryType = context.getBeanPropertyDefinition().get().getRawPrimaryType();
        // 过滤得到目标类型
        if (annotation.isPresent() && IEnum.class.isAssignableFrom(rawPrimaryType)) {
            IEnum[] values = (IEnum[]) rawPrimaryType.getEnumConstants();
            final List<String> displayValues = CollectionUtil.transToList(Arrays.asList(values), JSONUtil::serializeIEnumJSON);
            final AllowableListValues allowableListValues = new AllowableListValues(displayValues, rawPrimaryType.getTypeName());
//            final ResolvedType resolvedType = context.getResolver().resolve(int.class);
            context.getBuilder().allowableValues(allowableListValues);
        }
    }

    @Override
    public boolean supports(DocumentationType documentationType) {
        return true;
    }
}
