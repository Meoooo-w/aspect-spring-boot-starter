package com.meoooow.aspect.interceptor;

import com.google.common.collect.Maps;
import com.meoooow.aspect.entity.*;
import com.meoooow.aspect.entity.enums.CodeMsgEnum;
import com.meoooow.aspect.entity.enums.CodeTypeEnum;
import com.meoooow.aspect.exception.ServiceException;
import com.meoooow.aspect.utils.PrintLog;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 通用拦截器
 */
@Slf4j
public abstract class BaseAspect {
    @Resource
    private PrintLog printLog;

    /**
     * 自定义响应结果
     * @return JSONObject
     */
    static Map<Object, Object> convertResult(Map<Object, Object> response) {
        Map<Object, Object> map = Maps.newHashMap();
        // 判断结果是成功，还是失败
        String code = String.valueOf(response.get(ResponseProperties.getCodeName()));
        if (!StringUtils.equals(code, ResponseProperties.getSuccess())) {
            if (CollectionUtils.isNotEmpty(ResponseProperties.getErrorMapping())) {
                ErrorCodeMapping errorCodeMapping = null;
                String finalCode = code;
                switch (ResponseProperties.getErrorMappingType()) {
                    case ONE_TO_ONE -> errorCodeMapping = ResponseProperties.getErrorMapping()
                            .stream()
                            .filter(mapping -> StringUtils.equals(finalCode, mapping.getOldCode()))
                            .findFirst()
                            .orElse(null);
                    case MANY_TO_ONE ->
                            errorCodeMapping = ResponseProperties.getErrorMapping().stream().filter(mapping -> StringUtils.equalsAny(finalCode, StringUtils.split(mapping.getOldCode(), ",")))
                                    .findFirst()
                                    .orElse(null);
                    case ALL_TO_ONE -> errorCodeMapping = ResponseProperties.getErrorMapping().get(0);
                }
                if (errorCodeMapping != null && StringUtils.isNotBlank(errorCodeMapping.getNewCode())) {
                    // code 转换
                    code = errorCodeMapping.getNewCode();
                }
            }
        }
        // 判断结果类型
        map.put(ResponseProperties.getCodeName(), ResponseProperties.getCodeType() == CodeTypeEnum.INT ? Response.parseNumber(code) : code);
        return map;
    }



    /**
     * 拦截请求
     *
     * @param proceedingJoinPoint 切面对象
     * @return R
     */
    protected ControllerLog setResponse(ControllerLog controllerLog, ProceedingJoinPoint proceedingJoinPoint) {
        // 获取 controller 执行结果
        HttpServletRequest request = getHttpServletRequest();
        // 异步获取请求中的内容
        Future<ControllerLog> futureControllerLog = printLog.syncInitControllerLog(proceedingJoinPoint, request);
        ControllerLog temp = new ControllerLog();
        try {
            // 同步 controllerLog 结果
            temp = futureControllerLog.get();
            temp.setStartTime(controllerLog.getStartTime());
            temp.setEndTime(LocalDateTime.now());
            temp.setThrowable(controllerLog.getThrowable());
        } catch (InterruptedException | ExecutionException e) {
            log.error("异步输出log异常：", e);
        }
        return temp;
    }

    /**
     * 拦截请求
     *
     * @param proceedingJoinPoint 切面对象
     * @return R
     */
    protected ControllerLog interceptor(ProceedingJoinPoint proceedingJoinPoint) {
        // 获取 controller 执行结果
        Map<?, ?> r = null;
        Throwable throwable = null;
        HttpServletRequest request = getHttpServletRequest();
        // 异步获取请求中的内容
        LocalDateTime startTime = LocalDateTime.now();
        ControllerLog controllerLog = new ControllerLog();
        try {
            // 校验token
            if (!checkToken(proceedingJoinPoint)) {
                throw ServiceException.getInstance(CodeMsgEnum.ERROR_AUTH_CHECK_FAILED);
            }
            // 参数处理
            if (request != null) {
                BaseCodeMsg paramsMsg = preprocessingParams(request, proceedingJoinPoint.getArgs());
                if (!StringUtils.equals(paramsMsg.getCode().toString(), String.valueOf(BaseCodeMsg.SUCCESS))) {
                    throw ServiceException.getInstance(paramsMsg);
                }
            }
            // 执行请求
            if (proceedingJoinPoint.proceed() instanceof Map<?, ?> temp) {
                r = temp;
            } else {
                throw ServiceException.getInstance(ResponseProperties.getError(), "接口返回类型异常");
            }
        } catch (Throwable th) {
            // 拦截异常
            throwable = th;
            if (throwable instanceof ServiceException) {
                r = Response.error(((ServiceException) throwable).getCode(), throwable.getMessage());
            } else {
                // 系统级错误记录到 error 中
                r = Response.error(CodeMsgEnum.ERROR_SYSTEM);
            }
        } finally {
            // 同步 controllerLog 结果
            controllerLog.setStartTime(startTime);
            controllerLog.setResult(r);
            controllerLog.setThrowable(throwable);
        }
        return controllerLog;
    }

    /**
     * 获取 HttpServletRequest
     *
     * @return HttpServletRequest
     */
    private HttpServletRequest getHttpServletRequest() {
        HttpServletRequest request = null;
        if (RequestContextHolder.getRequestAttributes() != null) {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            request = attributes.getRequest();
        }
        return request;
    }

    /**
     * 接口参数处理
     *
     * @param request 请求
     * @param object  接口参数
     * @return 处理结果
     */
    protected abstract BaseCodeMsg preprocessingParams(HttpServletRequest request, Object[] object);

    /**
     * 保存接口请求记录
     *
     * @param controllerLog log
     * @param param         接口参数
     * @return 保存结果
     */
    protected abstract boolean saveResult(ControllerLog controllerLog, Object[] param);

    /**
     * 保存返回值
     *
     * @param proceedingJoinPoint 切面对象
     * @return 校验结果
     */
    protected abstract boolean checkToken(ProceedingJoinPoint proceedingJoinPoint);
}
