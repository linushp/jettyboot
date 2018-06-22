package cn.ubibi.jettyboot.framework.rest.impl;

import cn.ubibi.jettyboot.framework.commons.FrameworkConfig;
import cn.ubibi.jettyboot.framework.commons.cache.CacheManager;
import cn.ubibi.jettyboot.framework.rest.annotation.Cache;

import java.lang.reflect.Method;

public class CacheAnnotationUtils {

    public static Object getResultFromCacheAnnotation(Method method, Object controller, Object[] params) {
        if (!FrameworkConfig.getInstance().isCacheAnnotation()) {
            return null;
        }

        Cache cacheAnnotation = method.getAnnotation(Cache.class);
        if (cacheAnnotation == null) {
            return null;
        }

        String key = toCacheKey(cacheAnnotation, params);
        return CacheManager.getObject(key);
    }


    public static void saveResultToCacheAnnotation(Method method, Object controller, Object[] params, Object invokeResult) {

        Cache cacheAnnotation = method.getAnnotation(Cache.class);
        if (cacheAnnotation == null) {
            return;
        }

        String key = toCacheKey(cacheAnnotation, params);

        long activeTime = cacheAnnotation.activeTime();
        CacheManager.putObject(key, invokeResult, activeTime);
    }



    private static String toCacheKey(Cache cacheAnnotation, Object[] params) {
        String key = cacheAnnotation.cacheKey();
        int[] paramsKey = cacheAnnotation.paramKey();
        if (paramsKey.length == 0) {
            return key;
        }


        StringBuilder sb = new StringBuilder();
        sb.append(key);
        for (int i = 0; i < paramsKey.length; i++) {
            sb.append(".");
            sb.append(String.valueOf(params[paramsKey[i]]));
        }

        return sb.toString();
    }

}
