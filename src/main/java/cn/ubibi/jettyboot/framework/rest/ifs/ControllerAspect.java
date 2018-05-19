package cn.ubibi.jettyboot.framework.rest.ifs;


import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public interface ControllerAspect {

    //此方法如果抛出异常，将停止调用Controller里面的方法
    void beforeInvoke(Method method, HttpParsedRequest request) throws Exception;

    void afterInvoke(Method method, HttpParsedRequest request, Object invokeResult, HttpServletResponse response) throws Exception;

    void afterRender(Method method, HttpParsedRequest request, Object invokeResult, HttpServletResponse response);
}

