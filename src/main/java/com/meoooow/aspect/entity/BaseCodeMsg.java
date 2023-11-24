package com.meoooow.aspect.entity;

public interface BaseCodeMsg {
    int SUCCESS = 0;
    String SUCCESS_MSG = "调用成功";
    int ERROR = 500;
    String ERROR_MSG = "接口异常";

    default Object getCode() {
        return SUCCESS;
    }

    default String getMessage() {
        return SUCCESS_MSG;
    }
}
