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

package com.xyz.caofancpu.mvc.standard.mq;

import com.xyz.caofancpu.annotation.WarnDoc;
import com.xyz.caofancpu.core.JSONUtil;
import com.xyz.caofancpu.logger.trace.ThreadTraceUtil;
import com.xyz.caofancpu.property.MQProperties;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * MQ生产者基类
 *
 * @author D8GER
 */
@Accessors(chain = true)
public class D8BaseProducer {
    /**
     * MQ配置
     */
    private final MQProperties mqProperties;

    /**
     * 成功回调处理
     */
    private final D8BaseSendCallback sendCallback;

    /**
     * 日志组件
     */
    private final Logger log;
    /**
     * 消息自增计数器
     */
    private final AtomicLong count = new AtomicLong(0);
    /**
     * 默认队列选择器
     */
    public MessageQueueSelector defaultQueueSelector;
    /**
     * 所有消息放在一个队列中, 保持顺序
     */
    @WarnDoc("慎用, 尽量把业务解耦, 不依赖顺序")
    public MessageQueueSelector orderedQueueSelectorZero;
    /**
     * 默认生产者, 源自RocketMQ
     */
    private DefaultMQProducer defaultMQProducer;

    public D8BaseProducer(MQProperties mqProperties, D8BaseSendCallback sendCallback) {
        if (mqProperties.isLegalMqEnvName()) {
            throw new RuntimeException("初始化producer失败, mqEnv不得为空也不得包含'_'");
        }
        this.mqProperties = mqProperties;
        this.log = LoggerFactory.getLogger(D8BaseProducer.class);
//        this.log = LoggerFactory.getLogger(this.mqProperties.getProducerMqLogAppenderName());
        this.sendCallback = sendCallback;
    }

    /**
     * 构造完成后初始化
     * * 测试环境：messageDelayLevel=1级对应1s 2级对应5s 3级对应10s 4级对应30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
     * * 线上环境：messageDelayLevel=1级对应 1s 2级对应5s 3级对应10s 依次类推30s 10m 20m 30m 1h 24h 24h 24h 24h 24h 24h 24h 24h 24h 24h
     * * 从1开始
     */
    @PostConstruct
    public void init()
            throws MQClientException {
        log.info("D8GER....执行初始化producer, producerGroup=[{}], nameSrvAddr=[{}]", mqProperties.getProducerGroup(), mqProperties.getNameSrvAddr());
        try {
            defaultMQProducer = new DefaultMQProducer(mqProperties.getMqEnv() + mqProperties.getProducerGroup());
            defaultMQProducer.setNamesrvAddr(mqProperties.getNameSrvAddr());
            defaultMQProducer.setRetryTimesWhenSendFailed(mqProperties.getProducerRetryTimesWhenSendFailed());
            defaultMQProducer.setDefaultTopicQueueNums(mqProperties.getProducerTopicQueueNums());
            defaultMQProducer.start();
        } catch (MQClientException e) {
            log.error("D8GER....初始化producer失败, 原因: ", e);
            throw e;
        }
        log.info("D8GER....初始化producer完成, producerGroup=[{}], nameSrvAddr=[{}]", mqProperties.getProducerGroup(), mqProperties.getNameSrvAddr());
        defaultQueueSelector = (mqs, msg, arg) -> {
            int id = (int) count.incrementAndGet();
            int size = mqs.size();
            int index = id % size;
            log.info("选择发送消息队列, index=[{}], arg=[{}], key=[{}]", index, arg, msg.getKeys());
            return mqs.get(index);
        };
        orderedQueueSelectorZero = (mqs, msg, arg) -> {
            log.info("选择发送消息队列, index=[{}], arg=[{}], key=[{}]", 0, arg, msg.getKeys());
            return mqs.get(0);
        };
    }

    /**
     * 销毁
     */
    @PreDestroy
    public void destroy() {
        defaultMQProducer.shutdown();
    }

    /**
     * 异步立即发送消息
     *
     * @param d8Message
     */
    public void sendMsgAsyn(D8BaseMessage d8Message) {
        sendMsgAsyn(d8Message, select(d8Message), null);
    }

    /**
     * 异步延时发送消息
     *
     * @param d8Message
     */
    public void sendMsgDelayAsyn(D8BaseMessage d8Message) {
        sendMsgAsyn(d8Message, select(d8Message), mqProperties.getProducerDefaultDelayLevel().getValue());
    }

    /**
     * 同步发送消息
     *
     * @param d8Message
     */
    public void sendMsgSyn(D8BaseMessage d8Message) {
        sendMsgSyn(d8Message, select(d8Message));
    }

