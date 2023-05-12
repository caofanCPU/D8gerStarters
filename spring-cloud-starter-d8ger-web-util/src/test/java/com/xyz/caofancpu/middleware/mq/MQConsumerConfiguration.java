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

import com.xyz.caofancpu.annotation.AttentionDoc;
import com.xyz.caofancpu.constant.D8gerConstants;
import com.xyz.caofancpu.mvc.standard.mq.D8BaseConsumer;
import com.xyz.caofancpu.property.MQProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * 消费者初始化配置
 */
@Configuration
@Slf4j
public class MQConsumerConfiguration {
    @Resource
    private MQProperties mqProperties;

    @Resource
    private D8MQConcurrentlyListener d8MQConcurrentlyListener;

    @Bean(name = "mqConsumer")
    @ConditionalOnProperty(name = {D8gerConstants.D8_MQ_ENABLE, D8gerConstants.D8_MQ_CONSUMER_ENABLE}, matchIfMissing = true)
    @ConditionalOnMissingBean(value = D8BaseConsumer.class)
    @AttentionDoc("当容器中不存在D8BaseConsumer才执行创建")
    public D8BaseConsumer mqConsumer() {
        log.info("D8GER....执行MQ消费者初始化");
        D8BaseConsumer mqConsumer = new D8BaseConsumer(mqProperties, d8MQConcurrentlyListener);
        log.info("D8GER....[mqConsumer]MQ消费者初始化完成!");
        return mqConsumer;
    }
}
