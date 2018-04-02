package cn.ubibi.jettyboot.framework.rest;

import cn.ubibi.jettyboot.framework.commons.StringWrapper;
import com.alibaba.fastjson.JSON;

import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReqParams {
    private HttpServletRequest request;
    private String targetPath;


    private Map<String, String> _pathVariable = null;
    private byte[] _requestBody = null;


    public ReqParams(HttpServletRequest request, String targetPath) {
        this.request = request;
        this.targetPath = targetPath;
    }

    public StringWrapper getRequestParam(String name) {
        return new StringWrapper(request.getParameter(name));
    }

    public StringWrapper getRequestParam(String name,String defaultValue) {
        String mm = request.getParameter(name);
        if(mm==null || mm.isEmpty()){
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

    public String getCookieValue(String cookieName){
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
//        Map<String, String[]> map1 = request.getParameterMap();

        Map<String,Object> map2= new HashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            String fieldName = field.getName();
            Class<?> fieldType = field.getType();

            if(fieldType.isArray() || List.class.isAssignableFrom(fieldType)){
                map2.put(fieldName,request.getParameterValues(fieldName));
            }else {
                map2.put(fieldName,request.getParameter(fieldName));
            }
        }


        String mapString = JSON.toJSONString(map2);
        T obj = JSON.parseObject(mapString, clazz);

        return obj;
    }


    public StringWrapper getPathVariable(String name) {
        if (this._pathVariable != null) {
            return new StringWrapper(this._pathVariable.get(name));
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


        return new StringWrapper(this._pathVariable.get(name));
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
}
