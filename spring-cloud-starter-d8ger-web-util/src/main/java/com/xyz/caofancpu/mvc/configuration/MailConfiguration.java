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

import com.xyz.caofancpu.constant.D8gerConstants;
import com.xyz.caofancpu.mvc.standard.MailService;
import com.xyz.caofancpu.property.MailProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * 邮箱配置
 *
 * @author D8GER
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(name = D8gerConstants.D8_ENABLE, matchIfMissing = true)
@EnableConfigurationProperties(MailProperties.class)
@Slf4j
public class MailConfiguration {

    @Resource
    private MailProperties mailProperties;

    @Bean(name = "mailService")
    @ConditionalOnProperty(name = D8gerConstants.D8_MAIL_ENABLE, matchIfMissing = true)
    public MailService mailService() {
        log.info("D8GER....执行邮件服务初始化");
        MailService mailService = new MailService(mailProperties);
        log.info("D8GER....[mailService]邮件服务初始化完成!");
        return mailService;
    }

}
