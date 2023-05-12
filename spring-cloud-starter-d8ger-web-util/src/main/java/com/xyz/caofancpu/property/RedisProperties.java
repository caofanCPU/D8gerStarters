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

package com.xyz.caofancpu.property;

import com.xyz.caofancpu.annotation.WarnDoc;
import com.xyz.caofancpu.constant.D8gerConstants;
import com.xyz.caofancpu.constant.SymbolConstantUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * {@link ConfigurationProperties} for D8ger Web Util.
 *
 * @author D8GER
 */
@ConfigurationProperties(prefix = D8gerConstants.REDIS_PROPERTY_PREFIX)
@Validated
@Data
@Accessors(chain = true)
public class RedisProperties {
    /**
     * IP
     */
    private String ip = "127.0.0.0";

    /**
     * 端口
     */
    private int port = 6379;

    /**
     * 密码
     */
    private String pwd = SymbolConstantUtil.EMPTY;

    /**
     * 数据库索引, 默认0号库
     */
    private int rDbIndex = 0;

    /**
     * 单次PipeLine最大命令数, 默认10w条
     */
    @WarnDoc("生产环境, 请确认合理值")
    private int maxSinglePipelineCmdNum = 100000;

    /**
     * 最大初始化启动时间
     */
    private int maxInitStartMillis = 2000;

    /**
     * 最大连接数
     */
    private int maxTotal = 256;

    /**
     * 最大空闲连接数
     */
    private int maxIdle = 256;

    /**
     * 最小空闲连接数
     */
    private int minIdle = 15;

    /**
     * 获取连接时最大等待时间, 默认1秒
     */
    private long maxWaitMillis = 1000;

    /**
     * 保证获取的是有效连接
     */
    private boolean testOnBorrow = true;

    /**
     * 开启一个空闲连接检测线程
     */
    private boolean testWhiledIdle = true;

    /**
     * 空闲连接检测线程每次扫描最多检查空闲连接数, 默认5个
     */
    private int numTestsPerEvictionRun = 5;

    /**
     * 空闲连接检测线程扫描间隔, 默认3秒
     */
    private long timeBetweenEvictionRunsMillis = 3000;

    /**
     * 连接空闲时间大于该阈值时被干掉, 默认3秒
     */
    private long minEvictableIdleTimeMillis = 3000;

    /**
     * 连接空闲时间大于该阈值且空闲连接大于{@link #minIdle}时被干掉, 默认1秒
     */
    private long softMinEvictableIdleTimeMillis = 1000;

}
