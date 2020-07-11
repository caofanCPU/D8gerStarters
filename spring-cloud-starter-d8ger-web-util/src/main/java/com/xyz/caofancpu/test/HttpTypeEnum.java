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

package com.xyz.caofancpu.test;

import com.xyz.caofancpu.constant.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Http请求方式枚举
 *
 * @author D8GER
 */
@AllArgsConstructor
public enum HttpTypeEnum implements IEnum {
    POST_BODY(0, "Post传对象"),
    POST_PARAM(1, "Post传参数"),
    GET_PARAM(2, "Get传参数"),

    ;

    @Getter
    private final Integer value;

    @Getter
    private final String name;

}
