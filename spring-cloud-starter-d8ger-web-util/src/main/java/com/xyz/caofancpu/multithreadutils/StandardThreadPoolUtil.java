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

package com.xyz.caofancpu.multithreadutils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;


/**
 * 线程池
 *
 * @author D8GER
 */
@Slf4j
@Deprecated
public class StandardThreadPoolUtil {
    private static volatile ThreadPoolTaskExecutor threadPoolTaskExecutor;

    private StandardThreadPoolUtil() {}

    public static ThreadPoolTaskExecutor getInstance() {
        if (threadPoolTaskExecutor == null) {
            synchronized (ThreadPoolTaskExecutor.class) {
                if (threadPoolTaskExecutor == null) {
                    threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
                    init(threadPoolTaskExecutor);
                }
            }
        }
        return threadPoolTaskExecutor;
    }

    private static void init(ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        //核心线程数
        threadPoolTaskExecutor.setCorePoolSize(16);
        //最大线程数
        threadPoolTaskExecutor.setMaxPoolSize(64);
        //队列最大数
        threadPoolTaskExecutor.setQueueCapacity(16);
        //线程名称前缀
        threadPoolTaskExecutor.setThreadNamePrefix("debugger_");
        /**
         * rejection-policy：当pool已经达到max size的时候，如何处理新任务
         * CALLER_RUNS：不在新线程中执行任务，而是由调用者所在的线程来执行
         * 对拒绝task的处理策略
         */
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //线程空闲后的最大存活时间
        threadPoolTaskExecutor.setKeepAliveSeconds(60);
        //加载
        threadPoolTaskExecutor.initialize();
    }

}
