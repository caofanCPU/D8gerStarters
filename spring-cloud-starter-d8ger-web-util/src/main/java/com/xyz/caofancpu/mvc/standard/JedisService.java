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

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.xyz.caofancpu.annotation.AttentionDoc;
import com.xyz.caofancpu.annotation.WarnDoc;
import com.xyz.caofancpu.core.CollectionUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * Jedis服务
 *
 * @author D8GER
 */
@Slf4j
public class JedisService {

    public final static byte[] CURSOR_FLAG = "0".getBytes();

    /**
     * Jedis连接池
     */
    private final JedisPool jedisPool;

    /**
     * 数据库索引, 默认0号库
     */
    private int rDbIndex;

    /**
     * 单次PipeLine最大命令数
     */
    @WarnDoc("做限制避免内存溢出")
    private int maxSinglePipelineCmdNum = 100000;

    /**
     * 构造函数, 限定需要初始化连接池
     *
     * @param jedisPool
     * @param rDbIndex
     */
    public JedisService(@NonNull JedisPool jedisPool, int rDbIndex) {
        if (rDbIndex > 0) {
            this.rDbIndex = rDbIndex;
        }
        this.jedisPool = jedisPool;
    }

    /**
     * 构造函数, 限定需要初始化连接池
     *
     * @param jedisPool
     * @param rDbIndex
     * @param maxSinglePipelineCmdNum
     */
    public JedisService(@NonNull JedisPool jedisPool, int rDbIndex, int maxSinglePipelineCmdNum) {
        this(jedisPool, rDbIndex);
        if (maxSinglePipelineCmdNum > 0) {
            this.maxSinglePipelineCmdNum = maxSinglePipelineCmdNum;
        }
    }

    //============================list操作========================//

    /**
     * 插入队列
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
     * 获取头部指定长度的队列
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
     * 获取尾部指定长度的队列
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
     * 获取队列头部元素
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
     * 获取队列尾部元素
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
     * 获取key总记录数
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
     * 头插法
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
     * 尾插法
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
     * 从头部批量加入队列
     *
     * @param key
     * @param valueList
     * @param expireTime
     * @return
     */
    public Boolean batchHeaderInsertList(String key, List<String> valueList, Integer expireTime) {
        preCheckPipelineCmdCount(valueList);
        try (Jedis jedis = getAndSelectRDB()) {
            Pipeline pipeline = jedis.pipelined();
            // 收集命令
            valueList.forEach(s -> pipeline.lpush(key, s));
            // 执行命令
            pipeline.sync();
            if (validateExpireTime(expireTime)) {
                jedis.expire(key, expireTime);
            }
            return true;
        } catch (Exception e) {
            log.error("头部批量加入元素失败: key=[{}], value=[{}], 原因: {}", key, CollectionUtil.show(valueList), e);
            throw e;
        }
    }

    /**
     * 从尾部批量加入队列
     *
     * @param key
     * @param valueList
     * @param expireTime
     * @return
     */
    public Boolean batchTailInsertList(String key, List<String> valueList, Integer expireTime) {
        preCheckPipelineCmdCount(valueList);
        try (Jedis jedis = getAndSelectRDB()) {
            Pipeline pipeline = jedis.pipelined();
            // 收集命令
            valueList.forEach(s -> pipeline.rpush(key, s));
            // 执行命令
            pipeline.sync();
            if (validateExpireTime(expireTime)) {
                jedis.expire(key, expireTime);
            }
            return true;
        } catch (Exception e) {
            log.error("尾部批量加入元素失败: key=[{}], value=[{}], 原因: {}", key, CollectionUtil.show(valueList), e);
            throw e;
        }
    }

