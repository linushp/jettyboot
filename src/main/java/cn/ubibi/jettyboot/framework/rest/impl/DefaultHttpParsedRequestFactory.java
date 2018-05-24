package cn.ubibi.jettyboot.framework.rest.impl;

import cn.ubibi.jettyboot.framework.rest.ifs.HttpParsedRequest;
import cn.ubibi.jettyboot.framework.rest.ifs.HttpParsedRequestFactory;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

public class DefaultHttpParsedRequestFactory implements HttpParsedRequestFactory {
    @Override
    public HttpParsedRequest createHttpParsedRequest(Object controller, Method method, HttpServletRequest request, String targetPath) {
        return new DefaultHttpParsedRequest(request, targetPath);
    }
}