package com.meoooow.aspect.interceptor;


import com.meoooow.aspect.entity.Response;
import com.meoooow.aspect.exception.ServiceException;
import com.meoooow.aspect.utils.PrintLog;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

import java.util.Map;


/**
 * 全局异常处理
 */
@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {
    @Resource
    private PrintLog printLog;

    protected static Object errorData;

    private Map<Object, Object> getR(Object status, String message) {
        Map<Object, Object> response = BaseAspect.convertResult(Response.error(status, message, errorData));
        printLog.printLog(response, message);
        return response;
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(ServiceException.class)
    public Object exceptionHandler(ServiceException e) {
        return getR(e.getCode(), e.getMessage());
    }

    //------------------------------------------------------------------------------------------------------------------
    // 1. 路径
    // 2. 请求方法
    // 3. MediaType
    // 4. 缺少参数 / 参数类型
    //------------------------------------------------------------------------------------------------------------------

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public Map<Object, Object> handlerNoFoundException(NoHandlerFoundException e) {
        return getR(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Map<Object, Object> httpMethodException(Exception e) {
        return getR(HttpStatus.METHOD_NOT_ALLOWED.value(), e.getMessage());
    }


    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(HttpMediaTypeException.class)
    public Map<Object, Object> mediaTypeException(Exception e) {
        return getR(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), e.getMessage());
    }

    /**
     * 缺少请求参数 / 参数类型错误
     *
     * @param e e
     * @return R
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class})
    public Map<Object, Object> handleMissingServletRequestParameterException(Exception e) {
        String subTitle = null;
        // MethodArgumentTypeMismatchException 堆栈看不出访问的那个 controller 方法
        if (e instanceof MethodArgumentTypeMismatchException) {
            try {
                subTitle = ((MethodArgumentTypeMismatchException) e).getParameter().getExecutable().toString();
            } catch (Exception ignored) {
            }
        }
        subTitle = subTitle == null ? "" : ": ";
        String msg = subTitle + e.getMessage();
        return getR(HttpStatus.BAD_REQUEST.value(), msg);
    }

    /**
     * hibernate validator 数据绑定验证异常拦截
     * 参数转换 NumberFormatException 也走这里.
     *
     * @param e 绑定验证异常
     * @return 错误返回消息
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public Map<Object, Object> validateErrorHandler(BindException e) {
        ObjectError error = e.getAllErrors().get(0);
        log.error("请求参数校验未通过: {}", error);
        return getR(HttpStatus.BAD_REQUEST.value(), error.toString());
    }

    /**
     * hibernate validator 数据绑定验证异常拦截
     * `@RequestBody` 走这里, 否则走 BindException .
     *
     * @param e 绑定验证异常
     * @return 错误返回消息
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<Object, Object> validateErrorHandler(MethodArgumentNotValidException e) {
        ObjectError error = e.getBindingResult().getAllErrors().get(0);
        log.error("请求参数校验未通过: {}", error);
        return getR(HttpStatus.BAD_REQUEST.value(), error.toString());
    }

    /**
     * 需要的请求头为空
     *
     * @param e HttpMessageNotReadableException
     * @return R
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Map<Object, Object> validateErrorHandler(HttpMessageNotReadableException e) {
        return getR(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    /**
     * spring 默认上传大小100MB 超出大小捕获异常 MaxUploadSizeExceededException
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Map<Object, Object> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        return getR(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DuplicateKeyException.class)
    public Map<Object, Object> handleDuplicateKeyException(DuplicateKeyException e) {
        return getR(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Map<Object, Object> handleException(Exception e) {
        return getR(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
    }
}