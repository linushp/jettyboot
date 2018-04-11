package cn.ubibi.jettyboot.framework.rest.ifs;

import cn.ubibi.jettyboot.framework.rest.Request;

import javax.servlet.http.HttpServletRequest;

public interface RequestParser {
    void doParse(Request request, HttpServletRequest httpServletRequest) throws Exception;
}
