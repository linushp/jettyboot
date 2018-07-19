package cn.ubibi.jettyboot.framework.commons.cache;

public class CacheObject {
    private String key;
    private Object object;
    private long expireTimeMs;

    public CacheObject(String key, Object object, long expireTimeMs) {
        this.key = key;
        this.object = object;
        this.expireTimeMs = expireTimeMs;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public long getExpireTimeMs() {
        return expireTimeMs;
    }

    public void setExpireTimeMs(long expireTimeMs) {
        this.expireTimeMs = expireTimeMs;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
