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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.xyz.caofancpu.core.CollectionUtil;
import com.xyz.caofancpu.logger.LoggerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 *
 */
@Slf4j
public class SpringRedisUtil {
    public final static int BASE_ONE_SECOND = 1;
    public final static int BASE_ONE_MINUTE = 60;
    public final static int DEFAULT_ONE_HOUR_EXPIRE = 60 * BASE_ONE_MINUTE;
    public final static int DEFAULT_ONE_DAY_EXPIRE = 24 * DEFAULT_ONE_HOUR_EXPIRE;
    public final static int DEFAULT_ONE_WEAK_EXPIRE = 7 * DEFAULT_ONE_DAY_EXPIRE;
    public final static int DEFAULT_ONE_MONTH_EXPIRE = 30 * DEFAULT_ONE_DAY_EXPIRE;
    public final static int NOT_EXPIRE = -1;
    private static StringRedisTemplate redisTemplate;

    public SpringRedisUtil(StringRedisTemplate redisTemplate) {
        SpringRedisUtil.redisTemplate = redisTemplate;
    }

    public static StringRedisTemplate getRedisTemplate() {
        return SpringRedisUtil.redisTemplate;
    }

    public static Set<String> keys(String key) {
        return getRedisTemplate().keys(key);
    }

    public static Boolean del(String format, Object... item) {
        return del(String.format(format, item));
    }

    public static Boolean del(String key) {
        return getRedisTemplate().delete(key);
    }

    public static Long deleteKeys(Collection<String> keys) {
        if (CollectionUtil.isEmpty(keys)) {
            return 0L;
        }
        return getRedisTemplate().delete(keys);
    }

    public static void set(String key, String val) {
        opsForValue().set(key, val);
        String logVal = Objects.nonNull(val) && val.length() > 500 ? val.substring(0, 500) : val;
        LoggerUtil.info(log, "设置RedisKey", "key", key, "value", logVal);
    }

    public static void set(String key, String val, Integer expire) {
        opsForValue().set(key, val, expire, TimeUnit.SECONDS);
        String logVal = Objects.nonNull(val) && val.length() > 500 ? val.substring(0, 500) : val;
        LoggerUtil.info(log, "设置RedisKey", "key", key, "value", logVal, "expireSeconds", expire);
    }

    public static boolean isExist(String key) {
        return getRedisTemplate().hasKey(key);
    }

    public static String get(String key) {
        try {
            return opsForValue().get(key);
        } catch (Exception e) {
            LoggerUtil.error(log, "get error", e, "key", key);
            return null;
        }
    }

    public static <T> T get(String key, Function<String, T> mapper) {
        String value = get(key);
        if (value == null) {
            return null;
        }
        return mapper.apply(value);
    }

    public static <T> T get(String key, TypeReference<T> typeReference) {
        String value = get(key);
        if (value == null) {
            return null;
        }
        return JSON.parseObject(value, typeReference);
    }

    public static void expire(String key, int expire) {
        getRedisTemplate().expire(key, expire, TimeUnit.SECONDS);
    }

    public static void hmset(String key, Map<String, String> hash) {
        hmset(key, hash, NOT_EXPIRE);
    }

    public static void hmset(String key, Map<String, String> hash, Integer expire) {
        opsForHash().putAll(key, hash);
        if (expire != NOT_EXPIRE) {
            expire(key, expire);
        }
    }

    public static String hget(String key, String field) {
        return opsForHash().get(key, field);
    }

    public static <T> T hget(String key, String field, Function<String, T> mapper) {
        try {
            String value = opsForHash().get(key, field);
            //noinspection ConstantConditions
            if (value == null) {
                return null;
            }
            return mapper.apply(value);
        } catch (Exception e) {
            LoggerUtil.error(log, "hget error", e, "key", key, "field", field);
        }
        return null;
    }

    public static List<String> hmget(String key, Collection<String> field) {
        return opsForHash().multiGet(key, field);
    }

//    public static <K, V> Map<K, V> hmget(String key, Collection<K> field, Function<String, V> mapper) {
//        List<String> fieldStr = CollectionUtil.transToList(field, Object::toString);
//        return CollectionUtil.transToMap(field, hmget(key, fieldStr), mapper);
//    }

    public static void hset(String key, String field, String value) {
        opsForHash().put(key, field, value);
    }

    public static void hset(String key, String field, String value, Integer expire) {
        opsForHash().put(key, field, value);
        if (expire != NOT_EXPIRE) {
            expire(key, expire);
        }
    }

    public static Boolean hsetnx(String key, String field, String value) {
        return opsForHash().putIfAbsent(key, field, value);
    }


    public static Long hdel(String key, String field) {
        return opsForHash().delete(key, field);
    }


    public static Long hdel(String key, String... field) {
        return opsForHash().delete(key, (Object[]) field);
    }

    public static Map<String, String> hgetAll(String key) {
        return opsForHash().entries(key);
    }

    public static Set<String> hkeys(String key) {
        return opsForHash().keys(key);
    }

    public static List<String> hvals(String key) {
        return opsForHash().values(key);
    }

    public static Long hincrBy(String key, String field, Integer value) {
        return opsForHash().increment(key, field, value);
    }

    public static boolean setnx(String key, String value, int second) {
        return opsForValue().setIfAbsent(key, value, second, TimeUnit.SECONDS);
    }


    public static Long lpush(String key, String value) {
        return opsForList().leftPush(key, value);
    }

    public static String lpop(String key) {
        return opsForList().leftPop(key);
    }

    public static Long rpush(String key, String... value) {
        return opsForList().rightPushAll(key, value);
    }

    public static String rpop(String key) {
        return opsForList().rightPop(key);
    }

    public static Long sadd(String key, String... values) {
        return opsForSet().add(key, values);
    }

    public static Set<String> smembers(String key) {
        return opsForSet().members(key);
    }


    private static HashOperations<String, String, String> opsForHash() {
        return getRedisTemplate().opsForHash();
    }

    private static ValueOperations<String, String> opsForValue() {
        return getRedisTemplate().opsForValue();
    }

    private static SetOperations<String, String> opsForSet() {
        return getRedisTemplate().opsForSet();
    }

    private static ListOperations<String, String> opsForList() {
        return getRedisTemplate().opsForList();
    }

    public static <T> T write(RedisHandle<T> handle) {
        return handle.handle(getRedisTemplate());
    }

    public interface RedisHandle<T> {
        T handle(StringRedisTemplate redisTemplate);
    }
}

