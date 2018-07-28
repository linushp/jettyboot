package cn.ubibi.jettyboot.framework.commons;

import cn.ubibi.jettyboot.framework.commons.ifs.CharFilter;
import cn.ubibi.jettyboot.framework.commons.ifs.ObjectFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.util.*;

public class CollectionUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(CollectionUtils.class);

    /**
     * 对集合中的每一个元素变成字符串后添加前缀和后缀
     *
     * @param collection 集合
     * @param prefix     字符串前缀
     * @param suffix     字符串后缀
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


    public static List[] listKeyValues(Map<String, Object> map) {
        List<Object> values = new ArrayList<>();
        List<String> keys = new ArrayList<>();
        Set<Map.Entry<String, Object>> entrys = map.entrySet();
        for (Map.Entry<String, Object> entry : entrys) {
            String key = entry.getKey();
            //忽略掉了空的key
            if (!isEmpty(key)) {
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


    //二维数组转一维数组
    public static List<String> toListAddAll(String[]... array2) {
        List<String> result = new ArrayList<>();
        if (!isEmpty(array2)) {
            for (String[] arr1 : array2) {
                if (!isEmpty(arr1)) {
                    for (String obj : arr1) {
                        result.add(obj);
                    }
                }
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
    public static List filterOnlyLegalItems(List idList, CharFilter idCharFilter) {
        if (isEmpty(idList)) {
            return idList;
        }

        List result = new ArrayList();
        for (Object obj : idList) {
            if (obj instanceof Long || obj instanceof Integer) {
                result.add(obj);
            } else if (obj instanceof String && isLegalStringItem((String) obj, idCharFilter)) {
                result.add(obj);
            }
        }

        return result;
    }


    /**
     * 遍历字符串中的每一个字符，判断是否是合法的字符串
     *
     * @param obj          字符串
     * @param idCharFilter
     * @return
     */
    private static boolean isLegalStringItem(String obj, CharFilter idCharFilter) {
        if (isEmpty(obj)) {
            return false;
        }
        for (int i = 0; i < obj.length(); i++) {
            char cc = obj.charAt(i);
            if (!idCharFilter.isOK(cc)) {
                return false;
            }
        }
        return true;
    }


    public static <T> List<T> filter(List<T> objectList, ObjectFilter<T>... objectFilterArray) {
        List<ObjectFilter<T>> objectFilters = new ArrayList<>(objectFilterArray.length);
        for (ObjectFilter<T> objectFilter : objectFilterArray) {
            objectFilters.add(objectFilter);
        }
        return filterList(objectList, objectFilters);
    }


    public static <T> List<T> filterList(List<T> objectList, List<ObjectFilter<T>> objectFilters) {

        if (objectList == null) {
            return null;
        }

        if (isEmpty(objectFilters)) {
            return objectList;
        }

        List<T> result = new ArrayList<>();
        for (T obj : objectList) {
            if (isFiltersOk(obj, objectFilters)) {
                result.add(obj);
            }
        }

        return result;
    }


    public static <T> boolean isFiltersOk(T obj, List<ObjectFilter<T>> objectFilters) {
        for (ObjectFilter<T> objectFilter : objectFilters) {
            if (!objectFilter.isOK(obj)) {
                return false;
            }
        }
        return true;
    }


    public static boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
    }

    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmpty(Object[] stringArray) {
        return stringArray == null || stringArray.length == 0;
    }

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static <T> List<T> toList(T[] array) {
        if (array == null) {
            return null;
        }
        List<T> result = new ArrayList<>();
        for (T e : array) {
            result.add(e);
        }
        return result;
    }

    //数组转List
    public static List toListFromArray(Object array) {
        if (array == null) {
            return null;
        }

        if (array.getClass().isArray()) {
            List list = new ArrayList();
            int arrayLength = Array.getLength(array);
            for (int i = 0; i < arrayLength; i++) {
                Object obj = Array.get(array, i);
                list.add(obj);
            }
            return list;
        }

        return null;
    }

    //创建list的快捷方式
    public static List toObjectList(Object... objects) {
        if (objects == null) {
            return null;
        }

        List list = new ArrayList<>();
        for (Object object : objects) {
            list.add(object);
        }
        return list;
    }


    public static List getFieldValues(List list, String fieldName) {
        List result = new ArrayList();
        if (isEmpty(list)) {
            return result;
        }

        for (Object object : list) {
            try {
                if (object == null) {
                    result.add(null);
                } else {
                    ReflectObject reflectObject = new ReflectObject(object);
                    Object fieldValue = reflectObject.getFieldValue(fieldName);
                    result.add(fieldValue);
                }
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
        return result;
    }


    public static void setFieldValues(List list, String fieldName, Object fieldValue) {
        if (isEmpty(list)) {
            return;
        }

        for (Object object : list) {
            try {
                if (object != null) {
                    ReflectObject reflectObject = new ReflectObject(object);
                    reflectObject.setFieldValue(fieldName, fieldValue);
                }
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
    }


    //移除重复元素
    public static List uniqueList(List list) {
        if (isEmpty(list)) {
            return list;
        }
        Set set = new LinkedHashSet(list);
        return new ArrayList(set);
    }


    //移除null的元素
    public static List removeNull(List list) {
        if (isEmpty(list)) {
            return list;
        }

        List result = new ArrayList();
        for (Object obj : list) {
            if (obj != null) {
                result.add(obj);
            }
        }
        return result;
    }
}
