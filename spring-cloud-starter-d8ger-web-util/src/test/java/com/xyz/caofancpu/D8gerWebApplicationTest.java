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

import com.xyz.caofancpu.core.JSONUtil;
import com.xyz.caofancpu.extra.NormalUseForTestUtil;
import com.xyz.caofancpu.mvc.configuration.BusinessPoolConfiguration;
import com.xyz.caofancpu.mvc.configuration.MQConfiguration;
import com.xyz.caofancpu.mvc.configuration.MailConfiguration;
import com.xyz.caofancpu.mvc.configuration.RedisConfiguration;
import com.xyz.caofancpu.mvc.configuration.RestTemplateConfiguration;
import com.xyz.caofancpu.mvc.configuration.StandardHTTPMessageConfiguration;
import com.xyz.caofancpu.mvc.configuration.SwaggerConfiguration;
import com.xyz.caofancpu.remote.DemoHttpRemoteInvoker;
import com.xyz.caofancpu.remote.SSOLoginReq;
import com.xyz.caofancpu.remote.SSOLoginRespBody;
import com.xyz.caofancpu.result.GlobalErrorInfoException;
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
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * 启动测试类
 *
 * @author D8GER
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {D8gerWebApplicationTest.TestConfig.class},
        properties = {
                "spring.cloud.d8ger.redis.ip=172.16.10.41",
                "spring.cloud.d8ger.redis.port=6381",
                "spring.cloud.d8ger.redis.pwd=redishtjy1",
        },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class D8gerWebApplicationTest {

    @Resource
    private DemoHttpRemoteInvoker demoHttpRemoteInvoker;

//    @Resource
//    private JedisService jedisService;
//
//    @Resource
//    private D8BaseProducer mqProducer;

    @Before
    public void before() {
        NormalUseForTestUtil.out("---------测试前---------");
    }

    @After
    public void after() {
        NormalUseForTestUtil.out("---------测试后---------");
    }

    @Test
    public void testRemoteInvoke()
            throws GlobalErrorInfoException {
        // 密码登录超管账号
        SSOLoginReq req = new SSOLoginReq().setPhone("13720203891").setPwd("ht123456.").setLoginType(0);
        // 登录需要特殊的请求头, 指明从哪个应用登录
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("HT-app", "3");
        SSOLoginRespBody body = demoHttpRemoteInvoker.execute(req, httpHeaders);
        NormalUseForTestUtil.out("测试结果: \n" + JSONUtil.formatStandardJSON(body));
    }

//    @Test
//    public void testRedis() {
//        NormalUseForTestUtil.out(jedisService.info());
//    }

    @Test
    public void testMQ() {
//        NormalUseForTestUtil.out(mqProducer);
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
            SwaggerConfiguration.class,
            DemoHttpRemoteInvoker.class,
            RedisConfiguration.class,
            MailConfiguration.class,
            MQConfiguration.class
    })
    public static class TestConfig {

    }


}
