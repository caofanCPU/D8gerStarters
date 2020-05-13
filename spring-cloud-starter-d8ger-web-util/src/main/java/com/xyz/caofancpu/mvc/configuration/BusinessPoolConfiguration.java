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
import com.xyz.caofancpu.property.BusinessPoolProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 业务线程池统一配置
 *
 * @author D8GER
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(name = D8gerConstants.D8_ENABLE, matchIfMissing = true)
@EnableConfigurationProperties(BusinessPoolProperties.class)
@EnableAsync
@Slf4j
public class BusinessPoolConfiguration {

    @Resource
    private BusinessPoolProperties businessPoolProperties;

    @Bean(name = "businessThreadPool")
    @ConditionalOnProperty(name = D8gerConstants.D8_BUSINESS_POOL_ENABLE, matchIfMissing = true)
    public ThreadPoolTaskExecutor standardThreadPool() {
        log.info("D8GER....执行服务线程池初始化");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(16);
        executor.setMaxPoolSize(64);
        executor.setQueueCapacity(16);
        executor.setThreadNamePrefix("debugger_");
        executor.setKeepAliveSeconds(60);
        executor.setRejectedExecutionHandler(rejectedExecutionHandler());
        // 初始化
        executor.initialize();
        log.info("D8GER....[businessThreadPool]线程池初始化完成!");
        return executor;
    }

    /**
     * rejection-policy：当pool已经达到max size的时候，如何处理新任务
     * CALLER_RUNS：不在新线程中执行任务，而是由调用者所在的线程来执行
     * 对拒绝task的处理策略
     *
     * @return
     */
    @Bean
    public RejectedExecutionHandler rejectedExecutionHandler() {
        RejectedExecutionHandler result = new ThreadPoolExecutor.CallerRunsPolicy();
        switch (businessPoolProperties.getRejectTaskPolicy()) {
            case ABORT_POLICY:
                result = new ThreadPoolExecutor.AbortPolicy();
                break;
            case DISCARD_POLICY:
                result = new ThreadPoolExecutor.DiscardPolicy();
                break;
            case DISCARD_OLDEST_POLICY:
                result = new ThreadPoolExecutor.DiscardOldestPolicy();
                break;
            default:
                break;
        }
        return result;
    }


}
