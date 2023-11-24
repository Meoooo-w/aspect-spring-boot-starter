package com.meoooow.aspect.entity;


import lombok.Data;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Data
public class BaseProperties<T extends BaseProperties<T>> implements Serializable {
    /**
     * 是否启用
     */
    private boolean enable;
    /**
     * 工具类id
     */
    private String utilId;
    /**
     * 编码
     */
    private Charset encoding = StandardCharsets.UTF_8;
    /**
     * 配置
     */
    private List<T> settings;

}
