package cn.ubibi.jettyboot.framework.rest.ifs;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public interface ControllerAspect {

    //此方法如果抛出异常，将停止调用Controller里面的方法
    void beforeInvoke(Method method, HttpServletRequest request) throws Exception;

    void afterInvoke(Method method, HttpServletRequest request, Object invokeResult, HttpServletResponse response) throws Exception;

    void afterRender(Method method, HttpServletRequest request, Object invokeResult, HttpServletResponse response);
}

