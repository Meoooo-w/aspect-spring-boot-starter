package com.meoooow.aspect.entity.enums;


import com.meoooow.aspect.entity.BaseCodeMsg;

public enum CodeMsgEnum implements BaseCodeMsg {
    /**
     * 系统错误
     */
    ERROR_SYSTEM(500, "系统错误"),
    /**
     * 权限验证失败
     */
    ERROR_AUTH_CHECK_FAILED(401, "权限验证失败"),
    /**
     * 成功
     */
    SUCCESS(0, "调用成功");

    private final Object code;
    private final String message;

    CodeMsgEnum(Object code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Object getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

}
