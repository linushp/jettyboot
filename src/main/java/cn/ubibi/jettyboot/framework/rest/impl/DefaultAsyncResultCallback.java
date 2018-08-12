package cn.ubibi.jettyboot.framework.rest.impl;

import cn.ubibi.jettyboot.framework.rest.ifs.HttpParsedRequest;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public class DefaultAsyncResultCallback implements AsyncResultCallback {
    private Method method;
    private String context;
    public DefaultAsyncResultCallback(Method method,String context) {
        this.method  = method;
        this.context = context;
    }

    @Override
    public void callback(Object invokeResult, ServletRequest request, ServletResponse response) throws Exception {
        ResultRenderMisc.renderAndAfterInvoke(invokeResult,method,(HttpParsedRequest) request,(HttpServletResponse) response,context);
    }
}
