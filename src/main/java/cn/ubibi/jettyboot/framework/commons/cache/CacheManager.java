package cn.ubibi.jettyboot.framework.commons.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class CacheManager {

    private static final Map<String, CacheObject> cacheMap = new HashMap<>();
    private static final Integer lock = 0;
    private static boolean isCheckExpireTimeThreadRunning = false;

    /**
     * @param key
     * @param object
     * @param keepTimeMs 有效时长，毫秒
     */
    public static void putObject(String key, Object object, long keepTimeMs) {
        synchronized (CacheManager.lock) {
            long expireTimeMs = System.currentTimeMillis() + keepTimeMs;
            CacheObject cacheObject = new CacheObject(key, object, expireTimeMs);
            cacheMap.put(key, cacheObject);
            if (!isCheckExpireTimeThreadRunning) {
                runCheckExpireTimeThread();
                isCheckExpireTimeThreadRunning = true;
            }
        }
    }


    private static void runCheckExpireTimeThread() {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {

                    try {
                        Thread.sleep(1000 * 60 * 5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    synchronized (CacheManager.lock) {
                        Map<String, CacheObject> cache0 = CacheManager.cacheMap;
                        Collection<CacheObject> values = cache0.values();
                        for (CacheObject object : values) {
                            if (isExpired(object.getExpireTimeMs())) {
                                cache0.remove(object.getKey());
                            }
                        }
                    }

                }
            }
        });


        t.setName("CacheManagerCheckCacheExpireThread");
        t.start();
    }


    public static <T> T getObject(String key) {
        synchronized (CacheManager.lock) {
            CacheObject cacheObject = cacheMap.get(key);
            if (cacheObject != null) {
                long expireTimeMs = cacheObject.getExpireTimeMs();
                if (!isExpired(expireTimeMs)) {
                    return (T) cacheObject.getObject();
                }
            }
            return null;
        }
    }


    /**
     * 判断是否已过期
     *
     * @param expireTimeMs 有效时间
     * @return
     */
    private static boolean isExpired(long expireTimeMs) {
        long nowTimeMs = System.currentTimeMillis();
        if (nowTimeMs > expireTimeMs) {
            return true; //已经过期
        }
        return false;
    }

}
