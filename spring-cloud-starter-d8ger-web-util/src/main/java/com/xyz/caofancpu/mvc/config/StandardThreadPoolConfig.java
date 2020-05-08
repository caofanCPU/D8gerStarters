package com.xyz.caofancpu.mvc.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 标准线程池统一配置
 *
 * @author D8GER
 */
@Configuration
@EnableAsync
@Slf4j
public class StandardThreadPoolConfig {
    @Bean(name = "standardThreadPool")
    public ThreadPoolTaskExecutor standardThreadPool() {
        log.info("初始化服务线程池");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数目
        executor.setCorePoolSize(16);
        // 指定最大线程数
        executor.setMaxPoolSize(64);
        // 队列中最大的数目
        executor.setQueueCapacity(16);
        // 线程名称前缀
        executor.setThreadNamePrefix("debugger_");
        // 线程池拒绝策略
        executor.setRejectedExecutionHandler(rejectedExecutionHandler());
        // 线程空闲后的最大存活时间
        executor.setKeepAliveSeconds(60);
        // 初始化
        executor.initialize();
        log.info("完成服务线程池启动");
        return executor;
    }

    @Bean
    public RejectedExecutionHandler rejectedExecutionHandler() {
        //rejection-policy：当pool已经达到max size的时候，如何处理新任务
        //CALLER_RUNS：不在新线程中执行任务，而是由调用者所在的线程来执行
        //对拒绝task的处理策略
        return new ThreadPoolExecutor.CallerRunsPolicy();
    }


}
