package cn.ubibi.jettyboot.framework.rest.ifs;


import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public interface HttpParsedRequest extends HttpServletRequest {

    String getPathVariable(String name);

    String getCookieValue(String cookieName);

    void setAspectVariable(String name, Object aspectVariable);

    Object getAspectVariable(String name);

    Object getAspectVariable(Class<?> clazz);

    byte[] getRequestBody() throws Exception;

    String getRequestBodyAsString(Charset charset) throws Exception;

    List<String> getParameterValuesAsList(String name);

    <T> T getParameterValuesAsObject(Class<? extends T> clazz);

    Map<String, String> getParameterValuesAsMap();

    String getMatchedControllerPath();

}
