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

package com.xyz.caofancpu.property;

import com.xyz.caofancpu.constant.D8gerConstants;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * {@link ConfigurationProperties} for D8ger Web Util.
 *
 * @author D8GER
 */
@ConfigurationProperties(prefix = D8gerConstants.REST_TEMPLATE_PROPERTY_PREFIX)
@Validated
@Data
@Accessors(chain = true)
public class RestTemplateProperties {
    /**
     * 总的最大连接数, 默认200
     */
    private int maxTotal = 200;
    /**
     * 每个主机最大连接数, 默认20
     */
    private int maxPerRoute = 20;
    /**
     * 重新验证持久连接的闲置时间30s, 单位ms
     * 设置过小易导致复用到服务端断开的连接, 报错无响应
     */
    private int validateAfterInactivity = 30000;
    /**
     * 请求连接超时时间2s, 单位ms
     */
    private int connectionRequestTimeout = 2000;
    /**
     * 连接超时时间2s, 单位ms
     */
    private int connectTimeout = 2000;
    /**
     * socket超时时间4s, 单位ms
     */
    private int socketTimeout = 4000;
    /**
     * 连接最大空闲时间10s, 单位ms
     */
    private int maxIdleTime = 10000;

}
