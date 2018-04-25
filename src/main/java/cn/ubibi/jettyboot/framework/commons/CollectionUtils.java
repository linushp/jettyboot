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


    public static List<Map<String, Object>> removeEmptyMap(List<Map<String, Object>> mapList) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (mapList != null) {
            for (Map<String, Object> map : mapList) {
                if (map != null && !map.isEmpty()) {
                    result.add(map);
                }
            }
        }
        return result;
    }


    public static Set<String> getAllMapKeys(List<Map<String, Object>> mapList) {
        Set<String> hashSet = new HashSet<>();
        for (Map<String, Object> map : mapList) {
            if (map != null && !map.isEmpty()) {
                Set<String> keys = map.keySet();
                hashSet.addAll(keys);
            }
        }
        return hashSet;
    }


    public static List<String> toListAddAll(String[] urls1, String[] urls2) {
        List<String> result = new ArrayList<>();

        if (urls1 != null && urls1.length > 0) {
            for (String s : urls1) {
                result.add(s);
            }
        }

        if (urls2 != null && urls2.length > 0) {
            for (String s2 : urls2) {
                result.add(s2);
            }
        }

        return result;

    }


    public static <T> T getFirstElement(List<T> result) {
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        return null;
    }


    /**
     * 过滤出合法的ID字符串，避免SQL注入
     * 因为ID一般不会包含特殊字符
     *
     * @return id List
     */
    public static List filterOnlyLegalId(List idList) {
        if (idList == null) {
            return null;
        }


        List result = new ArrayList();

        for (Object obj : idList) {

            if (obj instanceof Long || obj instanceof Integer) {
                result.add(obj);
            } else if (obj instanceof String && isLegalStringId((String) obj)) {
                result.add(obj);
            }
        }

        return result;
    }


    private static boolean isLegalStringId(String obj) {
        if (obj == null || obj.isEmpty()) {
            return false;
        }
        for (int i = 0; i < obj.length(); i++) {
            char cc = obj.charAt(i);
            if (!isLegalStringIdChar(cc)) {
                return false;
            }
        }
        return true;
    }


    private static boolean isLegalStringIdChar(char cc) {
        if (cc >= 'A' && cc <= 'Z') {
            return true;
        }
        if (cc >= 'a' && cc <= 'z') {
            return true;
        }
        if (cc >= '0' && cc <= '9') {
            return true;
        }

        if (cc == '-' || cc == '_' || cc == '~') {
            return true;
        }

        return false;
    }
}
