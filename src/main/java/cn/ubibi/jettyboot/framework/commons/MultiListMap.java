package cn.ubibi.jettyboot.framework.commons;

import cn.ubibi.jettyboot.framework.commons.ifs.Getter;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class MultiListMap<S,T> {

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

    public synchronized void putElements(Getter<T,S> keyGetter,List<T> objectList){
        for (T obj : objectList){
            S key = keyGetter.doGet(obj);
            putElement(key,obj);
        }
    }

    public List<T> getList(S key) {
        return map.get(key);
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
