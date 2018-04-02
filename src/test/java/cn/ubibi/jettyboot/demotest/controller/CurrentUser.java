package cn.ubibi.jettyboot.demotest.controller;

import cn.ubibi.jettyboot.framework.rest.IReqParser;
import cn.ubibi.jettyboot.framework.rest.ReqParams;
import cn.ubibi.jettyboot.framework.rest.impl.RequestParamParser;

import javax.servlet.http.HttpServletRequest;

public class CurrentUser implements IReqParser{
    private String name;

    @Override
    public void doParse(ReqParams jettyBootReqParams, HttpServletRequest request, String path) throws Exception {
        this.name = jettyBootReqParams.getCookieValue("name");
        if(this.name == null){
            throw new Exception("NotLogin");
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
