package cn.ubibi.jettyboot.demotest.controller;

import cn.ubibi.jettyboot.demotest.entity.UserEntity;
import cn.ubibi.jettyboot.framework.rest.IRestRender;
import cn.ubibi.jettyboot.framework.rest.impl.RestTextRender;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PageRender implements IRestRender{

    public PageRender(String pageUri, Object pageData) {
    }

    @Override
    public void doRender(HttpServletRequest request, HttpServletResponse response) throws IOException {
        new RestTextRender("hello page ").doRender(request,response);
    }
}
