package cn.ubibi.jettyboot.framework.rest;

import javax.servlet.http.HttpServletRequest;

public interface IReqParser {
    void doParse(ReqParams jettyBootReqParams, HttpServletRequest request, String path) throws Exception;
}
