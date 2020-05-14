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

package com.xyz.caofancpu;

import com.xyz.caofancpu.extra.NormalUseForTestUtil;
import com.xyz.caofancpu.mvc.configuration.BusinessPoolConfiguration;
import com.xyz.caofancpu.mvc.configuration.RestTemplateConfiguration;
import com.xyz.caofancpu.mvc.configuration.StandardHTTPMessageConfiguration;
import com.xyz.caofancpu.mvc.configuration.SwaggerConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 启动测试类
 *
 * @author D8GER
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {D8gerWebApplicationTest.TestConfig.class},
        properties = {

        },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class D8gerWebApplicationTest {

    @Before
    public void before() {
        NormalUseForTestUtil.out("---------测试前---------");
    }

    @After
    public void after() {
        NormalUseForTestUtil.out("---------测试后---------");
    }

    @Test
    public void test() {
        NormalUseForTestUtil.out("你瞅啥? 瞅你咋滴?!");
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
            BusinessPoolConfiguration.class,
            RestTemplateConfiguration.class,
            StandardHTTPMessageConfiguration.class,
            SwaggerConfiguration.class
    })
    public static class TestConfig {

    }

}