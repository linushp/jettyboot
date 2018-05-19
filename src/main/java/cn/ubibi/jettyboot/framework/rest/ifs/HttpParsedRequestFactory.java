package cn.ubibi.jettyboot.framework.rest.ifs;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

public interface HttpParsedRequestFactory {
    HttpParsedRequest createHttpParsedRequest(Object controller, Method method, HttpServletRequest request, String targetPath);
}