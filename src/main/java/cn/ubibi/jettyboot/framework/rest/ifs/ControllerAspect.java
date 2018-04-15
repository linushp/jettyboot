package cn.ubibi.jettyboot.framework.rest.ifs;

import cn.ubibi.jettyboot.framework.rest.ControllerRequest;

import java.lang.reflect.Method;

public interface ControllerAspect {

    void invokeBefore(Method method, ControllerRequest JBRequest) throws Exception;

    void invokeAfter(Method method, ControllerRequest JBRequest, Object invokeResult) throws Exception;

}
