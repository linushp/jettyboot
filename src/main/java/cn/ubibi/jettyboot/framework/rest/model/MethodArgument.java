package cn.ubibi.jettyboot.framework.rest.model;

import cn.ubibi.jettyboot.framework.commons.CollectionUtils;
import com.alibaba.fastjson.JSONArray;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class MethodArgument {
    private Method method;
    private Type type;
    private Annotation[] annotations;
    private int argIndex;
    private JSONArray dwrJSONArray;
    private String requestMethod;

    public MethodArgument(Method method, Type type, Annotation[] annotations, int argIndex, JSONArray dwrJSONArray,String request_method) {
        this.method = method;
        this.type = type;
        this.annotations = annotations;
        this.argIndex = argIndex;
        this.dwrJSONArray = dwrJSONArray;
        this.requestMethod = request_method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setAnnotations(Annotation[] annotations) {
        this.annotations = annotations;
    }


    public void setArgIndex(int argIndex) {
        this.argIndex = argIndex;
    }

    public void setDwrJSONArray(JSONArray dwrJSONArray) {
        this.dwrJSONArray = dwrJSONArray;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public Method getMethod() {
        return method;
    }

    public Type getType() {
        return type;
    }

    public Type getRawType() {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return parameterizedType.getRawType();
        } else {
            return type;
        }
    }

    public Type[] getActualTypeArguments() {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return parameterizedType.getActualTypeArguments();
        }
        return null;
    }

    public Annotation[] getAnnotations() {
        return annotations;
    }


    public boolean hasAnnotation(Class<? extends Annotation> annotationType) {
        if (CollectionUtils.isEmpty(annotations)) {
            return false;
        }
        for (Annotation annotation : annotations) {
            if (annotationType == annotation.getClass()) {
                return true;
            }
        }
        return false;
    }


    public int getArgIndex() {
        return argIndex;
    }

    public JSONArray getDwrJSONArray() {
        return dwrJSONArray;
    }
}
