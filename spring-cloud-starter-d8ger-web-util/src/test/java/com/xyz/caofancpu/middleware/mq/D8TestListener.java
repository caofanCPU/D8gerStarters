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

package com.xyz.caofancpu.middleware.mq;

import com.xyz.caofancpu.constant.D8gerConstants;
import com.xyz.caofancpu.core.JSONUtil;
import com.xyz.caofancpu.mvc.standard.mq.D8MQConsumerListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * 消费者测试监听器
 *
 * @author D8GER
 */
@Configuration
@ConditionalOnProperty(name = {D8gerConstants.D8_MQ_ENABLE, D8gerConstants.D8_MQ_CONSUMER_ENABLE}, matchIfMissing = true)
@Slf4j
public class D8TestListener implements D8MQConsumerListener {

    @Override
    public void consumeMessage(MessageExt messageExt)
            throws Exception {
        D8gerWebApplicationMQTest.JSONX jsonx = JSONUtil.deserializeJSON(new String(messageExt.getBody()), D8gerWebApplicationMQTest.JSONX.class);
        log.info("消息来了, 消息主体body=[{}]", JSONUtil.formatStandardJSON(jsonx));
    }

    @Override
    public void consumeFailed(MessageExt messageExt)
            throws Exception {
        log.error("消费失败, message=[{}]", new String(messageExt.getBody()));
    }
}
