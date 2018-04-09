package cn.ubibi.jettyboot.framework.rest.ifs;

import cn.ubibi.jettyboot.framework.rest.JBRequest;

import javax.servlet.http.HttpServletRequest;

public interface JBRequestParser {
    void doParse(JBRequest jettyBootJBRequest, HttpServletRequest request, String path) throws Exception;
}
