package cn.ubibi.jettyboot.framework.rest.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
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

    public Annotation[] getAnnotations() {
        return annotations;
    }

    public boolean isBasicNumberType() {
        if (type == Integer.TYPE ||
                type== Long.TYPE ||
                type== Short.TYPE ||
                type == Double.TYPE ||
                type==Float.TYPE){
            return true;
        }
        return false;
    }

    public boolean isBooleanType(){
        return Boolean.TYPE == type;
    }
}
