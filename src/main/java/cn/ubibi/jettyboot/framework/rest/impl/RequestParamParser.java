package cn.ubibi.jettyboot.framework.rest.impl;

import cn.ubibi.jettyboot.framework.commons.BeanUtils;
import cn.ubibi.jettyboot.framework.rest.IReqParser;
import cn.ubibi.jettyboot.framework.rest.ReqParams;

import javax.servlet.http.HttpServletRequest;


public class RequestParamParser implements IReqParser {
    @Override
    public void doParse(ReqParams jettyBootReqParams, HttpServletRequest request, String path) {
        Object obj = jettyBootReqParams.getRequestParamObject(this.getClass());
        BeanUtils.copyField(this, obj);
    }
}