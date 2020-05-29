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

package com.xyz.caofancpu.mvc.standard;

import com.xyz.caofancpu.core.CollectionUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Jedis服务
 *
 * @author D8GER
 */
@Slf4j
public class JedisService {

    private final JedisPool jedisPool;

    private int rDbIndex;

    public JedisService(@NonNull JedisPool jedisPool, int rDbIndex) {
        if (rDbIndex > 0) {
            this.rDbIndex = rDbIndex;
        }
        this.jedisPool = jedisPool;
    }

    private Jedis getResource() {
        return jedisPool.getResource();
    }

    private Jedis getAndSelectRDB() {
        Jedis jedis = jedisPool.getResource();
        jedis.select(rDbIndex);
        return jedis;
    }

    /**
     * 功能: 插入队列
     *
     * @param key
     * @param value
     * @return
     */
    public boolean insertQueue(String key, String value) {
        try (Jedis jedis = getAndSelectRDB()) {
            jedis.rpush(key, value);
            return true;
        } catch (Exception e) {
            log.error("插入队列失败: key=[{}], value=[{}], 原因: {}", key, value, e);
            throw e;
        }
    }

    /**
     * 功能: 获取头部指定长度的队列
     *
     * @param key
     * @param length
     * @return
     */
    public List<String> getHeaderQueue(String key, int length) {
        try (Jedis jedis = getAndSelectRDB()) {
            List<String> queueValue = new ArrayList<String>();
            for (int i = 0; i < length; i++) {
                String v = jedis.lpop(key);
                if (StringUtils.isNotEmpty(v)) {
                    queueValue.add(v);
                }
            }
            return queueValue;
        } catch (Exception e) {
            log.error("获取头部指定长度的队列失败: key=[{}], length=[{}], 原因: {}", key, length, e);
            throw e;
        }
    }

    /**
     * 功能: 获取尾部指定长度的队列
     *
     * @param key
     * @param length
     * @return
     */
    public List<String> getFootQueue(String key, int length) {
        try (Jedis jedis = getAndSelectRDB()) {
            List<String> queueValue = new ArrayList<String>();
            for (int i = 0; i < length; i++) {
                String v = jedis.rpop(key);
                if (StringUtils.isNotEmpty(v)) {
                    queueValue.add(v);
                }
            }
            return queueValue;
        } catch (Exception e) {
            log.error("获取尾部指定长度的队列失败: key=[{}], length=[{}], 原因: {}", key, length, e);
            throw e;
        }
    }

    /**
     * 功能: 获取队列头部元素
     *
     * @param key
     * @return
     */
    public String getHeadQueue(String key) {
        try (Jedis jedis = getAndSelectRDB()) {
            return jedis.lpop(key);
        } catch (Exception e) {
            log.error("获取队列头部元素失败: key=[{}]], 原因: {}", key, e);
            throw e;
        }
    }

    /**
     * 功能: 获取队列尾部元素
     *
     * @param key
     * @return
     */
    public String getTailQueue(String key) {
        try (Jedis jedis = getAndSelectRDB()) {
            return jedis.rpop(key);
        } catch (Exception e) {
            log.error("获取队列尾部元素失败: key=[{}]], 原因: {}", key, e);
            throw e;
        }
    }

    /**
     * 功能: 获取key总记录数
     *
     * @param key
     * @return
     */
    public Long getQueueCount(String key) {
        try (Jedis jedis = getAndSelectRDB()) {
            return jedis.llen(key);
        } catch (Exception e) {
            log.error("获取总记录数失败: key=[{}]], 原因: {}", key, e);
            throw e;
        }
    }

    /**
     * 功能: 头插法
     *
     * @param key
     * @param value
     * @return
     */
    public boolean insertHeadQueue(String key, String value) {
        try (Jedis jedis = getAndSelectRDB()) {
            jedis.lpush(key, value);
            return true;
        } catch (Exception e) {
            log.error("头插元素失败: key=[{}], value=[{}], 原因: {}", key, value, e);
            throw e;
        }
    }

