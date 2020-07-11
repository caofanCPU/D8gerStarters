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
import com.xyz.caofancpu.mvc.standard.JedisService;
import com.xyz.caofancpu.property.RedisProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.Resource;
import java.util.Optional;

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

    @Resource
    private Optional<JedisPool> optionalJedisPool;

    /**
     * jedis连接池, 不存在JedisPool实例时才进行初始化
     *
     * @return
     */
    @Bean(name = "jedisPool")
    @ConditionalOnProperty(name = D8gerConstants.D8_REDIS_ENABLE, matchIfMissing = true)
    @ConditionalOnMissingBean(value = JedisPool.class)
    @AttentionDoc("当容器中不存在JedisPool才执行创建")
    public JedisPool jedisPool() {
        log.info("D8GER....执行Redis连接池初始化");
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(redisProperties.getMaxTotal());
        config.setMaxIdle(redisProperties.getMaxIdle());
        config.setMinIdle(redisProperties.getMinIdle());
        config.setMaxWaitMillis(redisProperties.getMaxWaitMillis());
        config.setTestOnBorrow(redisProperties.isTestOnBorrow());
        config.setTestWhileIdle(redisProperties.isTestWhiledIdle());
        config.setNumTestsPerEvictionRun(redisProperties.getNumTestsPerEvictionRun());
        config.setTimeBetweenEvictionRunsMillis(redisProperties.getTimeBetweenEvictionRunsMillis());
        config.setMinEvictableIdleTimeMillis(redisProperties.getMinEvictableIdleTimeMillis());
        config.setSoftMinEvictableIdleTimeMillis(redisProperties.getSoftMinEvictableIdleTimeMillis());
        JedisPool jedisPool = new JedisPool(config, redisProperties.getIp(), redisProperties.getPort(), redisProperties.getMaxInitStartMillis(), redisProperties.getPwd());
        log.info("D8GER....[jedisPool]连接池初始化完成!");
        return jedisPool;
    }

    @Bean(name = "redisClient")
    @ConditionalOnProperty(name = D8gerConstants.D8_REDIS_ENABLE, matchIfMissing = true)
    @ConditionalOnMissingBean(value = JedisService.class)
    @ConditionalOnBean(name = "jedisPool")
    public JedisService jedisService() {
        JedisService redisClient = new JedisService(optionalJedisPool.orElse(null), redisProperties.getRDbIndex(), redisProperties.getMaxSinglePipelineCmdNum());
        log.info("D8GER....[redisClient]客户端初始化完成!");
        return redisClient;
    }
}
