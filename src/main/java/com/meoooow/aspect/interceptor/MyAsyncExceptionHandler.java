package com.meoooow.aspect.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

@Slf4j
public class MyAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    /**
     * 拦截异步方法异常
     * @param throwable throwable
     * @param method method
     * @param obj obj
     */
    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... obj) {
        log.info("Exception message - {} \nMethod name - {}" ,throwable.getMessage(),method.getName());
        for (Object param : obj) {
            log.info("Parameter value - " + param);
        }
    }
}
