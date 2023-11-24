package com.meoooow.aspect.config.async;

import com.meoooow.aspect.entity.ExecutorProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;

/**
 * PrintLogAsyncConfig 日志拦截异步线程池
 * 如果有大量需要异步请求的方法，应当根据实际情况修改以下配置
 * <p>
 * <p>
 * 默认情况下，在创建了线程池后，线程池中的线程数为0，当有任务来之后，就会创建一个线程去执行任务，
 * 当线程池中的线程数目达到 CORE_POOL_SIZE 后，就会把到达的任务放到缓存队列当中；
 * 当队列满了，就继续创建线程，当线程数量大于等于 MAX_POOL_SIZE 后，开始使用拒绝策略拒绝
 */
@EnableAsync
@Slf4j
@EnableConfigurationProperties(ExecutorProperties.class)
public class PrintLogAsyncConfig {

    @Resource
    ExecutorProperties executorProperties;

    /**
     * 线程池名前缀
     */
    private static final String THREAD_PREFIX = "meoooow-print-log-";

    /**
     * 自定义异步线程池
     *
     * @return executor
     */
    @Bean
    public Executor meoooowPrintLogExecutor() {
        ExecutorProperties asyncExecutorProperties = executorProperties == null ? new ExecutorProperties() : executorProperties;
        asyncExecutorProperties.setThreadPrefix(THREAD_PREFIX);
        log.debug("PrintLogAsyncConfig:{}", asyncExecutorProperties);
        return MyExecutor.getThreadPoolTaskExecutor(asyncExecutorProperties);
    }
}