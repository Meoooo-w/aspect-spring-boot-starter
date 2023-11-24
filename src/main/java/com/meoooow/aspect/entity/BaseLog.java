package com.meoooow.aspect.entity;

import com.meoooow.aspect.utils.PrintLog;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
public class BaseLog {
    private String requestUrl;
    private String httpMethod;
    private Map<String, String> header;
    private Object params;
    private Object result;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Throwable throwable;
}
