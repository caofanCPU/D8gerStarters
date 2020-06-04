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

import com.xyz.caofancpu.extra.NormalUseForTestUtil;
import com.xyz.caofancpu.mvc.configuration.MQConfiguration;
import com.xyz.caofancpu.mvc.standard.mq.D8BaseMessage;
import com.xyz.caofancpu.mvc.standard.mq.D8BaseProducer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.Serializable;

/**
 * 启动测试类
 *
 * @author D8GER
 */
@RunWith(SpringRunner.class)
@SpringBootApplication
@SpringBootTest(classes = {D8gerWebApplicationMQTest.TestConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class D8gerWebApplicationMQTest {

    @Resource
    private D8BaseProducer mqProducer;

    @Before
    public void before() {
        NormalUseForTestUtil.out("---------测试前---------");
    }

    @After
    public void after() {
        NormalUseForTestUtil.out("---------测试后---------");
    }

    @Test
    public void testSendMQ() {
        @SuppressWarnings("unchecked")
        D8BaseMessage d8BaseMessage = new D8BaseMessage()
                .setTopic("D8TOPIC")
                .setTag("D8TAG")
                .setKey("USER-D8GER")
                .setData(new JSONX("name", "帝八哥"));
        mqProducer.sendMsgAsyn(d8BaseMessage);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class JSONX implements Serializable {
        private String key;
        private String value;
    }

    /**
     * 测试时禁用数据库自动配置
     */
    @Configuration
    @EnableAutoConfiguration(exclude = {
            DataSourceAutoConfiguration.class,
            DataSourceTransactionManagerAutoConfiguration.class,
            HibernateJpaAutoConfiguration.class
    })
    @ImportAutoConfiguration({
            MQConfiguration.class,
            D8MQConcurrentlyListener.class,
            MQConsumerConfiguration.class
    })
    public static class TestConfig {

    }

}
