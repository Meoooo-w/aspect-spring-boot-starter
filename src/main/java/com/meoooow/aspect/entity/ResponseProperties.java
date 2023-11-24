package com.meoooow.aspect.entity;


import com.meoooow.aspect.entity.enums.CodeTypeEnum;
import com.meoooow.aspect.entity.enums.ErrorCodeMappingTypeEnum;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Slf4j
@ConfigurationProperties(prefix = "meoooow.aspect.response")
public class ResponseProperties implements CommandLineRunner {
    @Getter
    private static Boolean enable = true;
    /**
     * code 字段名
     */
    @Getter
    private static String codeName = "code";
    /**
     * code 类型
     */
    @Getter
    private static CodeTypeEnum codeType = CodeTypeEnum.INT;
    /**
     * message 字段名
     */
    @Getter
    private static String messageName = "message";
    /**
     * data 字段名
     */
    @Getter
    private static String dataName = "data";
    /**
     * 成功 code
     */
    @Getter
    private static String success = "0";
    /**
     * 异常 code
     */
    @Getter
    private static String error = "500";
    /**
     * 异常 code 映射类型
     */
    @Getter
    private static ErrorCodeMappingTypeEnum errorMappingType = ErrorCodeMappingTypeEnum.DEFAULT;
    /**
     * 异常 code 映射关系
     */
    @Getter
    private static List<ErrorCodeMapping> errorMapping;

    public void setEnable(boolean enable) {
        if (!ResponseProperties.init) {
            ResponseProperties.enable = enable;
        }
    }

    public void setCodeName(String codeName) {
        if (!ResponseProperties.init) {
            ResponseProperties.codeName = codeName;
        }
    }

    public void setCodeType(CodeTypeEnum codeType) {
        if (!ResponseProperties.init) {
            ResponseProperties.codeType = codeType;
        }
    }

    public void setDataName(String dataName) {
        if (!ResponseProperties.init) {
            ResponseProperties.dataName = dataName;
        }
    }

    public void setErrorMapping(List<ErrorCodeMapping> errorMapping) {
        if (!ResponseProperties.init) {
            ResponseProperties.errorMapping = errorMapping;
        }
    }

    public void setErrorMappingType(ErrorCodeMappingTypeEnum errorMappingType) {
        if (!ResponseProperties.init) {
            ResponseProperties.errorMappingType = errorMappingType;
        }
    }

    public void setMessageName(String messageName) {
        if (!ResponseProperties.init) {
            ResponseProperties.messageName = messageName;
        }
    }

    public void setSuccess(String success) {
        if (!ResponseProperties.init) {
            ResponseProperties.success = success;
        }
    }

    public void setError(String error) {
        if (!ResponseProperties.init) {
            ResponseProperties.error = error;
        }
    }

    /**
     * 初始化标记
     */
    private static Boolean init = false;

    @Override
    public void run(String... args) {
        log.info("ResponseProperties init:{} ......", init);
        init = true;
        log.info("ResponseProperties init:{} ......", true);
    }
}
