package cn.ubibi.jettyboot.framework.commons;

import java.util.*;

public class MultiListMap<T> {

    private Map<String, List<T>> map;

    public MultiListMap() {
        this.map = new HashMap<>();
    }

    public MultiListMap(Map map) {
        this.map = map;
    }

    public void putElement(String key, T object) {
        List<T> list = getListNotNull(key);
        list.add(object);
    }

    public List<T> getList(String key){
        return map.get(key);
    }

    public List<T> getListNotNull(String key){
        List<T> list = map.get(key);
        if (list == null) {
            list = new ArrayList<T>();
            map.put(key, list);
        }
        return list;
    }
}
