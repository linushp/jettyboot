package cn.ubibi.jettyboot.framework.rest.model;

import cn.ubibi.jettyboot.framework.commons.CollectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class MethodArgument {
    private Method method;
    private Type type;
    private Annotation[] annotations;


    public MethodArgument(Method method, Type type, Annotation[] annotations) {
        this.method = method;
        this.type = type;
        this.annotations = annotations;
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
}
