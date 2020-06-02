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
import com.xyz.caofancpu.annotation.AttentionDoc;
import com.xyz.caofancpu.annotation.WarnDoc;
import com.xyz.caofancpu.core.CollectionUtil;
import com.xyz.caofancpu.extra.NormalUseForTestUtil;
import com.xyz.caofancpu.mvc.standard.JedisService;
import com.xyz.caofancpu.property.RedisProperties;
import org.junit.Test;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Redis测试类
 *
 * @author D8GER
 */
public class RedisTest {
    /**
     * 本测试使用KEY前缀
     */
    private static final String TEST_KEY_PREFIX = "D8GER_";

    /**
     * Redis正则表达式支持很有限, 仅三种:
     * 1. * 任意长度字符
     * 2. ? 任意单一字符
     * 3. []可选单一字符
     */
    @WarnDoc("生产环境慎用")
    @AttentionDoc("Redis正则表达式支持有限")
    private static final Pattern REDIS_KEY_SEARCH_REGEX = Pattern.compile("D8GER_*");

    /**
     * 本测试过期时间相关的KEY
     */
    private static final String TEST_EXPIRE_KEY = TEST_KEY_PREFIX + "expireTime";

    /**
     * 获取REDIS服务信息
     *
     * @throws Exception
     */
    @Test
    public void testInfo()
            throws Exception {
        JedisService jedisService = initRedisClient();
        String info = jedisService.info();
        NormalUseForTestUtil.out(info);
    }

    /**
     * 设置KV及可能的过期时间
     *
     * @throws Exception
     */
    @Test
    public void testSetKV()
            throws Exception {
        JedisService jedisService = initRedisClient();
        boolean success = jedisService.setEx(TEST_EXPIRE_KEY, "帝八哥", null);
        NormalUseForTestUtil.out(success);
    }

    /**
     * 设置过期时间, 单位为秒
     *
     * @throws Exception
     */
    @Test
    public void testExpireTime()
            throws Exception {
        JedisService jedisService = initRedisClient();
        boolean success = jedisService.batchSetExpireTime(Sets.newHashSet(TEST_EXPIRE_KEY), 60);
        NormalUseForTestUtil.out(success);
    }

    /**
     * 设置永久KEY, ttl为-1
     *
     * @throws Exception
     */
    @Test
    public void testPersistKey()
            throws Exception {
        JedisService jedisService = initRedisClient();
        boolean success = jedisService.batchPersist(Sets.newHashSet(TEST_EXPIRE_KEY));
        NormalUseForTestUtil.out(success);
    }

    /**
     * 根据正则搜索KEY
     *
     * @throws Exception
     */
    @Test
    public void testSearchByKey()
            throws Exception {
        JedisService jedisService = initRedisClient();
        Set<String> results = jedisService.searchByKeyRegex(REDIS_KEY_SEARCH_REGEX.pattern());
        if (CollectionUtil.isNotEmpty(results)) {
            NormalUseForTestUtil.out(CollectionUtil.show(results));
        }
    }

    /**
     * 设置KV及可能的过期时间
     *
     * @throws Exception
     */
    @Test
    public void testGetProbableExpireTime()
            throws Exception {
        JedisService jedisService = initRedisClient();
        Map<String, Long> resultMap = jedisService.getProbableExpireTime(Sets.newHashSet(TEST_EXPIRE_KEY, "account:phone:13100522418"));
        NormalUseForTestUtil.out(CollectionUtil.showMap(resultMap));
    }

    /**
     * 初始化redis客户端(Jedis版本)
     *
     * @return
     */
    private JedisService initRedisClient() {
        RedisProperties redisProperties = new RedisProperties()
                .setIp("172.16.10.41")
                .setPort(6381)
                .setPwd("redishtjy1");
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
        JedisPool jedisPool = new JedisPool(config, redisProperties.getIp(), redisProperties.getPort(), redisProperties.getMaxInitStartMillis(), redisProperties.getPwd());
        return new JedisService(jedisPool, redisProperties.getRDbIndex());
    }

}