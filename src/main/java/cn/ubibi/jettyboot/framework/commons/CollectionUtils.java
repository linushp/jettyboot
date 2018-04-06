package cn.ubibi.jettyboot.framework.commons;

import java.util.*;

public class CollectionUtils {

    public static List<String> eachWrap(Collection collection, String a, String b) {
        List<String> result = new ArrayList<>();

        for (Object s : collection) {
            String ss = "";
            if (s != null) {
                ss = s.toString();
            }
            result.add(a + ss + b);
        }

        return result;
    }


    public static <T> List<T> repeatList(T obj, int repeatTimes) {
        List<T> result = new ArrayList<>();

        for (int i = 0; i < repeatTimes; i++) {
            result.add(obj);
        }

        return result;
    }


    public static List[] listKeyValues(Map<String, Object> map) {
        List<Object> values = new ArrayList<>();
        List<String> keys = new ArrayList<>();
        Set<Map.Entry<String, Object>> entrys = map.entrySet();
        for (Map.Entry<String, Object> entry : entrys) {
            values.add(entry.getValue());
            keys.add(entry.getKey());
        }
        return new List[]{keys, values};
    }


    public static List<String> removeEmpty(String[] stringArray) {
        List<String> result = new ArrayList<>();
        if (stringArray != null) {
            for (int i = 0; i < stringArray.length; i++) {
                String s = stringArray[i];
                if (s != null && !s.isEmpty()) {
                    result.add(s);
                }
            }
        }
        return result;
    }


    public static List<Map<String,Object>> removeEmptyMap(List<Map<String,Object>> mapList) {
        List<Map<String,Object>> result = new ArrayList<>();
        if(mapList!=null){
            for (Map<String,Object> map : mapList) {
                if (map != null && !map.isEmpty()) {
                    result.add(map);
                }
            }
        }
        return result;
    }




    public static Set<String> getAllMapKeys(List<Map<String,Object>> mapList){
        Set<String> hashSet = new HashSet<>();
        for (Map<String,Object> map : mapList) {
            if (map != null && !map.isEmpty()) {
                Set<String> keys = map.keySet();
                hashSet.addAll(keys);
            }
        }
        return hashSet;
    }


}
