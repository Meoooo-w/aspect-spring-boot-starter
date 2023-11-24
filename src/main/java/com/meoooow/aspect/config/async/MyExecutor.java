package com.meoooow.aspect.config.async;

import com.meoooow.aspect.entity.ExecutorProperties;
import com.meoooow.aspect.interceptor.MyAsyncExceptionHandler;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;


public class MyExecutor {

    /**
     * 自定义异步异常拦截器
     * @return AsyncUncaughtExceptionHandler
     */
    public static AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new MyAsyncExceptionHandler();
    }

    /**
     * 自定义异步线程池
     * @param executorProperties 线程池配置
     * @return executor
     */
    public static Executor getThreadPoolTaskExecutor(ExecutorProperties executorProperties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix(executorProperties.getThreadPrefix());
        // 核心线程数，默认为1
        executor.setCorePoolSize(executorProperties.getCorePoolSize());
        // 最大线程数，默认为Integer.MAX_VALUE
        executor.setMaxPoolSize(executorProperties.getMaxPoolSize());
        // 等待队列数
        executor.setQueueCapacity(executorProperties.getQueueCapacity());
        // 线程池维护线程所允许的空闲时间，默认为60s
        executor.setKeepAliveSeconds(executorProperties.getKeepAliveTime());
        // CallerRunsPolicy: 主线程直接执行该任务，执行完之后尝试添加下一个任务到线程池中，可以有效降低向线程池内添加任务的时间
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

}
