package cn.ubibi.jettyboot.framework.rest;

import java.lang.reflect.Method;

public interface IRestMethodAspect {

    void invokeBefore(Method method, ReqParams reqParams) throws Exception;

    void invokeAfter(Method method, ReqParams reqParams, Object invokeResult) throws Exception;

}
