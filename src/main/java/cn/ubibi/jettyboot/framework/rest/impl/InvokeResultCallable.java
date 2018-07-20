package cn.ubibi.jettyboot.framework.rest.impl;

import cn.ubibi.jettyboot.framework.commons.cache.CacheAnnotationUtils;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class InvokeResultCallable implements Callable {

    private Method method;
    private Object[] paramsObjects;
    private Object controller;

    public InvokeResultCallable(Method method, Object[] paramsObjects, Object controller) {
        this.method = method;
        this.paramsObjects = paramsObjects;
        this.controller = controller;
    }

    @Override
    public Object call() throws Exception {

        if (CacheAnnotationUtils.isNeedCache(method)) {
            //先从缓存里取
            Object invokeResult = CacheAnnotationUtils.getResultFromCacheAnnotation(method, paramsObjects);
            if (invokeResult == null) {
                //方法调用
                invokeResult = method.invoke(controller, paramsObjects);
                CacheAnnotationUtils.saveResultToCacheAnnotation(method, paramsObjects, invokeResult);
            }
            return invokeResult;
        } else {
            return method.invoke(controller, paramsObjects);
        }
    }
}
