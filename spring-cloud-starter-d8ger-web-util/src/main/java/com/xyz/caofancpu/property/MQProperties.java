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

import com.google.common.collect.Maps;
import com.xyz.caofancpu.annotation.WarnDoc;
import com.xyz.caofancpu.constant.D8gerConstants;
import com.xyz.caofancpu.constant.IEnum;
import com.xyz.caofancpu.constant.SymbolConstantUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

/**
 * {@link ConfigurationProperties} for D8ger Web Util.
 *
 * @author D8GER
 */
@ConfigurationProperties(prefix = D8gerConstants.MQ_PROPERTY_PREFIX)
@Validated
@Data
@Accessors(chain = true)
public class MQProperties {
    //========================公共========================//
    /**
     * 服务端地址
     */
    private String nameSrvAddr = SymbolConstantUtil.EMPTY;

    /**
     * 环境标识
     */
    private String mqEnv = "D8MqEnv";

    //========================生产者========================//
    /**
     * 生产者所属组名，根据应用区分
     */
    private String producerGroup = "D8ProducerG";

    /**
     * 每个Topic下队列数量, 默认4
     */
    private Integer producerTopicQueueNums = 4;

    /**
     * 发送失败回调时进行重试, 默认启用
     */
    private boolean producerRetryOnSendFailedCallback = true;

    /**
     * 发送失败重试次数, 默认3次
     */
    private Integer producerRetryTimesWhenSendFailed = 3;

    /**
     * 默认延时级别, 30秒
     */
    private DelayLevelEnum producerDefaultDelayLevel = DelayLevelEnum.DELAY_THIRTY_SECONDS;

    /**
     * 异步发送消息超时阈值, 默认5秒
     */
    private int producerAsynTimeOutMillis = 5000;

    /**
     * 同步发送消息超时阈值, 默认1秒
     */
    private int producerSynTimeOutMillis = 1000;

    /**
     * MQ日志文件, 默认MQ_LOG
     */
    private String producerMqLogAppenderName = "MQ_LOG";

    /**
     * 消费者订阅主题合集
     */
    private Map<String, String> producerTopicTagMap = Maps.newHashMap();

    //========================消费者========================//
    /**
     * 消费者所属组名，根据应用区分
     */
    private String consumerGroup = "D8ConsumerG";

    /**
     * 消费者订阅主题合集
     */
    private Map<String, String> consumerTopicTagMap = Maps.newHashMap();

    /**
     * 消费模式, 默认集群消费
     */
    @WarnDoc("建议不要使用广播消费, 即使需要也尽量建议在业务层予以统一处理")
    private MessageModel consumerMessageModel = MessageModel.CLUSTERING;

    /**
     * 消费线程最少数, 默认50
     */
    private int consumerMinThreads = 50;

    /**
     * 消费线程最大数, 默认50
     */
    private int consumerMaxThreads = 50;

    /**
     * 消费者处理最大超时时间, 默认15分钟
     */
    @WarnDoc("该字段配置会阻塞消费者线程, 应慎重配置")
    private int consumerMaxTimeOutInMinutes = 15;

    /**
     * 消费失败重试次数, 默认10次
     */
    private Integer consumerRetryTimesWhenHandleFailed = 10;

    /**
     * 析取主题, 移除 mqEnv + '_'
     *
     * @param topic
     * @return
     */
    public static String extractLogicTopic(String topic) {
        return StringUtils.isBlank(topic) ? topic : topic.substring(topic.indexOf(SymbolConstantUtil.ENGLISH_UNDER_JOINER) + 1);
    }

    /**
     * 包装Topic, 实际的主题格式为: mqEnv + '_' + topic
     *
     * @return
     */
    public String wrapperTopic(String topic) {
        return StringUtils.isBlank(topic) ? topic : this.mqEnv + SymbolConstantUtil.ENGLISH_UNDER_JOINER + topic;
    }

    /**
     * 检查mqEnv, 不允许空也不允许包含 '_'
     *
     * @return
     */
    public boolean isLegalMqEnvName() {
        return StringUtils.isBlank(this.mqEnv) || this.mqEnv.contains(SymbolConstantUtil.ENGLISH_UNDER_JOINER);
    }

    /**
     * 延时级别
     *
     * @author D8GER
     */
    @AllArgsConstructor
    public enum DelayLevelEnum implements IEnum {
        DELAY_ONE_SECOND(1, "1s"),
        DELAY_FIVE_SECONDS(2, "5s"),
        DELAY_TEN_SECONDS(3, "10s"),
        DELAY_THIRTY_SECONDS(4, "30s"),
        DELAY_ONE_MINUTE(5, "1m"),
        DELAY_TWO_MINUTES(6, "2m"),
        DELAY_THREE_MINUTES(7, "3m"),
        DELAY_FOUR_MINUTES(8, "4m"),
        DELAY_FIVE_MINUTES(9, "5m"),
        DELAY_SIX_MINUTES(10, "6m"),
        DELAY_SEVEN_MINUTES(11, "7m"),
        DELAY_EIGHT_MINUTES(12, "8m"),
        DELAY_NINE_MINUTES(13, "9m"),
        DELAY_TEN_MINUTES(14, "10m"),
        DELAY_TWENTY_MINUTES(15, "20m"),
        DELAY_THIRTY_MINUTES(16, "30m"),
        DELAY_ONE_HOUR(17, "1h"),
        DELAY_TWO_HOURS(18, "2h"),
        ;

        private final int value;

        private final String name;


        @Override
        public Integer getValue() {
            return this.value;
        }

        @Override
        public String getName() {
            return this.name;
        }
    }
}

