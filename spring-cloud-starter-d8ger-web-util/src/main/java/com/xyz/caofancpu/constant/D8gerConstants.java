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

    /**
     * Redis开关, 默认true打开
     */
    public static final String D8_REDIS_ENABLE = "spring.cloud.d8ger.redis.enable";

    /**
     * Mail开关, 默认true打开
     */
    public static final String D8_MAIL_ENABLE = "spring.cloud.d8ger.mail.enable";

    /**
     * MQ开关, 默认true打开
     */
    public static final String D8_MQ_ENABLE = "spring.cloud.d8ger.mq.enable";

    /**
     * MQ生产者开关, 默认true打开
     */
    public static final String D8_MQ_PRODUCER_ENABLE = "spring.cloud.d8ger.mq.producer.enable";

    /**
     * MQ生产者发送回调开关, 默认true打开
     */
    public static final String D8_MQ_PRODUCER_SEND_CALLBACK_ENABLE = "spring.cloud.d8ger.mq.producer.default-send-callback.enable";

    /**
     * MQ消费者开关, 默认true打开
     */
    public static final String D8_MQ_CONSUMER_ENABLE = "spring.cloud.d8ger.mq.consumer.enable";

    /**
     * RestTemplate属性配置前缀
     */
    public static final String REST_TEMPLATE_PROPERTY_PREFIX = "spring.cloud.d8ger.rest-template";

    /**
     * Swagger属性配置前缀
     */
    public static final String SWAGGER_PROPERTY_PREFIX = "spring.cloud.d8ger.swagger";

    /**
     * 业务线程池属性配置前缀
     */
    public static final String BUSINESS_POOL_PROPERTY_PREFIX = "spring.cloud.d8ger.business-pool";

    /**
     * Redis属性配置前缀
     */
    public static final String REDIS_PROPERTY_PREFIX = "spring.cloud.d8ger.redis";

    /**
     * Mail属性配置前缀
     */
    public static final String MAIL_PROPERTY_PREFIX = "spring.cloud.d8ger.mail";

    /**
     * MQ属性配置前缀
     */
    public static final String MQ_PROPERTY_PREFIX = "spring.cloud.d8ger.mq";

    private D8gerConstants() {
        throw new AssertionError("Must not instantiate constant utility class");
    }

}
