package com.xyz.caofancpu.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RUNTIME)
public @interface Check {

    /**
     * 字段校验规则
     * 格式：字段名+校验规则+冒号+错误信息
     * 例如：id<10:ID必须少于10
     *
     * @return
     */
    String[] value();

}