    /**
     * 发送异步消息
     *
     * @param d8Message
     * @param selector
     * @param delayLevel
     */
    @WarnDoc("出现失败时会存在重复发送消息, 交由客户端处理重复消息保证幂等")
    private void sendMsgAsyn(D8BaseMessage d8Message, MessageQueueSelector selector, Integer delayLevel) {
        String topic = mqProperties.wrapperTopic(d8Message.getTopic());
        String tag = d8Message.getTag();
        String key = d8Message.getKey();
        Message message = new Message(topic, tag, key, JSONUtil.toJSONStringWithDateFormat(d8Message.getData()).getBytes());
        if (Objects.nonNull(delayLevel)) {
            message.setDelayTimeLevel(delayLevel);
        }
        log.info("开始发送消息, topic=[{}], tag=[{}], key=[{}]", topic, tag, key);
        try {
            defaultMQProducer.send(message, selector, d8Message.getData(),
                    new SendCallback() {
                        @Override
                        public void onSuccess(SendResult sendResult) {
                            ThreadTraceUtil.beginTrace();
                            if (sendResult.getSendStatus().equals(SendStatus.SEND_OK)) {
                                log.info("发送消息成功, topic=[{}], tag=[{}], key=[{}]", topic, tag, key);
                                handleSuccess(d8Message);
                            } else {
                                log.info("发送消息失败, topic=[{}], tag=[{}], key=[{}]", topic, tag, key);
                                handleFailed(d8Message);
                            }
                            ThreadTraceUtil.endTrace();
                        }

                        @Override
                        public void onException(Throwable e) {
                            ThreadTraceUtil.beginTrace();
                            log.info("发送消息异常, topic=[{}], tag=[{}], key=[{}]", topic, tag, key);
                            handleFailed(d8Message);
                            ThreadTraceUtil.endTrace();
                        }
                    },
                    mqProperties.getProducerAsynTimeOutMillis()
            );
        } catch (Exception e) {
            log.info("发送消息异常, topic=[{}], tag=[{}], key=[{}], 异常原因: {}", topic, tag, key, e);
            handleFailed(d8Message);
        }
    }


    /**
     * 同步发送消息
     *
     * @param d8Message
     * @param selector
     */
    @WarnDoc("出现失败时会存在重复发送消息, 交由客户端处理重复消息保证幂等")
    private void sendMsgSyn(D8BaseMessage d8Message, MessageQueueSelector selector) {
        String topic = mqProperties.wrapperTopic(d8Message.getTopic());
        String tag = d8Message.getTag();
        String key = d8Message.getKey();
        log.info("开始发送消息, topic=[{}], tag=[{}], key=[{}]", topic, tag, key);
        Message message = new Message(topic, tag, key, JSONUtil.toJSONStringWithDateFormat(d8Message.getData()).getBytes());
        try {
            SendResult sendResult = defaultMQProducer.send(message, selector, d8Message.getData(), mqProperties.getProducerSynTimeOutMillis());
            if (sendResult.getSendStatus().equals(SendStatus.SEND_OK)) {
                log.info("发送消息成功, topic=[{}], tag=[{}], key=[{}]", topic, tag, key);
                handleSuccess(d8Message);
            } else {
                log.info("发送消息失败, topic=[{}], tag=[{}], key=[{}], sendResult=[{}]", topic, tag, key, sendResult);
                handleFailed(d8Message);
            }
        } catch (Exception e) {
            log.info("发送消息异常, topic=[{}], tag=[{}], key=[{}], 异常原因: {}", topic, tag, key, e);
            handleFailed(d8Message);
        }
    }

    /**
     * 同步发送消息
     *
     * @param d8Message
     * @return
     */
    private MessageQueueSelector select(@NonNull D8BaseMessage d8Message) {
        return d8Message.getConsumeOrdered() ? orderedQueueSelectorZero : defaultQueueSelector;
    }

    /**
     * 成功回调
     *
     * @param d8Message
     */
    private void handleSuccess(D8BaseMessage d8Message) {
        try {
            sendCallback.onSuccess(d8Message);
        } catch (Exception e) {
            log.error("发送消息成功回调处理异常, d8Message=[{}], 原因: {}", d8Message, e);
        }
    }

    /**
     * 失败回调
     *
     * @param d8Message
     */
    private void handleFailed(D8BaseMessage d8Message) {
        try {
            sendCallback.onFailed(d8Message);
        } catch (Exception e) {
            log.error("发送消息失败回调处理异常, d8Message=[{}], 原因: {}", d8Message, e);
        }
    }

}
