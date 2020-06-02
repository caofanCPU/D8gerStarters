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

package com.xyz.caofancpu.middleware;

import com.google.common.collect.Sets;
import com.xyz.caofancpu.core.NumberUtil;
import com.xyz.caofancpu.mvc.standard.MailService;
import com.xyz.caofancpu.property.MailProperties;
import org.junit.Test;

/**
 * Mail测试类
 *
 * @author D8GER
 */
public class MailTest {
    @Test
    public void testSentMail() {
        MailProperties mailProperties = new MailProperties()
                .setMailSendHost("smtp.exmail.qq.com")
                .setFromEmailAddress("caofan@huitongjy.com")
                .setAuthPwd("IDon'tKnow");
        MailService mailService = new MailService(mailProperties);
        mailService.sendMail(Sets.newHashSet("xyb****ZCY@gmail.com", "caofan@huitongjy.com"), "邮箱激活码", "您的邮箱激活码是: " + NumberUtil.getRandomInteger(6));
    }

}