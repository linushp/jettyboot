package cn.ubibi.jettyboot.framework.rest.impl;

import cn.ubibi.jettyboot.framework.commons.BeanUtils;
import cn.ubibi.jettyboot.framework.rest.IReqParser;
import cn.ubibi.jettyboot.framework.rest.ReqParams;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


public class RequestBodyParser implements IReqParser {
    @Override
    public void doParse(ReqParams jettyBootReqParams, HttpServletRequest request, String path) {
        try {
            Object obj = jettyBootReqParams.getRequestBodyObject(this.getClass());
            BeanUtils.copyField(this, obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}