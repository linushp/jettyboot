package cn.ubibi.jettyboot.framework.ioc;

import cn.ubibi.jettyboot.framework.commons.cache.CacheAnnotationUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ServiceProxyHandler implements InvocationHandler {
    private Object realServiceObject;
    public ServiceProxyHandler(Object realServiceObject) {
        this.realServiceObject = realServiceObject;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] paramsObjects) throws Throwable {

        //先从缓存里取
        Object invokeResult = CacheAnnotationUtils.getResultFromCacheAnnotation(method, paramsObjects);
        if (invokeResult == null) {
            invokeResult = method.invoke(this.realServiceObject, paramsObjects);
            CacheAnnotationUtils.saveResultToCacheAnnotation(method, paramsObjects, invokeResult);
        }

        return invokeResult;
    }



}
