package com.meoooow.aspect.exception;

import com.meoooow.aspect.entity.BaseCodeMsg;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;


@EqualsAndHashCode(callSuper = true)
@Data
public class ServiceException extends RuntimeException implements Serializable {
    
    /**
     * 错误码
     */
    private Object code;
    /**
     * 错误描述
     */
    private String message;


    /**
     * 自定义错误
     * @param code 错误码
     * @param message 错误描述
     */
    ServiceException(Object code,String message){
        this.code = code;
        this.message = message;
    }

    /**
     * 获取异常内容
     * @return String
     */
    @Override
    public String getMessage() {
        return StringUtils.isBlank(this.message) ? super.getMessage(): this.message;
    }

    /**
     * 获取异常
     * @param code code
     * @param message message
     * @return ServiceException
     */
    public static ServiceException getInstance(Object code, String message){
        return new ServiceException(code,message);
    }

    /**
     * 获取异常
     * @param baseCodeMsg baseCodeMsg
     * @return ServiceException
     */
    public static ServiceException getInstance(BaseCodeMsg baseCodeMsg){
        return new ServiceException(baseCodeMsg.getCode(),baseCodeMsg.getMessage());
    }

}
