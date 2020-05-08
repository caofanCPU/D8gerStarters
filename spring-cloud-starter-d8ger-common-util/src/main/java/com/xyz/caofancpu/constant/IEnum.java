package com.xyz.caofancpu.constant;

import org.apache.commons.lang3.StringUtils;

/**
 * 枚举接口
 *
 * @author D8GER
 */
public interface IEnum {
    /**
     * 枚举值, key
     */
    String IENUM_VALUE_KEY = "value";
    /**
     * 枚举名称, key
     */
    String IENUM_NAME_KEY = "name";

    /**
     * 要存入数据库的类型值
     *
     * @return
     */
    Integer getValue();

    /**
     * 业务或代码上的类型名称(中文|英文)
     *
     * @return
     */
    String getName();

    /**
     * 类型别名, 可用于前端展示
     * 默认为空串
     *
     * @return
     */
    default String getViewName() {
        return StringUtils.EMPTY;
    }
}