    /**
     * 获取指定范围的列表
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public List<String> getList(String key, int start, int end) {
        try (Jedis jedis = getAndSelectRDB()) {
            return jedis.lrange(key, start, end);
        } catch (Exception e) {
            log.error("获取指定范围的列表失败, key=[{}], start=[{}], end=[{}], 原因: {}", key, start, end, e);
            throw e;
        }
    }

    //============================string操作============================//

    /**
     * 批量设置字符串
     *
     * @param kvMap
     * @return
     */
    public Boolean batchAdd(Map<String, String> kvMap) {
        if (CollectionUtil.isEmpty(kvMap)) {
            return false;
        }
        preCheckPipelineCmdCount(kvMap.entrySet());
        try (Jedis jedis = getAndSelectRDB()) {
            Pipeline pipeline = jedis.pipelined();
            // 收集命令
            kvMap.forEach(pipeline::set);
            // 执行命令
            pipeline.sync();
            return true;
        } catch (Exception e) {
            log.error("批量设置字符串失败: map=[{}], 原因: {}", CollectionUtil.showMap(kvMap), e);
            throw e;
        }
    }

    /**
     * 根据keys批量设置过期时间
     *
     * @param keys
     * @param expireTime 单位秒
     * @return
     */
    public boolean batchSetExpireTime(Set<String> keys, Integer expireTime) {
        if (CollectionUtil.isEmpty(keys) || !validateExpireTime(expireTime)) {
            return false;
        }
        preCheckPipelineCmdCount(keys);
        try (Jedis jedis = getAndSelectRDB()) {
            Pipeline pipeline = jedis.pipelined();
            // 收集命令
            keys.forEach(key -> pipeline.expire(key, expireTime));
            // 执行命令
            pipeline.sync();
            return true;
        } catch (Exception e) {
            log.error("批量设置过期时间失败, keys=[{}], expireTime=[{}], 原因: {}", CollectionUtil.show(keys), expireTime, e);
            throw e;
        }
    }

    /**
     * 批量设置持久化KEY
     * <p>
     * {@link #setEx}
     *
     * @param keys
     * @return
     */
    @AttentionDoc("如无必要, 请使用setEx代替")
    public boolean batchPersist(Set<String> keys) {
        if (CollectionUtil.isEmpty(keys)) {
            return false;
        }
        preCheckPipelineCmdCount(keys);
        try (Jedis jedis = getAndSelectRDB()) {
            Pipeline pipeline = jedis.pipelined();
            // 收集命令
            keys.forEach(pipeline::persist);
            // 执行命令
            pipeline.sync();
            return true;
        } catch (Exception e) {
            log.error("批量设置持久化KEY失败, keys=[{}], 原因: {}", CollectionUtil.show(keys), e);
            throw e;
        }
    }