    /**
     * 功能: 尾插法
     *
     * @param key
     * @param value
     * @return
     */
    public Boolean insertFootQueue(String key, String value) {
        try (Jedis jedis = getAndSelectRDB()) {
            jedis.rpush(key, value);
            return true;
        } catch (Exception e) {
            log.error("尾插元素失败: key=[{}], value=[{}], 原因: {}", key, value, e);
            throw e;
        }
    }

    /**
     * 功能: 从头部批量加入队列
     *
     * @param key
     * @param valueList
     * @param expire
     * @return
     */
    public Boolean batchHeaderInsertList(String key, List<String> valueList, Integer expire) {
        try (Jedis jedis = getAndSelectRDB()) {
            Pipeline p = jedis.pipelined();
            for (int i = 0; i < valueList.size(); i++) {
                p.lpush(key, valueList.get(i));
            }
            p.sync();
            if (expire != null && expire > 0) {
                jedis.expire(key, expire);
            }
            return true;
        } catch (Exception e) {
            log.error("头部批量加入元素失败: key=[{}], value=[{}], 原因: {}", key, CollectionUtil.show(valueList), e);
            throw e;
        }
    }

    /**
     * 功能: 从尾部批量加入队列
     *
     * @param key
     * @param valueList
     * @param expire
     * @return
     */
    public Boolean batchTailInsertList(String key, List<String> valueList, Integer expire) {
        try (Jedis jedis = getAndSelectRDB()) {
            Pipeline p = jedis.pipelined();
            for (int i = 0; i < valueList.size(); i++) {
                p.rpush(key, valueList.get(i));
            }
            p.sync();
            if (expire != null && expire > 0) {
                jedis.expire(key, expire);
            }
            return true;
        } catch (Exception e) {
            log.error("尾部批量加入元素失败: key=[{}], value=[{}], 原因: {}", key, CollectionUtil.show(valueList), e);
            throw e;
        }
    }

//
//    /**
//     * 功能: 向hash中插入单条数据
//     */
//    public Boolean hset(String key, String field, String value) {
//
//    }
//
//    /**
//     * 功能: 向hash中插入单条数据过期
//     */
//    public Boolean hset(String key, String field, String value, Integer expireTime) {
//        return jedisPool.hset(key, field, value, rDbIndex, expireTime);
//    }
//
//    /**
//     * 功能: 向hash中批量插入多条数据
//     */
//    public Boolean hmset(String key, Map<String, String> hash, Integer expire) {
//        return jedisPool.hmset(key, hash, expire, rDbIndex);
//    }
//
//    /**
//     * 功能: 获取hash中的单条数据
//     */
//    public String hget(String key, String field) {
//        return jedisPool.hget(key, field, rDbIndex);
//    }
//
//    /**
//     * 功能: 获取hash中指定fields的数据
//     */
//    public List<String> hmget(String key, String... fields) {
//        return jedisPool.hmget(key, rDbIndex, fields);
//    }
//
//    /**
//     * 功能: 获取hash的所有数据
//     */
//    public Map<String, String> hgetAll(String key) {
//        return jedisPool.hgetAll(key, rDbIndex);
//    }
//
//    /**
//     * 功能: 从hash中移除指定的field
//     */
//    public Long hdel(String key, String... field) {
//        return jedisPool.hdel(rDbIndex, key, field);
//    }
//
//    /**
//     * 功能: 获取hash中key
//     */
//    public Set<String> hkeys(String key) {
//        return jedisPool.hkeys(key, rDbIndex);
//    }
//
//    /**
//     * 功能: 获取hash的长度
//     */
//    public Long hlen(String key) {
//        return jedisPool.hlen(key, rDbIndex);
//    }
//
//    /**
//     * 功能: 行集合set种放入数据
//     */
//    public Long sadd(String key, String... members) {
//        return jedisPool.sadd(rDbIndex, key, members);
//    }
//
//    /**
//     * 功能: 获取无序集合set
//     */
//    public Set<String> smembers(String key) {
//        return jedisPool.smembers(rDbIndex, key);
//    }
//
//    /**
//     * 功能: 判断某个元素是否在无序集合中
//     */
//    public Boolean sismember(String key, String member) {
//        return jedisPool.sismember(rDbIndex, key, member);
//    }
//
//    /**
//     * 功能: 从无序集合中移除一个或多个元素
//     */
//    public Long srem(String key, String... members) {
//        return jedisPool.srem(rDbIndex, key, members);
//    }
//
//    /**
//     * 功能: 设置字符串值
//     */
//    public Boolean setValue(String key, String sValue, Integer expire) {
//        boolean fag = jedisPool.set(key, sValue, rDbIndex);
//        if (fag && expire != null && expire > 0) {
//            jedisPool.expire(key, expire, rDbIndex);
//        }
//        return fag;
//    }
//
//    /**
//     * 功能: 获取字符串值
//     */
//    public String getValue(String key) {
//        return jedisPool.get(key, rDbIndex);
//    }
//
//    /**
//     * 功能: 删除字符串直
//     */
//    public Long deleteKey(String key) {
//        return jedisPool.del(key, rDbIndex);
//    }
//
//    /**
//     * 功能: 删除字符串直
//     */
//    public Long deleteKeys(String... keys) {
//        return jedisPool.del(rDbIndex, keys);
//    }
//
//    /**
//     * 功能: 判断key是否存在
//     */
//    public Boolean isKeyExists(String key) {
//        return jedisPool.isKeyExists(key, rDbIndex);
//    }
//
//    /**
//     * 批量设置字符串
//     *
//     * @param key_val
//     * @param expire
//     * @return
//     */
//    public Boolean setBatchValue(Map<String, String> key_val, Integer expire) {
//        boolean result = jedisPool.setBatchValue(key_val, rDbIndex);
//        if (expire != null && expire > 0) {
//            jedisPool.batchExpireKey(key_val.keySet(), expire, rDbIndex);
//        }
//        return result;
//    }
//
//    public List<String> getList(String key, int start, int end) {
//        return jedisPool.getRedisList(key, start, end, rDbIndex);
//    }
//
//    public Set<String> key(String skey) {
//        return jedisPool.key(skey, rDbIndex);
//    }
//
//    public Long setNx(String key, String value, int expireTime) {
//        return jedisPool.setnx(key, value, expireTime, rDbIndex);
//    }
//
//    public String setEx(String key, String value, int expireTime) {
//        return jedisPool.setex(key, value, expireTime, rDbIndex);
//    }
//
//    public String getnx(String key) {
//        return jedisPool.getnx(key, rDbIndex);
//    }
//
//
//    /**
//     * @param key
//     * @param member
//     * @param score
//     * @return
//     */
//    public Long zadd(String key, String member, Double score) {
//        return jedisPool.zadd(rDbIndex, key, member, score);
//    }
//
//    public Long zadd(String key, Map<String, Double> scoreMembers) {
//        return jedisPool.zadd(rDbIndex, key, scoreMembers);
//    }
//
//    /**
//     * 获取有序集合总成员总数
//     *
//     * @param key
//     * @return
//     */
//    public Long zcard(String key) {
//        return jedisPool.zcard(rDbIndex, key);
//    }
//
//    /**
//     * 返回有序集合中指定成员的排名，有序集成员按分数值递减(从小到大)排序
//     *
//     * @param key
//     * @param member
//     * @return
//     */
//    public Long zrank(String key, String member) {
//        return jedisPool.zrank(rDbIndex, key, member);
//    }
//
//    /**
//     * 返回有序集 key 中，指定区间内的成员，成员的位置按 score 值递增(从小到大)来排序
//     *
//     * @param key
//     * @param start
//     * @param end
//     */
//    public Set<String> zrange(String key, long start, long end) {
//        return jedisPool.zrange(rDbIndex, key, start, end);
//    }
//
//    /**
//     * 移除有序集 key 中的一个或多个成员，不存在的成员将被忽略。
//     *
//     * @param key
//     * @param members
//     * @return
//     */
//    public Long zrem(String key, String... members) {
//        return jedisPool.zrem(rDbIndex, key, members);
//    }
//
//    /**
//     * 返回有序集合中指定成员的排名，有序集成员按分数值递减(从大到小)排序
//     *
//     * @param key
//     * @param userId
//     * @return
//     */
//    public Long zrevrank(String key, Long userId) {
//        return jedisPool.zrevrank(rDbIndex, key, userId.toString());
//    }
//
//    /**
//     * 返回有序集中指定分数区间内的成员，分数从高到低排序
//     *
//     * @param key
//     * @param max
//     * @param min
//     * @return
//     */
//    public Set<String> zrevrangeByScore(String key, Double max, Double min) {
//        return jedisPool.zrevrangeByScore(rDbIndex, key, max, min);
//    }
//
//    /**
//     * 返回有序集中，成员的分数值
//     *
//     * @param key
//     * @param userId
//     * @return
//     */
//    public Double zscore(String key, Long userId) {
//        return jedisPool.zscore(rDbIndex, key, userId.toString());
//    }
//
//    /**
//     * 执行事务
//     *
//     * @return
//     */
//    public void multi(RedisMultiAction action) {
//        jedisPool.multi(rDbIndex, action);
//    }
//
//    /**
//     * 执行命令
//     *
//     * @param ts
//     * @return
//     */
//    public List<Object> exec(Transaction ts) {
//        return jedisPool.exec(ts, rDbIndex);
//    }
//
//    /**
//     * 获取redis的服务信息
//     *
//     * @return
//     */
//    public String info() {
//        return jedisPool.info();
//    }
//
//    /**
//     * 获取redis的服务指定信息
//     *
//     * @param section
//     * @return
//     */
//    public String info(String section) {
//        return jedisPool.info(section);
//    }

