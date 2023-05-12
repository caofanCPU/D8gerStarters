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

package com.xyz.caofancpu.mvc.configuration;

import com.xyz.caofancpu.annotation.AttentionDoc;
import com.xyz.caofancpu.constant.D8gerConstants;
import com.xyz.caofancpu.mvc.standard.mq.D8BaseProducer;
import com.xyz.caofancpu.mvc.standard.mq.D8BaseSendCallback;
import com.xyz.caofancpu.mvc.standard.mq.DefaultSendCallback;
import com.xyz.caofancpu.property.MQProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * MQ配置
 *
 * @author D8GER
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(name = {D8gerConstants.D8_ENABLE, D8gerConstants.D8_MQ_ENABLE, D8gerConstants.D8_MQ_PRODUCER_ENABLE}, matchIfMissing = true)
@EnableConfigurationProperties(MQProperties.class)
@Slf4j
public class MQConfiguration {

    @Resource
    private MQProperties mqProperties;

    @Resource
    private Optional<D8BaseSendCallback> optionalD8BaseSendCallback;

    @Bean(name = "mqProducer")
    @ConditionalOnMissingBean(value = D8BaseProducer.class)
    @AttentionDoc("当容器中不存在D8BaseProducer才执行创建")
    public D8BaseProducer mqProducer() {
        log.info("D8GER....执行MQ生产者初始化");
        D8BaseProducer mqProducer = new D8BaseProducer(mqProperties, optionalD8BaseSendCallback.orElse(new DefaultSendCallback()));
        log.info("D8GER....[mqProducer]MQ生产者初始化完成!");
        return mqProducer;
    }

}
