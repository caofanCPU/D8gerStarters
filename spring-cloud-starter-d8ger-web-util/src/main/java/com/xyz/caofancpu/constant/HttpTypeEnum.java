package com.xyz.caofancpu.constant;

/**
 * Http请求方式枚举
 *
 * @author D8GER
 */
public enum HttpTypeEnum implements IEnum {
    POST_BODY(0, "Post传对象"),
    POST_PARAM(1, "Post传参数"),
    GET_PARAM(2, "Get传参数"),

    ;

    private final int value;

    private final String name;

    HttpTypeEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
