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
