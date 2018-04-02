package cn.ubibi.jettyboot.demotest.controller;

import cn.ubibi.jettyboot.framework.rest.IReqParamParser;
import cn.ubibi.jettyboot.framework.rest.impl.RequestParamParser;

public class UserInfoParser implements IReqParamParser{

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
