package cn.ubibi.jettyboot.framework.commons.cache;

import java.util.*;


public class CacheManager {

    private static final Map<String, CacheObject> cacheMap = new HashMap<>();
    private static final Map<String, List<CacheExpiredListener>> cacheExpiredListeners = new HashMap<>();
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
        List<CacheExpiredListener> list = cacheExpiredListeners.get(cacheKey);
        if (list == null) {
            list = new ArrayList<>();
            cacheExpiredListeners.put(cacheKey, list);
        }
        list.add(cacheExpiredListener);
    }


    private static void onExpiredCacheKey(List<String> expiredCacheKey) {
        for (String cacheKey : expiredCacheKey) {
            List<CacheExpiredListener> list = cacheExpiredListeners.get(cacheKey);
            if (list != null) {
                for (CacheExpiredListener listener : list) {
                    try {
                        listener.onCacheExpired();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
                        Thread.sleep(1000 * 60 * 5); //5分钟
                    } catch (InterruptedException e) {
                        e.printStackTrace();
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
                    }catch (Exception e) {
                        e.printStackTrace();
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
        synchronized(CacheManager.lock) {
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
