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
import com.xyz.caofancpu.mvc.standard.JedisService;
import com.xyz.caofancpu.property.RedisProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.Resource;

/**
 * Redis统一配置
 *
 * @author D8GER
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(name = D8gerConstants.D8_REDIS_ENABLE, matchIfMissing = true)
@EnableConfigurationProperties(RedisProperties.class)
@Slf4j
public class RedisConfiguration {

    @Resource
    private RedisProperties redisProperties;

    /**
     * jedis连接池
     *
     * @return
     */
    @Bean(name = "jedisPool")
    @ConditionalOnProperty(name = D8gerConstants.D8_REDIS_ENABLE, matchIfMissing = true)
    public JedisPool jedisPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(redisProperties.getMaxTotal());
        config.setMaxIdle(redisProperties.getMaxIdle());
        config.setMinIdle(redisProperties.getMinIdle());
        config.setMaxWaitMillis(redisProperties.getMaxWaitMillis());
        config.setTestWhileIdle(redisProperties.isTestWhiledIdle());
        config.setNumTestsPerEvictionRun(redisProperties.getNumTestsPerEvictionRun());
        config.setTimeBetweenEvictionRunsMillis(redisProperties.getTimeBetweenEvictionRunsMillis());
        config.setMinEvictableIdleTimeMillis(redisProperties.getMinEvictableIdleTimeMillis());
        config.setSoftMinEvictableIdleTimeMillis(redisProperties.getSoftMinEvictableIdleTimeMillis());
        return new JedisPool(config, redisProperties.getIp(), redisProperties.getPort(), redisProperties.getMaxInitStartMillis(), redisProperties.getPwd());
    }

    @Bean(name = "redisClient")
    @ConditionalOnProperty(name = D8gerConstants.D8_REDIS_ENABLE, matchIfMissing = true)
    @ConditionalOnBean(name = "jedisPool")
    public JedisService jedisService() {
        return new JedisService();
    }
}
