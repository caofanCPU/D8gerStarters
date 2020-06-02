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

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.xyz.caofancpu.annotation.AttentionDoc;
import com.xyz.caofancpu.annotation.WarnDoc;
import com.xyz.caofancpu.core.CollectionUtil;
import com.xyz.caofancpu.extra.NormalUseForTestUtil;
import com.xyz.caofancpu.mvc.standard.JedisService;
import com.xyz.caofancpu.property.RedisProperties;
import org.junit.Before;
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
     * 本测试ZSet相关的KEY
     */
    private static final String TEST_Z_SET_KEY = TEST_KEY_PREFIX + "zset";

    /**
     * 本测试HSet相关的KEY
     */
    private static final String TEST_H_SET_KEY = TEST_KEY_PREFIX + "hset";

    /**
     * 本测试Set相关的KEY
     */
    private static final String TEST_SET_KEY = TEST_KEY_PREFIX + "set";

    /**
     * 本测试List相关的KEY
     */
    private static final String TEST_LIST_KEY = TEST_KEY_PREFIX + "list";

    /**
     * Redis客户端
     */
    private static JedisService redisClient = null;

    /**
     * 初始化Redis客户端
     */
    @Before
    public void before() {
        redisClient = initRedisClient();
        NormalUseForTestUtil.out("初始化Redis客户端完成");
    }

    /**
     * 获取REDIS服务信息
     *
     * @throws Exception
     */
    @Test
    public void testInfo()
            throws Exception {
        String info = redisClient.info();
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
        boolean success = redisClient.setEx(TEST_EXPIRE_KEY, "帝八哥", null);
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
        boolean success = redisClient.batchSetExpireTime(Sets.newHashSet(TEST_EXPIRE_KEY), 60);
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
        boolean success = redisClient.batchPersist(Sets.newHashSet(TEST_EXPIRE_KEY));
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
        Set<String> results = redisClient.searchByKeyRegex(REDIS_KEY_SEARCH_REGEX.pattern());
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
        Map<String, Long> resultMap = redisClient.getProbableExpireTime(Sets.newHashSet(TEST_EXPIRE_KEY, "account:phone:13100522418"));
        NormalUseForTestUtil.out(CollectionUtil.showMap(resultMap));
    }

    /**
     * ZSet测试
     *
     * @throws Exception
     */
    @Test
    public void testZSet()
            throws Exception {
        Map<String, Double> zSetMap = Maps.newHashMap();
        zSetMap.put("A", 15d);
        zSetMap.put("B", 18d);
        zSetMap.put("C", 15d);
        zSetMap.put("D", 14d);
        zSetMap.put("E", 22d);
        zSetMap.put("F", 11d);
        redisClient.zBatchAdd(TEST_Z_SET_KEY, zSetMap);
        NormalUseForTestUtil.out(redisClient.zCard(TEST_Z_SET_KEY));
        NormalUseForTestUtil.out(redisClient.zScore(TEST_Z_SET_KEY, "C"));
        NormalUseForTestUtil.out(redisClient.zRank(TEST_Z_SET_KEY, "C"));
        NormalUseForTestUtil.out(redisClient.zReverseRank(TEST_Z_SET_KEY, "B"));
        NormalUseForTestUtil.out(CollectionUtil.show(redisClient.zRange(TEST_Z_SET_KEY, 2, 4)));
        NormalUseForTestUtil.out(CollectionUtil.show(redisClient.zReverseRange(TEST_Z_SET_KEY, 2, 4)));
        NormalUseForTestUtil.out(CollectionUtil.show(redisClient.zRangeByScore(TEST_Z_SET_KEY, 16d, 14d)));
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
                .setPwd("redishtjy1")
                .setMaxSinglePipelineCmdNum(10000);
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
        return new JedisService(jedisPool, redisProperties.getRDbIndex(), redisProperties.getMaxSinglePipelineCmdNum());
    }

}