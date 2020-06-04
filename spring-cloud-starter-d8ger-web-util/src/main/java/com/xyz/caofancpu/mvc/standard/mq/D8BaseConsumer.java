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

import com.xyz.caofancpu.property.MQProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.Objects;

/**
 * MQ消费者基类
 *
 * @author D8GER
 */
@Slf4j
public class D8BaseConsumer {

    private final MQProperties mqProperties;

    private final MessageListener messageListener;

    private DefaultMQPushConsumer defaultMQPushConsumer;

    public D8BaseConsumer(MQProperties mqProperties, AbstractD8MQConcurrentlyListener messageListener) {
        if (mqProperties.isLegalMqEnvName()) {
            throw new RuntimeException("初始化consumer失败, mqEnv不得为空也不得包含'_'");
        }
        if (Objects.isNull(messageListener)) {
            throw new RuntimeException("缺少消息监听处理器");
        }
        this.mqProperties = mqProperties;
        this.messageListener = messageListener;
    }

    @PostConstruct
    public void init()
            throws MQClientException {
        log.info("D8GER....执行consumer初始化, consumerGroup=[{}], nameSrvAddr=[{}]", mqProperties.getConsumerGroup(), mqProperties.getNameSrvAddr());
        try {
            defaultMQPushConsumer = new DefaultMQPushConsumer(mqProperties.getMqEnv() + mqProperties.getConsumerGroup());
            defaultMQPushConsumer.setNamesrvAddr(mqProperties.getNameSrvAddr());
            for (Map.Entry<String, String> entry : mqProperties.getConsumerTopicTagMap().entrySet()) {
                defaultMQPushConsumer.subscribe(mqProperties.wrapperTopic(entry.getKey()), entry.getValue());
            }

            // 第一次启动从开始位置消费, 其他从上次消费位置继续消费
            defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            defaultMQPushConsumer.setConsumeThreadMax(mqProperties.getConsumerMaxThreads());
            defaultMQPushConsumer.setConsumeThreadMin(mqProperties.getConsumerMinThreads());
            defaultMQPushConsumer.setConsumeTimeout(mqProperties.getConsumerMaxTimeOutInMinutes());
            defaultMQPushConsumer.setMessageModel(mqProperties.getConsumerMessageModel());
            if (messageListener instanceof MessageListenerConcurrently) {
                defaultMQPushConsumer.registerMessageListener((MessageListenerConcurrently) messageListener);
            } else if (messageListener instanceof MessageListenerOrderly) {
                defaultMQPushConsumer.registerMessageListener((MessageListenerOrderly) messageListener);
            } else {
                throw new RuntimeException("缺少消息监听处理器");
            }
            defaultMQPushConsumer.start();
        } catch (Exception e) {
            log.error("D8GER....初始化consumer失败, 原因: ", e);
            throw e;
        }
        log.info("D8GER....初始化consumer完成, consumerGroup=[{}], nameSrvAddr=[{}]", mqProperties.getConsumerGroup(), mqProperties.getNameSrvAddr());
    }

    @PreDestroy
    public void destroy() {
        defaultMQPushConsumer.shutdown();
    }

}
