package com.meoooow.aspect.utils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.meoooow.aspect.entity.BaseLog;
import com.meoooow.aspect.entity.ControllerLog;
import com.meoooow.aspect.entity.RequestLog;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class PrintLog {
    private static final String UNKNOWN = "unknown";

    /**
     * 初始化 ControllerLog
     * <p>异步调用</p>
     *
     * @param proceedingJoinPoint aop
     * @param request             HttpServletRequest
     * @return Future<ControllerLog>
     */
    @Async("meoooowPrintLogExecutor")
    public CompletableFuture<ControllerLog> syncInitControllerLog(ProceedingJoinPoint proceedingJoinPoint, HttpServletRequest request) {
        return CompletableFuture.completedFuture(getControllerLog(proceedingJoinPoint, request));
    }

    /**
     * 初始化 ControllerLog
     * <p>同步调用</p>
     *
     * @param proceedingJoinPoint aop
     * @param request             HttpServletRequest
     * @return ControllerLog
     */
    @SuppressWarnings("unused")
    public ControllerLog initControllerLog(ProceedingJoinPoint proceedingJoinPoint, HttpServletRequest request) {
        return getControllerLog(proceedingJoinPoint, request);
    }

    /**
     * 解析请求和响应
     */
    @Async("meoooowPrintLogExecutor")
    public void printLog(BaseLog baseLog) {
        StringBuilder string = new StringBuilder();
        string.append("\n----------------------------------------------------------------");
        string.append("\r\n RequestUrl : ").append(baseLog.getRequestUrl() == null ? "" : baseLog.getRequestUrl())
                .append("\r\n HttpMethod : ").append(baseLog.getHttpMethod() == null ? "" : baseLog.getHttpMethod())
                .append("\r\n Header     : ").append(baseLog.getHeader() == null ? "" : JSON.toJSONString(baseLog.getHeader()));
        if (baseLog instanceof ControllerLog) {
            string.append("\r\n IP         : ").append(((ControllerLog) baseLog).getIp() == null ? "" : ((ControllerLog) baseLog).getIp())
                    .append("\r\n Executing  : ").append(((ControllerLog) baseLog).getExecuting() == null ? "" : ((ControllerLog) baseLog).getExecuting());
        }
        if (baseLog.getParams() instanceof String) {
            string.append("\r\n Params     : ").append(baseLog.getParams() == null ? "" : baseLog.getParams());
        } else {
            string.append("\r\n Params     : ").append(baseLog.getParams() == null ? "" : JSON.toJSONString(baseLog.getParams()));
        }
        if (baseLog.getResult() instanceof String) {
            string.append("\r\n Result     : ").append(baseLog.getResult() == null ? "" : baseLog.getResult());
        } else {
            string.append("\r\n Result     : ").append(baseLog.getResult() == null ? "" : JSON.toJSONString(baseLog.getResult()));
        }
        string.append("\r\n StartTime  : ").append(DateUtils.format(baseLog.getStartTime(), DateUtils.YYYY_MM_DD_HH_MM_SS_SSS))
                .append("\r\n EndTime    : ").append(DateUtils.format(baseLog.getEndTime(), DateUtils.YYYY_MM_DD_HH_MM_SS_SSS))
                .append("\r\n Took       : ").append(baseLog.getStartTime().until(baseLog.getEndTime(), ChronoUnit.MILLIS)).append(" ms")
                .append("\r\n Exception  : ").append(baseLog.getThrowable() == null ? "" : ExceptionUtils.getStackTrace(baseLog.getThrowable()));
        string.append("\n----------------------------------------------------------------");
        log.info(string.toString());
    }

    /**
     * 解析请求和响应
     */
    @Async("meoooowPrintLogExecutor")
    public void printLog(Map<Object, Object> response, String errorMessage) {
        log.info("""

                ----------------------------------------------------------------
                 Result     : {}
                 Exception  : {}
                ----------------------------------------------------------------""", JSON.toJSONString(response), errorMessage);
    }

    /**
     * 打印 Log
     * 同步调用
     *
     * @param startTime 请求开始时间
     * @param url       请求路径
     * @param method    请求类型
     * @param header    请求头
     * @param params    请求参数
     * @param result    响应结果
     * @param exception 异常信息
     */
    @Async("meoooowPrintLogExecutor")
    public void printLog(LocalDateTime startTime, String url, String method, Map<String, String> header, Object params, String result, Exception exception) {
        RequestLog requestLog = new RequestLog();
        requestLog.setStartTime(startTime);
        requestLog.setEndTime(LocalDateTime.now());
        requestLog.setRequestUrl(url);
        requestLog.setHttpMethod(method);
        requestLog.setHeader(header);
        requestLog.setParams(params);
        requestLog.setResult(result);
        if (exception != null) {
            requestLog.setThrowable(exception);
        }
        printLog(requestLog);
    }


    /**
     * 获取 ControllerLog
     *
     * @param proceedingJoinPoint aop
     * @param request             HttpServletRequest
     * @return ControllerLog
     */
    private ControllerLog getControllerLog(ProceedingJoinPoint proceedingJoinPoint, HttpServletRequest request) {
        ControllerLog controllerLog = new ControllerLog();
        controllerLog.setStartTime(LocalDateTime.now());
        controllerLog.setExecuting(PrintLog.getExecuting(proceedingJoinPoint));
        if (request != null) {
            try {
                controllerLog.setHttpMethod(request.getMethod());
                controllerLog.setParams(PrintLog.getParamsString(request,
                        ((MethodSignature) proceedingJoinPoint.getSignature()).getParameterNames(),
                        proceedingJoinPoint.getArgs()));
                controllerLog.setRequestUrl(String.valueOf(request.getRequestURL()));
                controllerLog.setIp(PrintLog.getClientIpAddr(request));
                controllerLog.setHeader(PrintLog.getHeaderString(request));
            } catch (Exception exception) {
                log.error("获取 request 数据异常：" + exception.getMessage());
            }
        }
        return controllerLog;
    }

    /**
     * 获取包路径和类名
     *
     * @param proceedingJoinPoint aop
     * @return String
     */
    private static String getExecuting(ProceedingJoinPoint proceedingJoinPoint) {
        String[] packageName = proceedingJoinPoint.getSignature().getDeclaringTypeName().split("\\.");
        StringBuilder executing = new StringBuilder();
        for (int i = 0; i < packageName.length; ++i) {
            if (i < packageName.length - 1) {
                executing.append(packageName[i], 0, 1);
            } else {
                executing.append(packageName[i]);
            }
            executing.append(".");
        }
        return executing + proceedingJoinPoint.getSignature().getName();
    }

    /***
     * 获取客户端ip地址(可以穿透代理)
     * @param request 请求
     * @return String
     */
    private static String getClientIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        log.debug("X-Forwarded-For:" + ip);
        if (ip == null || ip.isEmpty() || StringUtils.equalsIgnoreCase(UNKNOWN, ip)) {
            ip = request.getHeader("Proxy-Client-IP");
            log.debug("Proxy-Client-IP:" + ip);
        }
        if (ip == null || ip.isEmpty() || StringUtils.equalsIgnoreCase(UNKNOWN, ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
            log.debug("WL-Proxy-Client-IP:" + ip);
        }
        if (ip == null || ip.isEmpty() || StringUtils.equalsIgnoreCase(UNKNOWN, ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            log.debug("HTTP_X_FORWARDED_FOR:" + ip);
        }
        if (ip == null || ip.isEmpty() || StringUtils.equalsIgnoreCase(UNKNOWN, ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED");
            log.debug("HTTP_X_FORWARDED:" + ip);
        }
        if (ip == null || ip.isEmpty() || StringUtils.equalsIgnoreCase(UNKNOWN, ip)) {
            ip = request.getHeader("HTTP_X_CLUSTER_CLIENT_IP");
            log.debug("HTTP_X_CLUSTER_CLIENT_IP:" + ip);
        }
        if (ip == null || ip.isEmpty() || StringUtils.equalsIgnoreCase(UNKNOWN, ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
            log.debug("HTTP_CLIENT_IP:" + ip);
        }
        if (ip == null || ip.isEmpty() || StringUtils.equalsIgnoreCase(UNKNOWN, ip)) {
            ip = request.getHeader("HTTP_FORWARDED_FOR");
            log.debug("HTTP_FORWARDED_FOR:" + ip);
        }
        if (ip == null || ip.isEmpty() || StringUtils.equalsIgnoreCase(UNKNOWN, ip)) {
            ip = request.getHeader("HTTP_FORWARDED");
            log.debug("HTTP_FORWARDED:" + ip);
        }
        if (ip == null || ip.isEmpty() || StringUtils.equalsIgnoreCase(UNKNOWN, ip)) {
            ip = request.getHeader("HTTP_VIA");
            log.debug("HTTP_VIA:" + ip);
        }
        if (ip == null || ip.isEmpty() || StringUtils.equalsIgnoreCase(UNKNOWN, ip)) {
            ip = request.getHeader("REMOTE_ADDRESS");
            log.debug("REMOTE_ADDRESS:" + ip);
        }
        if (ip == null || ip.isEmpty() || StringUtils.equalsIgnoreCase(UNKNOWN, ip)) {
            ip = request.getRemoteAddr();
            log.debug("getRemoteAddress:" + ip);
        }
        return ip;
    }

    /**
     * 获取请求中header的参数
     *
     * @param request 请求
     * @return String
     */
    public static Map<String, String> getHeaderString(HttpServletRequest request) {
        Map<String, String> param = Maps.newConcurrentMap();
        //获取请求参数
        Enumeration<?> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            param.put(key, value);
        }
        return param;
    }

    /**
     * 获取请求中的参数
     *
     * @param request 请求
     * @param objects controller 接收到的参数
     * @return String
     */
    public static Map<String, Object> getParamsString(HttpServletRequest request, String[] argNames, Object[] objects) {
        Map<String, Object> param = Maps.newConcurrentMap();
        // 获取请求参数
        if (argNames != null && objects != null) {
            for (int i = 0; i < argNames.length; i++) {
                param.put(argNames[i], objects[i] == null ? "" : objects[i].toString());
            }
        } else {
            request.getParameterMap().forEach((k, v) -> param.put(k, request.getParameter(k)));
        }
        return param;
    }

}
