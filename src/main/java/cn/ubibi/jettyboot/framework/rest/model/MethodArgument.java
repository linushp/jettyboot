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
    private boolean dwr;
    private int argIndex;
    private JSONArray dwrJSONArray;


    public MethodArgument(Method method, Type type, Annotation[] annotations, int argIndex, boolean isDWR, JSONArray dwrJSONArray) {
        this.method = method;
        this.type = type;
        this.annotations = annotations;
        this.dwr = isDWR;
        this.argIndex = argIndex;
        this.dwrJSONArray = dwrJSONArray;
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

    public boolean isDwr() {
        return this.dwr;
    }

    public int getArgIndex() {
        return argIndex;
    }

    public JSONArray getDwrJSONArray() {
        return dwrJSONArray;
    }
}
