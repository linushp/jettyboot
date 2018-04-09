package cn.ubibi.jettyboot.framework.rest.ifs;

import cn.ubibi.jettyboot.framework.rest.JBRequest;

import java.lang.reflect.Method;

public interface JBRequestAspect {

    void invokeBefore(Method method, JBRequest JBRequest) throws Exception;

    void invokeAfter(Method method, JBRequest JBRequest, Object invokeResult) throws Exception;

}
