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
import com.xyz.caofancpu.mvc.standard.mq.AbstractD8MQConcurrentlyListener;
import com.xyz.caofancpu.property.MQProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * MQ消费者监听协调处理器
 *
 * @author D8GER
 */
@Component
@Slf4j
public class D8MQConcurrentlyListener extends AbstractD8MQConcurrentlyListener {
    @Resource
    private D8TestListener d8TestListener;

    @Resource
    private ApplicationContext applicationContext;

    @PostConstruct
    @AttentionDoc("用@PostConstruct完成构造函数之外的参数初始化")
    @Override
    public void init() {
        // 1. 适配主题与监听器
        this.mqConsumerListenerMap.put("D8TOPIC", d8TestListener);
        // 2. MQ参数配置, 父类中已有该字段, 直接从容器中取值比@Resource更快
        this.mqProperties = applicationContext.getBean(MQProperties.class);
        // 3. MQ处理日志记录
        this.MQ_LOG = log;
    }

}
