package cn.ubibi.jettyboot.demotest.controller.parser;

import cn.ubibi.jettyboot.framework.rest.ifs.JBRequestParser;
import cn.ubibi.jettyboot.framework.rest.JBRequest;

import javax.servlet.http.HttpServletRequest;

public class CurrentUser implements JBRequestParser {

    private String name;

    @Override
    public void doParse(JBRequest jettyBootJBRequest, HttpServletRequest request, String path) throws Exception {
        this.name = jettyBootJBRequest.getCookieValue("name");
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
