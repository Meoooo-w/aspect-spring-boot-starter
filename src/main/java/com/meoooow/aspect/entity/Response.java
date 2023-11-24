package com.meoooow.aspect.entity;

import com.google.common.collect.Maps;
import com.meoooow.aspect.entity.enums.CodeTypeEnum;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

@Data
public class Response implements BaseCodeMsg {
    static <T> Map<Object, Object> setMap(Object code, String message, T data) {
        Map<Object, Object> result = Maps.newHashMap();
        result.put(ResponseProperties.getCodeName(), ResponseProperties.getCodeType() == CodeTypeEnum.INT ? parseNumber(String.valueOf(code)) : code);
        if (StringUtils.isNotBlank(message)) {
            result.put(ResponseProperties.getMessageName(), message);
        }
        if (data != null) {
            result.put(ResponseProperties.getDataName(), data);
        }
        return result;
    }

    public static int parseNumber(String number) {
        if (StringUtils.isNotBlank(number)) {
            return number.matches("-?[0-9]+.?[0-9]*") ? Integer.parseInt(number) : 0;
        }
        return 0;
    }

    public static Map<Object, Object> success() {
        return setMap(ResponseProperties.getSuccess(), SUCCESS_MSG, null);
    }

    public static Map<Object, Object> success(BaseCodeMsg baseCodeMsg) {
        return setMap(baseCodeMsg.getCode(), baseCodeMsg.getMessage(), null);
    }

    public static <T> Map<Object, Object> success(T data) {
        return setMap(ResponseProperties.getSuccess(), SUCCESS_MSG, data);
    }

    public static <T> Map<Object, Object> success(Object code, T data) {
        return setMap(code, SUCCESS_MSG, data);
    }

    public static <T> Map<Object, Object> success(Object code, String message, T data) {
        return setMap(code, message, data);
    }

    public static Map<Object, Object> error() {
        return setMap(ResponseProperties.getError(), ERROR_MSG, null);
    }

    public static Map<Object, Object> error(BaseCodeMsg baseCodeMsg) {
        return setMap(baseCodeMsg.getCode(), baseCodeMsg.getMessage(), null);
    }

    public static Map<Object, Object> error(String message) {
        return setMap(ResponseProperties.getError(), message, null);
    }

    public static Map<Object, Object> error(Object code, String message) {
        return setMap(code, message, null);
    }

    public static <T> Map<Object, Object> error(Object code, String message, T data) {
        return setMap(code, message, data);
    }

}
