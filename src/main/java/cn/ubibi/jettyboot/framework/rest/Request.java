package cn.ubibi.jettyboot.framework.rest;

import cn.ubibi.jettyboot.framework.commons.StringWrapper;
import com.alibaba.fastjson.JSON;

import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {
    private HttpServletRequest request;
    private String targetPath;


    private Map<String, String> _pathVariable = null;
    private byte[] _requestBody = null;


    private Map<String, Object> aspectVariable = null;


    private Request(HttpServletRequest request, String targetPath) {
        this.request = request;
        this.targetPath = targetPath;
    }


    public static Request getInstance(HttpServletRequest request, String targetPath) {
        String name = Request.class.getName() + "_jettyBootRequest";
        Request jettyBootRequest = (Request) request.getAttribute(name);
        if (jettyBootRequest == null) {
            jettyBootRequest = new Request(request, targetPath);
            request.setAttribute(name, jettyBootRequest);
        }
        return jettyBootRequest;
    }


    public String getContextPath() {
        return this.request.getContextPath();
    }

    public String getMethod() {
        return this.request.getMethod();
    }

    public String getPathInfo() {
        return this.request.getPathInfo();
    }

    public String getHeader(String name) {
        return this.request.getHeader(name);
    }

    public String getQueryString() {
        return this.request.getQueryString();
    }

    public String[] getParameterValues(String name) {
        return request.getParameterValues(name);
    }

    public String getParameter(String name) {
        return request.getParameter(name);
    }


    public StringWrapper getRequestParam(String name) {
        return new StringWrapper(request.getParameter(name));
    }

    public StringWrapper getRequestParam(String name, String defaultValue) {
        String mm = request.getParameter(name);
        if (mm == null || mm.isEmpty()) {
            mm = defaultValue;
        }
        return new StringWrapper(mm);
    }

    public StringWrapper[] getRequestParams(String name) {
        String[] values = request.getParameterValues(name);
        StringWrapper[] valuesWrapper = new StringWrapper[values.length];
        for (int i = 0; i < values.length; i++) {
            String value = values[i];
            valuesWrapper[i] = new StringWrapper(value);
        }
        return valuesWrapper;
    }


    public String getCookieValue(String cookieName) {
        Cookie[] cookies = this.request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public <T> T getRequestParamObject(Class<? extends T> clazz) {

        Map<String, Object> map2 = new HashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            String fieldName = field.getName();
            Class<?> fieldType = field.getType();

            if (fieldType.isArray() || List.class.isAssignableFrom(fieldType)) {
                map2.put(fieldName, request.getParameterValues(fieldName));
            } else {
                map2.put(fieldName, request.getParameter(fieldName));
            }
        }


        String mapString = JSON.toJSONString(map2);
        T obj = JSON.parseObject(mapString, clazz);

        return obj;
    }


    public String getPathVariable(String name) {
        if (this._pathVariable != null) {
            return (this._pathVariable.get(name));
        }

        this._pathVariable = new HashMap<>();

        String pathInfo = this.request.getPathInfo();
        String[] pathInfoArray = pathInfo.split("/");
        String[] targetPathArray = this.targetPath.split("/");

        for (int i = 0; i < targetPathArray.length; i++) {
            String p1 = targetPathArray[i];
            String p2 = pathInfoArray[i];
            if (p1.startsWith(":")) {
                String k = p1.replaceFirst(":", "");
                this._pathVariable.put(k, p2);
            }
        }
        return this._pathVariable.get(name);
    }


    public byte[] getRequestBody() throws IOException {

        if (this._requestBody != null) {
            return this._requestBody;
        }

        int len = request.getContentLength();
        if (len <= 0) {
            return null;
        }


        ServletInputStream inputStream = request.getInputStream();
        byte[] buffer = new byte[len];
        inputStream.read(buffer, 0, len);


        this._requestBody = buffer;
        return buffer;
    }


    public <T> T getRequestBodyObject(Class<? extends T> clazz) throws IOException {
        byte[] body = this.getRequestBody();
        if (body == null || body.length == 0) {
            return null;
        }

        String bodyString = new String(body, "utf-8");
        return JSON.parseObject(bodyString, clazz);
    }

    public String getTargetPath() {
        return this.targetPath;
    }


    public Object getAspectVariable(String name) {
        if (this.aspectVariable == null) {
            return null;
        }
        return this.aspectVariable.get(name);
    }


    public Object getAspectVariableByClassType(Class clazz) {
        if (this.aspectVariable == null) {
            return null;
        }


        Collection<Object> values = this.aspectVariable.values();
        for (Object obj : values) {
            if (obj.getClass() == clazz || obj.getClass().equals(clazz)) {
                return obj;
            }
        }

        return null;
    }


    public void setAspectVariable(String name, Object aspectVariable) {
        if (this.aspectVariable == null) {
            this.aspectVariable = new HashMap<>();
        }
        this.aspectVariable.put(name, aspectVariable);
    }


}
