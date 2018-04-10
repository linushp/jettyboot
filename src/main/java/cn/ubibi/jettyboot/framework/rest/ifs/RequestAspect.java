package cn.ubibi.jettyboot.framework.rest.ifs;

import cn.ubibi.jettyboot.framework.rest.Request;

import java.lang.reflect.Method;

public interface RequestAspect {

    void invokeBefore(Method method, Request JBRequest) throws Exception;

    void invokeAfter(Method method, Request JBRequest, Object invokeResult) throws Exception;

}
