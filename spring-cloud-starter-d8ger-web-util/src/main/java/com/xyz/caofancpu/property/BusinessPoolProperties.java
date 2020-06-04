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

import com.xyz.caofancpu.constant.D8gerConstants;
import com.xyz.caofancpu.constant.IEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * {@link ConfigurationProperties} for D8ger Web Util.
 *
 * @author D8GER
 */
@ConfigurationProperties(prefix = D8gerConstants.BUSINESS_POOL_PROPERTY_PREFIX)
@Validated
@Data
@Accessors(chain = true)
public class BusinessPoolProperties {
    /**
     * 核心线程数目
     */
    private int corePoolSize = 16;

    /**
     * 指定最大线程数
     */
    private int maxPoolSize = 64;

    /**
     * 队列中最大的数目
     */
    private int queueCapacity = 16;

    /**
     * 线程名称前缀
     */
    private String threadNamePrefix = "D8ger_";

    /**
     * 线程空闲后的最大存活时间
     */
    private int keepAliveSeconds = 60;

    /**
     * 线程池拒绝策略
     */
    private RejectTaskPolicyEnum rejectTaskPolicy = RejectTaskPolicyEnum.CALLER_RUNS_POLICY;

    @AllArgsConstructor
    public enum RejectTaskPolicyEnum implements IEnum {
        CALLER_RUNS_POLICY(0, "使用调用者线程"),
        ABORT_POLICY(1, "以抛异常方式直接拒绝"),
        DISCARD_POLICY(2, "静悄悄丢弃任务"),
        DISCARD_OLDEST_POLICY(3, "挤掉之前的其他任务, 再尝试运行");

        private final int value;

        private final String name;

        @Override
        public Integer getValue() {
            return this.value;
        }

        @Override
        public String getName() {
            return this.name;
        }
    }

}

