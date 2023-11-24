package com.meoooow.aspect.interceptor;


import com.meoooow.aspect.entity.BaseCodeMsg;
import com.meoooow.aspect.entity.ControllerLog;
import com.meoooow.aspect.entity.ResponseProperties;
import com.meoooow.aspect.entity.enums.CodeMsgEnum;
import com.meoooow.aspect.utils.PrintLog;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;


@Slf4j
@Aspect
@Configuration
@ConditionalOnMissingBean(ControllerAspect.class)
public class ControllerAspect extends BaseAspect {
    @Resource
    private PrintLog printLog;

    @Pointcut("execution(public * *..*.controller..*Controller.*(..))")
    private void publicMethod() {
    }

    // @Around(value = "publicMethod()")
    @Around("publicMethod() && args(..)")
    private Object doAround(ProceedingJoinPoint proceedingJoinPoint) {
        return this.getResult(proceedingJoinPoint);
    }

    /**
     * 获取返回结果
     *
     * @param proceedingJoinPoint 切面对象
     * @return Object
     */
    protected Map<?, ?> getResult(ProceedingJoinPoint proceedingJoinPoint) {
        // 获取请求
        ControllerLog controllerLog = interceptor(proceedingJoinPoint);
        // 异步获取接口信息，防止报错，影响接口返回
        ControllerLog log = setResponse(controllerLog, proceedingJoinPoint);
        log.setResult(controllerLog.getResult());
        this.saveResult(log, proceedingJoinPoint.getArgs());
        // 异步打印log
        printLog.printLog(log);
        return (Map<?, ?>) controllerLog.getResult();
    }

    /**
     * 处理请求参数
     * <p>对请求中的参数，加密，解密等操作，替换原有的请求参数</p>
     *
     * @param request 请求
     * @param object  接口参数
     * @return BaseCodeMsg
     */
    @Override
    protected BaseCodeMsg preprocessingParams(HttpServletRequest request, Object[] object) {
        return CodeMsgEnum.SUCCESS;
    }

    /**
     * 保存请求结果
     *
     * @param controllerLog log
     * @param param         接口参数
     * @return boolean
     */
    @Override
    protected boolean saveResult(ControllerLog controllerLog, Object[] param) {
        return true;
    }

    /**
     * 校验token
     *
     * @param proceedingJoinPoint 切面对象
     * @return boolean
     */
    @Override
    protected boolean checkToken(ProceedingJoinPoint proceedingJoinPoint) {
        return true;
    }
}
