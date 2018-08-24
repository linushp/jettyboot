package cn.ubibi.jettyboot.framework.commons;

import cn.ubibi.jettyboot.framework.commons.ifs.Getter;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class MultiListMap<S, T> {

    private Map<S, List<T>> map;

    public MultiListMap() {
        this.map = new HashMap<>();
    }

    public MultiListMap(Map map) {
        this.map = map;
    }

    public synchronized void putElement(S key, T object) {
        List<T> list = getListNotNull(key);
        list.add(object);
    }

    public synchronized void putElements(Getter<T, S> keyGetter, List<T> objectList) {
        for (T obj : objectList) {
            S key = keyGetter.doGet(obj);
            putElement(key, obj);
        }
    }


    public synchronized void putElementsList(Map<S, List<T>> sourceMap) {
        Set<Map.Entry<S, List<T>>> entrySet = sourceMap.entrySet();
        for (Map.Entry<S, List<T>> entry : entrySet) {
            S key = entry.getKey();
            List<T> tList = entry.getValue();
            if (!CollectionUtils.isEmpty(tList)) {
                List<T> list = getListNotNull(key);
                for (T t : tList) {
                    list.add(t);
                }
            }
        }
    }

    public synchronized void putElementsArray(Map<S, T[]> sourceMap) {
        Set<Map.Entry<S, T[]>> entrySet = sourceMap.entrySet();
        for (Map.Entry<S, T[]> entry : entrySet) {
            S key = entry.getKey();
            T[] tList = entry.getValue();
            if (!CollectionUtils.isEmpty(tList)) {
                List<T> list = getListNotNull(key);
                for (int i = 0; i < tList.length; i++) {
                    list.add(tList[i]);
                }
            }
        }
    }


    public Map<S, T> toHashMap() {
        Map<S, T> result = new HashMap<>();
        Set<Map.Entry<S, List<T>>> entrySet = map.entrySet();
        for (Map.Entry<S, List<T>> entry : entrySet) {
            S key = entry.getKey();
            List<T> tList = entry.getValue();
            if (!CollectionUtils.isEmpty(tList)) {
                result.put(key, tList.get(0));
            }
        }
        return result;
    }

    public List<T> getList(S key) {
        return map.get(key);
    }

    public Map<S, List<T>> getMap() {
        return map;
    }

    public synchronized List<T> getListNotNull(S key) {
        List<T> list = map.get(key);
        if (list == null) {
            list = new CopyOnWriteArrayList<>();
            map.put(key, list);
        }
        return list;
    }
}
