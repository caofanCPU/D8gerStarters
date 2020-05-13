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

/**
 * @author D8GER
 */
public final class D8gerConstants {

    /**
     * 所有配置开关, 默认true打开
     */
    public static final String D8_ENABLE = "spring.cloud.d8ger.enable";

    /**
     * HTTP请求响应枚举转换, 时间格式转换器开关, 默认true打开
     */
    public static final String D8_HTTP_MESSAGE_CONVERT_ENABLE = "spring.cloud.d8ger.http-message-converter.enable";

    /**
     * RestTemplate配置开关, 默认true打开
     */
    public static final String D8_REST_TEMPLATE_ENABLE = "spring.cloud.d8ger.rest-template.enable";

    /**
     * SwaggerApi文档开关, 默认true打开
     */
    public static final String D8_SWAGGER_ENABLE = "spring.cloud.d8ger.rest-template.enable";

    /**
     * 业务线程池开关, 默认true打开
     */
    public static final String D8_BUSINESS_POOL_ENABLE = "spring.cloud.d8ger.business-pool.enable";

    public static final String REST_TEMPLATE_PROPERTY_PREFIX = "spring.cloud.d8ger.rest-template";

    public static final String SWAGGER_PROPERTY_PREFIX = "spring.cloud.d8ger.swagger";

    public static final String BUSINESS_POOL_PROPERTY_PREFIX = "spring.cloud.d8ger.business-pool";

    private D8gerConstants() {
        throw new AssertionError("Must not instantiate constant utility class");
    }

}
