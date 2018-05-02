package cn.ubibi.jettyboot.framework.rest.ifs;

import cn.ubibi.jettyboot.framework.rest.ControllerRequest;

import java.lang.reflect.Method;

public interface ControllerInterceptor {

    void invokeBefore(Method method, ControllerRequest request) throws Exception;

    void invokeAfter(Method method, ControllerRequest request, Object invokeResult) throws Exception;

}
