package cn.ubibi.jettyboot.framework.ioc;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ServiceProxyHandler implements InvocationHandler {
    private Object realServiceObject;
    public ServiceProxyHandler(Object realServiceObject) {
        this.realServiceObject = realServiceObject;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Annotation[] aaa = method.getDeclaredAnnotations();

        //TODO 事务，缓存
        Object result = method.invoke(this.realServiceObject, args);


        return result;
    }
}
