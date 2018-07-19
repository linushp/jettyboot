package cn.ubibi.jettyboot.framework.commons.cache;

import cn.ubibi.jettyboot.framework.commons.FrameworkConfig;

import java.lang.reflect.Method;

public class CacheAnnotationUtils {

    private static final String CACHE_KEY_PREFIX = "sys:";

    public static Object getResultFromCacheAnnotation(Method method, Object[] params) {
        if (!FrameworkConfig.getInstance().isCacheAnnotation()) {
            return null;
        }

        CacheMethod cacheAnnotation = method.getDeclaredAnnotation(CacheMethod.class);
        if (cacheAnnotation == null) {
            return null;
        }

        String key = toCacheKey(cacheAnnotation, params);
        return CacheManager.getObject(key);
    }


    public static void saveResultToCacheAnnotation(Method method, Object[] params, Object invokeResult) {

        CacheMethod cacheAnnotation = method.getAnnotation(CacheMethod.class);
        if (cacheAnnotation == null) {
            return;
        }

        String key = toCacheKey(cacheAnnotation, params);

        long activeTime = cacheAnnotation.activeTime();
        CacheManager.putObject(key, invokeResult, activeTime);
    }


    private static String toCacheKey(CacheMethod cacheAnnotation, Object[] params) {
        String key = CACHE_KEY_PREFIX + cacheAnnotation.cacheKey();

        int[] paramsKey = cacheAnnotation.paramKey();
        if (paramsKey.length == 0) {
            return key;
        }


        StringBuilder sb = new StringBuilder();
        sb.append(key);
        for (int i = 0; i < paramsKey.length; i++) {
            sb.append("_");
            sb.append(String.valueOf(params[paramsKey[i]]));
        }

        return sb.toString();
    }

}
