package com.meoooow.aspect.entity.enums;

/**
 * 异常 code 转换规则
 */
public enum ErrorCodeMappingTypeEnum {
    /**
     * 不处理
     */
    DEFAULT,
    /**
     * 1对1转换
     */
    ONE_TO_ONE,
    /**
     * 所有异常 code 转成同一个
     */
    ALL_TO_ONE,
    /**
     * 多对1转换
     */
    MANY_TO_ONE;

    ErrorCodeMappingTypeEnum() {
    }
}
