package com.meoooow.aspect.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Data
@ConfigurationProperties(prefix = "meoooow.utils.result.error")
public class ErrorCodeMapping {
    /**
     * 旧 code
     * <p>多个 code 用英文逗号分隔</p>
     * <p>ALL_TO_ONE 可以不填</p>
     */
    private String oldCode;
    /**
     * 新 code
     */
    private String newCode;
}
