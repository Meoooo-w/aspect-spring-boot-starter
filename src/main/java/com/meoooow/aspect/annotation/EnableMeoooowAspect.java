package com.meoooow.aspect.annotation;

import com.meoooow.aspect.config.async.PrintLogAsyncConfig;
import com.meoooow.aspect.config.date.JavaTimeAutoConfiguration;
import com.meoooow.aspect.entity.ErrorCodeMapping;
import com.meoooow.aspect.entity.ResponseProperties;
import com.meoooow.aspect.interceptor.ControllerAspect;
import com.meoooow.aspect.interceptor.ControllerExceptionHandler;
import com.meoooow.aspect.utils.PrintLog;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({ResponseProperties.class,
        ErrorCodeMapping.class,
        PrintLog.class,
        PrintLogAsyncConfig.class,
        JavaTimeAutoConfiguration.class,
        ControllerExceptionHandler.class,
        ControllerAspect.class})
public @interface EnableMeoooowAspect {
}
