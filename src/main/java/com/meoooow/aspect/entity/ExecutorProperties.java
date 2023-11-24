package com.meoooow.aspect.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "meoooow.executor")
public class ExecutorProperties {
    /**
     * 核心线程数（默认线程数）
     */
    private Integer corePoolSize = Runtime.getRuntime().availableProcessors();
    /**
     * 最大线程数
     */
    private Integer maxPoolSize = 50;
    /**
     * 缓冲队列大小,只有缓冲被占满时，才会创建新的线程
     */
    private Integer queueCapacity = 1000;
    /**
     * 允许线程空闲时间（单位：默认为秒）
     */
    private Integer keepAliveTime = 60;
    /**
     * 线程池名前缀
     */
    private String threadPrefix = "meoooow-async-";
}
