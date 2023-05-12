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

import com.google.common.collect.Maps;
import com.xyz.caofancpu.logger.trace.ThreadTraceUtil;
import com.xyz.caofancpu.property.MQProperties;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * MQ消息监听协调处理器父类
 *
 * @author D8GER
 */
@NoArgsConstructor
@Slf4j
public abstract class AbstractD8MQConcurrentlyListener implements MessageListenerConcurrently {
    protected MQProperties mqProperties;

    /**
     * 消息处理器
     */
    protected Map<String, D8MQConsumerListener> mqConsumerListenerMap = Maps.newHashMap();

    /**
     * MQ消费记录日志
     */
    protected Logger MQ_LOG;

    /**
     * 交由子类实现参数初始化
     */
    public abstract void init();

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgList, ConsumeConcurrentlyContext context) {
        ThreadTraceUtil.beginTrace();
        MessageExt msg = msgList.get(0);
        MQ_LOG.info("model:[{}].result:[{}].topic:[{}].tag:[{}].key:[{}].hashId:[{}].recTraceId:[{}]",
                "REC", "SUCCESS", msg.getTopic(), msg.getTags(), msg.getKeys(), "", ThreadTraceUtil.getTraceId());
        log.info("开始消费消息, msg=[{}]", msg);
        D8MQConsumerListener d8MqConsumerListener = mqConsumerListenerMap.get(MQProperties.extractLogicTopic(msg.getTopic()));
        try {
            d8MqConsumerListener.consumeMessage(msg);
            log.info("消费消息完成, msg=[{}]", msg);
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            int retryTimesWhenFailed = msg.getReconsumeTimes();
            if (retryTimesWhenFailed > mqProperties.getConsumerRetryTimesWhenHandleFailed()) {
                log.info("消费消息异常, msg=[{}]", msg);
                try {
                    d8MqConsumerListener.consumeFailed(msg);
                } catch (Exception ex) {
                    log.error("消费消息失败回调处理异常, msg=[{}], 原因: {}", msg, ex);
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
            log.error("消费消息处理异常, 尝试重新消费, msg=[{}], 重新消费次数retryTimesWhenFailed[{}], 异常原因: {}", msg, msg.getReconsumeTimes(), e);
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        } finally {
            ThreadTraceUtil.endTrace();
        }
    }
}