    /**
     * 批量设置字符串
     *
     * @param kvMap
     * @return
     */
    public Boolean setBatchValue(Map<String, String> kvMap) {
        if (CollectionUtil.isEmpty(kvMap)) {
            return false;
        }

        try (Jedis jedis = getAndSelectRDB()) {
            Pipeline pipeline = jedis.pipelined();
            kvMap.forEach(pipeline::set);
            pipeline.sync();
            return true;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 批量设置过期时间
     *
     * @param keys
     * @param expireTime
     * @return
     */
    public boolean batchSetExpireTime(Set<String> keys, Integer expireTime) {
        if (CollectionUtil.isEmpty(keys) || expireTime == null || expireTime < 0) {
            return false;
        }
        try (Jedis jedis = getAndSelectRDB()) {
            Pipeline pipeline = jedis.pipelined();
            keys.forEach(key -> pipeline.expire(key, expireTime));
            pipeline.sync();
            return true;
        } catch (Exception e) {
            log.error("批量设置过期时间失败, key=[{}], expireTime=[{}], 原因: {}", CollectionUtil.show(keys), expireTime, e);
            throw e;
        }
    }

    /**
     * 向hash中插入单条
     *
     * @param key
     * @param field
     * @param value
     * @return
     */
    public boolean hSet(String key, String field, String value) {
        try (Jedis jedis = getAndSelectRDB()) {
            return jedis.hset(key, field, value) > 0;
        } catch (Exception e) {
            log.error("向hash中插入单条数据并设置过期时间失败, key=[{}], field=[{}], value=[{}], 原因: {}", key, field, value, e);
            throw e;
        }
    }

    /**
     * 向hash中插入单条数据并设置过期时间
     *
     * @param key
     * @param field
     * @param value
     * @return
     */
    public Boolean hSetAndExpire(String key, String field, String value, Integer expireTime) {
        try (Jedis jedis = getAndSelectRDB()) {
            Transaction transaction = jedis.multi();
            transaction.hset(key, field, value);
            transaction.expire(key, expireTime);
            transaction.exec();
            return true;
        } catch (Exception e) {
            log.error("向hash中插入单条数据并设置过期时间失败, key=[{}], field=[{}], value=[{}], expireTime=[{}], 原因: {}", key, field, value, expireTime, e);
            throw e;
        }
    }

    /**
     * 向hash中批量插入多条数据并设置过期时间
     *
     * @param key
     * @param kvMap
     * @return
     */
    public Boolean hMultiSet(String key, Map<String, String> kvMap, Integer expireTime) {
        try (Jedis jedis = getAndSelectRDB()) {
            jedis.hmset(key, kvMap);
            if (expireTime != null && expireTime > 0) {
                jedis.expire(key, expireTime);
            }
            return true;
        } catch (Exception e) {
            log.error("向hash中批量插入多条数据并设置过期时间, key=[{}], value=[{}], expireTime=[{}], 原因: {}", key, CollectionUtil.showMap(kvMap), expireTime, e);
            throw e;
        }
    }
}
