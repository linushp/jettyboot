package cn.ubibi.jettyboot.framework.rest.ifs;

import cn.ubibi.jettyboot.framework.rest.ControllerRequest;

import java.lang.reflect.Method;

public interface ControllerAspect {

    //此方法如果抛出异常，将停止调用Controller里面的方法
    void invokeBefore(Method method, ControllerRequest request) throws Exception;

    void invokeAfter(Method method, ControllerRequest request, Object invokeResult) throws Exception;

}

