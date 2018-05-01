package cn.ubibi.jettyboot.framework.commons;

import cn.ubibi.jettyboot.framework.commons.ifs.FilterFunctions;

import java.util.*;

public class CollectionUtils {


    /**
     * 对集合中的每一个元素变成字符串后添加前缀和后缀
     * @param collection 集合
     * @param prefix 字符串前缀
     * @param suffix 字符串后缀
     * @return
     */
    public static List<String> eachWrap(Collection collection, String prefix, String suffix) {
        List<String> result = new ArrayList<>();
        for (Object obj : collection) {
            if (obj != null) {
                result.add(prefix + obj.toString() + suffix);
            }
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



    public static List[] listKeyValues(Map<String, Object> map){
        List<Object> values = new ArrayList<>();
        List<String> keys = new ArrayList<>();
        Set<Map.Entry<String, Object>> entrys = map.entrySet();
        for (Map.Entry<String, Object> entry : entrys) {
            String key = entry.getKey();
            //忽略掉了空的key
            if (!isEmpty(key)){
                values.add(entry.getValue());
                keys.add(key);
            }
        }
        return new List[]{keys, values};
    }


    public static List<String> removeEmptyString(String[] stringArray) {
        List<String> result = new ArrayList<>();
        if (!isEmpty(stringArray)) {
            for (int i = 0; i < stringArray.length; i++) {
                String str = stringArray[i];
                if (!isEmpty(str)) {
                    result.add(str);
                }
            }
        }
        return result;
    }


    public static List<Map<String, Object>> removeEmptyMap(List<Map<String, Object>> mapList) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (mapList != null) {
            for (Map<String, Object> map : mapList) {
                if (!isEmpty(map)) {
                    result.add(map);
                }
            }
        }
        return result;
    }


    public static Set<String> getAllMapKeys(List<Map<String, Object>> mapList) {
        Set<String> hashSet = new HashSet<>();
        for (Map<String, Object> map : mapList) {
            if (!isEmpty(map)) {
                Set<String> keys = map.keySet();
                hashSet.addAll(keys);
            }
        }
        return hashSet;
    }


    public static List<String> toListAddAll(String[] urls1, String[] urls2) {
        List<String> result = new ArrayList<>();

        if (!isEmpty(urls1)) {
            for (String s : urls1) {
                result.add(s);
            }
        }

        if (!isEmpty(urls2)) {
            for (String s2 : urls2) {
                result.add(s2);
            }
        }
        return result;
    }


    public static <T> T getFirstElement(List<T> result) {
        if (isEmpty(result)) {
            return null;
        }
        return result.get(0);
    }


    /**
     * 过滤出合法的ID字符串，避免SQL注入
     * 因为ID一般不会包含特殊字符
     *
     * @return id List
     */
    public static List filterOnlyLegalItems(List idList, FilterFunctions filterFunctions) {
        if (idList == null) {
            return null;
        }

        List result = new ArrayList();
        for (Object obj : idList) {
            if (obj instanceof Long || obj instanceof Integer) {
                result.add(obj);
            } else if (obj instanceof String && isLegalStringItem((String) obj, filterFunctions)) {
                result.add(obj);
            }
        }

        return result;
    }


    /**
     * 遍历字符串中的每一个字符，判断是否是合法的字符串
     *
     * @param obj             字符串
     * @param filterFunctions
     * @return
     */
    private static boolean isLegalStringItem(String obj, FilterFunctions filterFunctions) {
        if (isEmpty(obj)) {
            return false;
        }
        for (int i = 0; i < obj.length(); i++) {
            char cc = obj.charAt(i);
            if (!filterFunctions.isLegalStringIdChar(cc)) {
                return false;
            }
        }
        return true;
    }


    public static boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
    }

    public static boolean isEmpty(List idList) {
        return idList == null || idList.isEmpty();
    }

    public static boolean isEmpty(Object[] stringArray) {
        return stringArray == null || stringArray.length == 0;
    }

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

}
