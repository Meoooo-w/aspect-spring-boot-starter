package com.meoooow.aspect.entity.enums;

import lombok.Getter;

@Getter
public enum CodeTypeEnum {
    /**
     * 字符串
     */
    STRING("String"),
    /**
     * 整型
     */
    INT("int");

    private final String typeName;

    CodeTypeEnum(String typeName) {
        this.typeName = typeName;
    }
}