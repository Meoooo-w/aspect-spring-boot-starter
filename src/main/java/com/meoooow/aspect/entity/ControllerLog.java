package com.meoooow.aspect.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ControllerLog extends BaseLog {

    private String ip;
    private String executing;

}
