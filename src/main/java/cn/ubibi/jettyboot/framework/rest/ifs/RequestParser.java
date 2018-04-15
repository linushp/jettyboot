package cn.ubibi.jettyboot.framework.rest.ifs;

import cn.ubibi.jettyboot.framework.rest.ControllerRequest;

import javax.servlet.http.HttpServletRequest;

public interface RequestParser {
    void doParse(ControllerRequest request, HttpServletRequest httpServletRequest) throws Exception;
}
