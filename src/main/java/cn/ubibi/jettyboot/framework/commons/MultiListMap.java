package cn.ubibi.jettyboot.framework.commons;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class MultiListMap<T> {

    private Map<String, List<T>> map;

    public MultiListMap() {
        this.map = new HashMap<>();
    }

    public MultiListMap(Map map) {
        this.map = map;
    }

    public synchronized void putElement(String key, T object) {
        List<T> list = getListNotNull(key);
        list.add(object);
    }

    public List<T> getList(String key) {
        return map.get(key);
    }

    public synchronized List<T> getListNotNull(String key) {
        List<T> list = map.get(key);
        if (list == null) {
            list = new CopyOnWriteArrayList<>();
            map.put(key, list);
        }
        return list;
    }
}
