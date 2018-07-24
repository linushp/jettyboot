package cn.ubibi.jettyboot.framework.rest.impl;

import cn.ubibi.jettyboot.framework.rest.ifs.HttpParsedRequest;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public class DefaultAsyncResultCallback implements AsyncResultCallback {
    private Method method;
    public DefaultAsyncResultCallback(Method method) {
        this.method  = method;
    }

    @Override
    public void callback(Object invokeResult, ServletRequest request, ServletResponse response) throws Exception {
        ResultRenderMisc.renderAndAfterInvoke(invokeResult,method,(HttpParsedRequest) request,(HttpServletResponse) response);
    }
}
