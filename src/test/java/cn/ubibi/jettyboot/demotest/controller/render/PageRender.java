package cn.ubibi.jettyboot.demotest.controller.render;

import cn.ubibi.jettyboot.framework.rest.ifs.JBResponseRender;
import cn.ubibi.jettyboot.framework.rest.impl.JBTextRender;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PageRender implements JBResponseRender {

    public PageRender(String pageUri, Object pageData) {
    }

    @Override
    public void doRender(HttpServletRequest request, HttpServletResponse response) throws IOException {
        new JBTextRender("hello page ").doRender(request,response);
    }
}
