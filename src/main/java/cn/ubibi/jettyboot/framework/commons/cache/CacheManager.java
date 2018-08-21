package cn.ubibi.jettyboot.framework.commons.cache;

import cn.ubibi.jettyboot.framework.commons.MultiListMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public class CacheManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheManager.class);
    private static final Map<String, CacheObject> cacheMap = new HashMap<>();
    private static final MultiListMap<String,CacheExpiredListener> cacheExpiredListeners = new MultiListMap<>();
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


    public static void addCacheExpiredListener(String cacheKey, CacheExpiredListener cacheExpiredListener) {
        cacheExpiredListeners.putElement(cacheKey, cacheExpiredListener);
    }


    private static void onExpiredCacheKey(List<String> expiredCacheKey) {
        for (String cacheKey : expiredCacheKey) {
            List<CacheExpiredListener> list = cacheExpiredListeners.getListNotNull(cacheKey);
            for (CacheExpiredListener listener : list) {
                try {
                    listener.onCacheExpired();
                } catch (Exception e) {
                    LOGGER.error("", e);
                }
            }
        }
    }


    private static void runCheckExpireTimeThread() {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {

                    try {
                        Thread.sleep(1000 * 30); //30 s
                    } catch (InterruptedException e) {
                        LOGGER.error("", e);
                    }

                    List<String> expiredCacheKey = new ArrayList<>();

                    synchronized (CacheManager.lock) {
                        Map<String, CacheObject> cache0 = CacheManager.cacheMap;
                        Collection<CacheObject> values = new ArrayList<>(cache0.values());
                        for (CacheObject object : values) {
                            if (isExpired(object.getExpireTimeMs())) {
                                cache0.remove(object.getKey());
                                expiredCacheKey.add(object.getKey());
                            }
                        }
                    }

                    try {
                        CacheManager.onExpiredCacheKey(expiredCacheKey);
                    } catch (Exception e) {
                        LOGGER.error("", e);
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


    public static void removeObject(String key) {
        synchronized (CacheManager.lock) {
            cacheMap.remove(key);
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