    /**
     * 设置字符串及过期时间
     *
     * @param key
     * @param value
     * @param expireTime 单位秒
     * @return
     */
    public boolean setEx(String key, String value, Integer expireTime) {
        try (Jedis jedis = getAndSelectRDB()) {
            if (validateExpireTime(expireTime)) {
                jedis.setex(key, expireTime, value);
            } else {
                jedis.set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error("设置字符串及过期时间失败, key=[{}], value=[{}], expireTime=[{}], 原因: {}", key, value, expireTime, e);
            throw e;
        }
    }

    /**
     * 设置值及过期时间(可用于简单的分布式锁)
     *
     * @param key
     * @param value
     * @param expireTime 单位秒
     * @return
     */
    public boolean setNx(String key, String value, Integer expireTime) {
        try (Jedis jedis = getAndSelectRDB()) {
            Long result = jedis.setnx(key, value);
            if (validateExpireTime(expireTime) && result == 1) {
                jedis.expire(key, expireTime);
            }
            return true;
        } catch (Exception e) {
            log.error("设置值及过期时间失败, key=[{}], value=[{}], expireTime=[{}], 原因: {}", key, value, expireTime, e);
            throw e;
        }
    }

    /**
     * 获取字符串
     *
     * @param key
     * @return
     */
    public String getValue(String key) {
        try (Jedis jedis = getAndSelectRDB()) {
            return jedis.get(key);
        } catch (Exception e) {
            log.error("获取字符串值失败, key=[{}], 原因: {}", key, e);
            throw e;
        }
    }

    /**
     * 批量删除字符串
     *
     * @param keys
     * @return
     */
    public Long batchDeleteKey(Set<String> keys) {
        if (CollectionUtil.isEmpty(keys)) {
            return 0L;
        }
        try (Jedis jedis = getAndSelectRDB()) {
            return jedis.del(keys.toArray(new String[0]));
        } catch (Exception e) {
            log.error("删除字符串失败, key=[{}], 原因: {}", CollectionUtil.show(keys), e);
            throw e;
        }
    }

    /**
     * 删除字符串
     *
     * @param key
     * @return
     */
    public Long deleteKey(String key) {
        return batchDeleteKey(Sets.newHashSet(key));
    }

    /**
     * 判断key是否存在
     *
     * @param key
     * @return
     */
    public Boolean isExist(String key) {
        try (Jedis jedis = getAndSelectRDB()) {
            return jedis.exists(key);
        } catch (Exception e) {
            log.error("判断是否存在失败, key=[{}], 原因: {}", key, e);
            throw e;
        }
    }

    /**
     * 根据Key正则搜索
     *
     * @param keyRegex
     * @return
     */
    public Set<String> searchByKeyRegex(String keyRegex) {
        try (Jedis jedis = getAndSelectRDB()) {
            return jedis.keys(keyRegex);
        } catch (Exception e) {
            log.error("根据Key正则搜索失败, keyRegex=[{}], 原因: {}", keyRegex, e);
            throw e;
        }
    }

    //============================百宝箱============================//

    /**
     * 批量获取KEY的大致过期时间
     *
     * @param keys
     * @return
     */
    @AttentionDoc("获取的过期时间比真实时间偏大")
    public Map<String, Long> getProbableExpireTime(Set<String> keys) {
        if (CollectionUtil.isEmpty(keys)) {
            return Maps.newHashMap();
        }
        preCheckPipelineCmdCount(keys);
        try (Jedis jedis = getAndSelectRDB()) {
            Pipeline pipeline = jedis.pipelined();
            // 收集命令
            Map<String, Response<Long>> cmdMap = CollectionUtil.transToMap(keys, Function.identity(), pipeline::ttl);
            // 执行命令
            pipeline.sync();
            // 获取及解析结果
            return CollectionUtil.transToMap(cmdMap.entrySet(), Map.Entry::getKey, entry -> entry.getValue().get());
        } catch (Exception e) {
            log.error("批量获取KEY的大致过期时间, keys=[{}] 原因: ", CollectionUtil.show(keys), e);
            throw e;
        }
    }

    /**
     * 批量执行事务命令
     *
     * @param transaction
     */
    public void batchExecute(RedisTransactionFunction transaction) {
        try (Jedis jedis = getAndSelectRDB()) {
            transaction.doAction(jedis.multi());
        } catch (Exception e) {
            log.error("批量执行事务命令失败, 原因: ", e);
            throw e;
        }
    }

    /**
     * 根据正则批量清理Key
     *
     * @param keyRegex key正则
     * @param count    每次扫描条数
     * @return
     */
    @WarnDoc("生产环境慎用, 应反复检查确认keyRegex")
    public Long clearByKeyRegex(final String keyRegex, final Integer count) {
        long keyTotalDel = 0;
        try (Jedis jedis = getAndSelectRDB()) {
            ScanParams scanParams = new ScanParams();
            scanParams.match(keyRegex);
            scanParams.count(count);
            byte[] cursor = CURSOR_FLAG;
            ScanResult<byte[]> scanResult;
            List<byte[]> keys;
            do {
                scanResult = jedis.scan(cursor, scanParams);
                keys = scanResult.getResult();
                if (CollectionUtil.isNotEmpty(keys)) {
                    jedis.del(CollectionUtil.filterAndTransArray(keys, CollectionUtil::isNotEmpty, String::new, String[]::new));
                    keyTotalDel += keys.size();
                    log.debug("成功清除[{}]个key", keys.size());
                }
                cursor = scanResult.getCursorAsBytes();
            } while (CollectionUtil.nonEqualsArray(CURSOR_FLAG, cursor));

            return keyTotalDel;
        } catch (Exception e) {
            log.error("根据正则批量清理Key失败, 原因: ", e);
            throw e;
        }
    }

    /**
     * 获取Redis服务信息
     *
     * @return
     */
    public String info() {
        try (Jedis jedis = getAndSelectRDB()) {
            return jedis.info();
        } catch (Exception e) {
            log.error("获取Redis服务信息失败, 原因: ", e);
            throw e;
        }
    }

    /**
     * 获取Redis服务指定信息
     *
     * @return
     */
    public String info(String section) {
        try (Jedis jedis = getAndSelectRDB()) {
            return jedis.info(section);
        } catch (Exception e) {
            log.error("获取Redis服务指定信息失败, section=[{}], 原因: {}", section, e);
            throw e;
        }
    }

    //============================hash操作============================//

    /**
     * 向hash中插入单条数据并设置过期时间
     *
     * @param key
     * @param field
     * @param value
     * @param expireTime 单位秒
     * @return
     */
    public boolean hSetAndExpire(String key, String field, String value, Integer expireTime) {
        try (Jedis jedis = getAndSelectRDB()) {
            Transaction transaction = jedis.multi();
            transaction.hset(key, field, value);
            if (validateExpireTime(expireTime)) {
                transaction.expire(key, expireTime);
            }
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
     * @param fvMap
     * @param expireTime 单位秒
     * @return
     */
    public boolean hMultiSetAndExpire(String key, Map<String, String> fvMap, Integer expireTime) {
        try (Jedis jedis = getAndSelectRDB()) {
            jedis.hmset(key, fvMap);
            if (validateExpireTime(expireTime)) {
                jedis.expire(key, expireTime);
            }
            return true;
        } catch (Exception e) {
            log.error("向hash中批量插入多条数据并设置过期时间失败, key=[{}], value=[{}], expireTime=[{}], 原因: {}", key, CollectionUtil.showMap(fvMap), expireTime, e);
            throw e;
        }
    }

    /**
     * 当hash中指定field不存在时插入数据
     *
     * @param key
     * @param field
     * @param value
     * @return
     */
    public boolean hSetIfAbsent(String key, String field, String value) {
        try (Jedis jedis = getAndSelectRDB()) {
            return jedis.hsetnx(key, field, value) > 0;
        } catch (Exception e) {
            log.error("当hash中指定field不存在时插入数据失败, key=[{}], field=[{}], value=[{}], 原因: {}", key, field, value, e);
            throw e;
        }
    }

    /**
     * 获取hash中单条数据
     *
     * @param key
     * @param field
     * @return
     */
    public String hGet(String key, String field) {
        try (Jedis jedis = getAndSelectRDB()) {
            return jedis.hget(key, field);
        } catch (Exception e) {
            log.error("获取hash中单条数据失败, key=[{}], field=[{}], 原因: {}", key, field, e);
            throw e;
        }
    }

    /**
     * 获取hash中多个fields数据
     *
     * @param key
     * @param fields
     * @return
     */
    public List<String> hMultiGet(String key, Set<String> fields) {
        try (Jedis jedis = getAndSelectRDB()) {
            return jedis.hmget(key, fields.toArray(new String[0]));
        } catch (Exception e) {
            log.error("获取hash中多个fields数据失败, key=[{}], fields=[{}], 原因: {}", key, CollectionUtil.show(fields), e);
            throw e;
        }
    }

    /**
     * 获取hash中的全部数据(field, value)
     *
     * @param key
     * @return
     */
    public Map<String, String> hGetAll(String key) {
        try (Jedis jedis = getAndSelectRDB()) {
            return jedis.hgetAll(key);
        } catch (Exception e) {
            log.error("获取hash中的全部数据失败, key=[{}], 原因: {}", key, e);
            throw e;
        }
    }

    /**
     * 删除hash中的多个字段
     *
     * @param key
     * @param fields
     * @return
     */
    public Long hDel(String key, Set<String> fields) {
        if (CollectionUtil.isEmpty(fields)) {
            return 0L;
        }
        try (Jedis jedis = getAndSelectRDB()) {
            return jedis.hdel(key, fields.toArray(new String[0]));
        } catch (Exception e) {
            log.error("删除hash中的字段数据失败, key=[{}], fields=[{}], 原因: {}", key, CollectionUtil.show(fields), e);
            throw e;
        }
    }

    /**
     * 获取hash中的field列表
     *
     * @param key
     * @return
     */
    public Set<String> hKeys(String key) {
        try (Jedis jedis = getAndSelectRDB()) {
            return jedis.hkeys(key);
        } catch (Exception e) {
            log.error("获取hash中的fields列表失败, key=[{}], 原因: {}", key, e);
            throw e;
        }
    }

    /**
     * 获取hash数据条数
     *
     * @param key
     * @return
     */
    public Long hLen(String key) {
        try (Jedis jedis = getAndSelectRDB()) {
            return jedis.hlen(key);
        } catch (Exception e) {
            log.error("获取hash数据条数失败, key=[{}], 原因: {}", key, e);
            throw e;
        }
    }

    //============================set操作============================//

    /**
     * 批量添加数据(无序集合)
     *
     * @param key
     * @param members
     */
    public Long sBatchAdd(String key, Set<String> members) {
        if (CollectionUtil.isEmpty(members)) {
            return 0L;
        }
        try (Jedis jedis = getAndSelectRDB()) {
            return jedis.sadd(key, members.toArray(new String[0]));
        } catch (Exception e) {
            log.error("无序集合批量添加数据失败, key=[{}], members=[{}], 原因: {}", key, CollectionUtil.show(members), e);
            throw e;
        }
    }

    /**
     * 添加成员(无序集合)
     *
     * @param key
     * @param member
     * @return
     */
    public Long sAdd(String key, String member) {
        return sBatchAdd(key, Sets.newHashSet(member));
    }

    /**
     * 获取所有成员(无序集合)
     *
     * @param key
     * @return
     */
    public Set<String> sGetAllMembers(String key) {
        try (Jedis jedis = getAndSelectRDB()) {
            return jedis.smembers(key);
        } catch (Exception e) {
            log.error("无序集合获取所有成员失败, key=[{}], 原因: {}", key, e);
            throw e;
        }
    }

    /**
     * 是否存在指定成员(无序集合)
     *
     * @param key
     * @param member
     * @return
     */
    public boolean sExist(String key, String member) {
        try (Jedis jedis = getAndSelectRDB()) {
            return jedis.sismember(key, member);
        } catch (Exception e) {
            log.error("无序集合判断是否存在指定成员失败, key=[{}], member=[{}], 原因: {}", key, member, e);
            throw e;
        }
    }

    /**
     * 批量移除成员(无序集合)
     *
     * @param key
     * @param members
     * @return
     */
    public Long sBatchDelete(String key, Set<String> members) {
        if (CollectionUtil.isEmpty(members)) {
            return 0L;
        }
        try (Jedis jedis = getAndSelectRDB()) {
            return jedis.srem(key, members.toArray(new String[0]));
        } catch (Exception e) {
            log.error("无序集合批量移除成员失败, key=[{}], members=[{}], 原因: {}", key, CollectionUtil.show(members), e);
            throw e;
        }
    }

    /**
     * 移除单个成员(无序集合)
     *
     * @param key
     * @param member
     * @return
     */
    public Long sDelete(String key, Set<String> member) {
        return sBatchAdd(key, Sets.newHashSet(member));
    }

    //============================zset操作============================//

    /**
     * 添加成员及分值
     *
     * @param key
     * @param member
     * @param score
     * @return
     */
    public Long zAdd(String key, String member, Double score) {
        try (Jedis jedis = getAndSelectRDB()) {
            return jedis.zadd(key, score, member);
        } catch (Exception e) {
            log.error("添加成员及分值失败, key=[{}], member=[{}], score=[{}], 原因: {}", key, member, score, e);
            throw e;
        }
    }

    /**
     * 批量添加成员及分值
     *
     * @param key
     * @param memberScoreMap
     * @return
     */
    public Long zBatchAdd(String key, Map<String, Double> memberScoreMap) {
        if (CollectionUtil.isEmpty(memberScoreMap)) {
            return 0L;
        }
        try (Jedis jedis = getAndSelectRDB()) {
            return jedis.zadd(key, memberScoreMap);
        } catch (Exception e) {
            log.error("批量添加成员及分值失败, key=[{}], memberScoreMap=[{}], 原因: {}", key, CollectionUtil.showMap(memberScoreMap), e);
            throw e;
        }
    }

    /**
     * 获取有序集合成员总数
     *
     * @param key
     * @return
     */
    public Long zCard(String key) {
        try (Jedis jedis = getAndSelectRDB()) {
            return jedis.zcard(key);
        } catch (Exception e) {
            log.error("获取有序集合成员总数失败, key=[{}], 原因: {}", key, e);
            throw e;
        }
    }

    /**
     * 获取有序集合中指定成员的增序排名
     *
     * @param key
     * @param member
     * @return
     */
    public Long zRank(String key, String member) {
        try (Jedis jedis = getAndSelectRDB()) {
            return jedis.zrank(key, member);
        } catch (Exception e) {
            log.error("获取有序集合中指定成员的增序排名失败, key=[{}], member=[{}], 原因: {}", key, member, e);
            throw e;
        }
    }

    /**
     * 获取有序集合中指定成员的降序排名
     *
     * @param key
     * @param member
     * @return
     */
    public Long zReverseRank(String key, String member) {
        try (Jedis jedis = getAndSelectRDB()) {
            return jedis.zrevrank(key, member);
        } catch (Exception e) {
            log.error("获取有序集合中指定成员的降序排名失败, key=[{}], member=[{}], 原因: {}", key, member, e);
            throw e;
        }
    }

    /**
     * 获取有序集合指定增序区间内的成员
     *
     * @param key
     * @param start
     * @param end
     */
    public Set<String> zRange(String key, long start, long end) {
        try (Jedis jedis = getAndSelectRDB()) {
            return jedis.zrange(key, start, end);
        } catch (Exception e) {
            log.error("获取有序集合指定增序区间内的成员失败, key=[{}], start=[{}], end=[{}], 原因: {}", key, start, end, e);
            throw e;
        }
    }

    /**
     * 获取有序集合指定降序区间内的成员
     * (区间自动纠错)
     *
     * @param key
     * @param start
     * @param end
     */
    public Set<String> zReverseRange(String key, long start, long end) {
        try (Jedis jedis = getAndSelectRDB()) {
            return jedis.zrevrange(key, Math.min(start, end), Math.max(start, end));
        } catch (Exception e) {
            log.error("获取有序集合指定降序区间内的成员失败, key=[{}], start=[{}], end=[{}], 原因: {}", key, start, end, e);
            throw e;
        }
    }

    /**
     * 获取有序集合指定分数增序区间内的成员
     * (区间自动纠错)
     *
     * @param key
     * @param max
     * @param min
     * @return
     */
    public Set<String> zRangeByScore(String key, Double min, Double max) {
        try (Jedis jedis = getAndSelectRDB()) {
            return jedis.zrangeByScore(key, Math.min(min, max), Math.max(min, max));
        } catch (Exception e) {
            log.error("获取有序集合指定分数降序区间内的成员, key=[{}], max=[{}], min=[{}], 原因: {}", key, max, min, e);
            throw e;
        }
    }

    /**
     * 获取有序集合指定分数降序区间内的成员
     * (区间自动纠错)
     *
     * @param key
     * @param max
     * @param min
     * @return
     */
    public Set<String> zReverseRangeByScore(String key, Double max, Double min) {
        try (Jedis jedis = getAndSelectRDB()) {
            return jedis.zrevrangeByScore(key, Math.max(min, max), Math.min(min, max));
        } catch (Exception e) {
            log.error("获取有序集合指定分数降序区间内的成员, key=[{}], max=[{}], min=[{}], 原因: {}", key, max, min, e);
            throw e;
        }
    }

    /**
     * 获取有序集合成员分数值
     *
     * @param key
     * @param member
     * @return
     */
    public Double zScore(String key, String member) {
        try (Jedis jedis = getAndSelectRDB()) {
            return jedis.zscore(key, member);
        } catch (Exception e) {
            log.error("获取有序集合成员分数值失败, key=[{}], member=[{}], 原因: {}", key, member, e);
            throw e;
        }
    }

    /**
     * 批量删除移除有序集合成员
     *
     * @param key
     * @param members
     * @return
     */
    public Long zBatchDelete(String key, Set<String> members) {
        if (CollectionUtil.isEmpty(members)) {
            return 0L;
        }
        try (Jedis jedis = getAndSelectRDB()) {
            return jedis.zrem(key, members.toArray(new String[0]));
        } catch (Exception e) {
            log.error("批量删除移除有序集合成员失败, key=[{}], members=[{}], 原因: {}", key, CollectionUtil.show(members), e);
            throw e;
        }
    }

    /**
     * 批量删除移除有序集合成员
     *
     * @param key
     * @param members
     * @return
     */
    public Long zDelete(String key, Set<String> members) {
        if (CollectionUtil.isEmpty(members)) {
            return 0L;
        }
        try (Jedis jedis = getAndSelectRDB()) {
            return jedis.zrem(key, members.toArray(new String[0]));
        } catch (Exception e) {
            log.error("批量删除移除有序集合成员失败, key=[{}], members=[{}], 原因: {}", key, CollectionUtil.show(members), e);
            throw e;
        }
    }

    /**
     * 根据增序分数范围批量删除移除有序集合成员
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Long zDeleteRangeByScore(String key, Double min, Double max) {
        try (Jedis jedis = getAndSelectRDB()) {
            return jedis.zremrangeByScore(key, Math.min(min, max), Math.max(min, max));
        } catch (Exception e) {
            log.error("根据增序分数范围批量删除移除有序集合成员失败, key=[{}], min=[{}], max=[{}], 原因: {}", key, min, max, e);
            throw e;
        }
    }

    /**
     * 根据增序排名范围批量删除移除有序集合成员
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Long zDeleteRangeByRank(String key, int min, int max) {
        try (Jedis jedis = getAndSelectRDB()) {
            return jedis.zremrangeByScore(key, Math.min(min, max), Math.max(min, max));
        } catch (Exception e) {
            log.error("根据增序排名范围批量删除移除有序集合成员失败, key=[{}], min=[{}], max=[{}], 原因: {}", key, min, max, e);
            throw e;
        }
    }

    /**
     * 检查过期时间, 为null代表永久, 然后是正整数
     *
     * @param expireTime
     * @return
     */
    private boolean validateExpireTime(Integer expireTime) {
        return Objects.nonNull(expireTime) && expireTime > 0;
    }

    /**
     * 获取数据库连接
     *
     * @return
     */
    private Jedis getAndSelectRDB() {
        Jedis jedis = jedisPool.getResource();
        jedis.select(rDbIndex);
        return jedis;
    }

    /**
     * Pipeline命令数阈值检测
     *
     * @param cmds
     */
    private void preCheckPipelineCmdCount(Collection<?> cmds) {
        if (CollectionUtil.isNotEmpty(cmds) && cmds.size() > maxSinglePipelineCmdNum) {
            throw new RuntimeException("超过单次Pipeline命令阈值!");
        }
    }

}
