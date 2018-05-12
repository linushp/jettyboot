package cn.ubibi.jettyboot.framework.commons;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;


public class CastTypeUtils {

    // 基本数据类型转换
    public static Object toTypeOf(Object value, Class targetType) throws Exception {
        if (targetType == null) {
            return value;
        }
        return new BasicConverter(value).toTypeOf(targetType);
    }


    // 基本数据类型转换,返回一个  Array.newInstance
    public static Object toTypeArrayOf(Collection jsonArray, Class elementType) throws Exception {
        if (jsonArray == null) {
            return null;
        }
        Object array_result = Array.newInstance(elementType, jsonArray.size());
        int index = 0;
        for (Object obj : jsonArray) {
            Object obj2 = toTypeOf(obj, elementType);
            Array.set(array_result, index, obj2);
            index++;
        }
        return array_result;

    }

    // 基本数据类型转换,返回一个  Array.newInstance
    public static Collection toTypeCollectionOf(Collection jsonArray, Class<? extends Collection> targetClazz, Class elementType) throws Exception {
        if (jsonArray == null) {
            return null;
        }
        Collection array_result = targetClazz.newInstance();
        for (Object obj : jsonArray) {
            Object obj2 = obj;
            if (elementType != null) {
                obj2 = toTypeOf(obj, elementType);
            }
            array_result.add(obj2);
        }
        return array_result;
    }


    // JSON对象类型转换
    public static Object jsonObjectToJavaObject(Object obj, Type type) throws Exception {

        Class targetClazz = null;
        Type[] actualTypeArguments = null;
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            targetClazz = (Class) parameterizedType.getRawType();
            actualTypeArguments = parameterizedType.getActualTypeArguments();
        } else {
            targetClazz = (Class) type;
        }


        if (obj == null) {
            if (isBasicType(targetClazz)) {
                return new BasicConverter(0).toTypeOf(targetClazz);
            }
            return null;
        }


        if (targetClazz.equals(Object.class)) {
            return obj;
        }

        if (targetClazz.equals(String.class)) {
            return obj.toString();
        }


        if (obj instanceof JSONObject) {

            if (targetClazz.equals(JSONObject.class)) {
                //无需转换
                return obj;
            }


            if (Map.class.isAssignableFrom(targetClazz)) {
                if (targetClazz.equals(Map.class)) {
                    targetClazz = HashMap.class;
                } else if (SortedMap.class.equals(targetClazz)) {
                    targetClazz = TreeMap.class;
                }
                return jsonObjectToMapObject((JSONObject) obj, targetClazz, actualTypeArguments);
            }


            JSONObject jsonObject = (JSONObject) obj;
            return jsonObject.toJavaObject(targetClazz);
        }


        if (obj instanceof JSONArray) {

            if (targetClazz.equals(JSONArray.class)) {
                //无需转换
                return obj;
            }

            if (targetClazz.isArray()) {
                Class elementType = targetClazz.getComponentType();
                return jsonArrayToJavaArray((Collection) obj, elementType);
            }


            if (Collection.class.isAssignableFrom(targetClazz)) {
                if (Collection.class.equals(targetClazz) || List.class.equals(targetClazz)) {
                    targetClazz = ArrayList.class;
                } else if (Set.class.equals(targetClazz)) {
                    targetClazz = HashSet.class;
                } else if (SortedSet.class.equals(targetClazz)) {
                    targetClazz = TreeSet.class;
                } else if (Queue.class.equals(targetClazz)) {
                    targetClazz = LinkedList.class;
                }
                return jsonArrayToJavaCollection((JSONArray) obj, targetClazz, actualTypeArguments);
            }

            return obj;
        }

        return toTypeOf(obj, targetClazz);
    }


    private static Collection jsonArrayToJavaCollection(JSONArray jsonArray, Class<? extends Collection> targetClazz, Type[] actualTypeArguments) throws Exception {
        if (jsonArray == null) {
            return null;
        }

        Type elementType = null;
        if (!CollectionUtils.isEmpty(actualTypeArguments)) {
            elementType = actualTypeArguments[0];
        }


        Collection list_result = targetClazz.newInstance();
        for (Object obj : jsonArray) {

            Object obj2 = obj;
            if (elementType != null) {
                obj2 = jsonObjectToJavaObject(obj, elementType);
            }

            list_result.add(obj2);
        }
        return list_result;
    }


    // JSON对象类型转换
    private static Object jsonArrayToJavaArray(Collection jsonArray, Class elementType) throws Exception {
        if (jsonArray == null) {
            return null;
        }
        Object array_result = Array.newInstance(elementType, jsonArray.size());
        int index = 0;
        for (Object obj : jsonArray) {
            Object obj2 = jsonObjectToJavaObject(obj, elementType);
            Array.set(array_result, index, obj2);
            index++;
        }
        return array_result;
    }


    private static Map jsonObjectToMapObject(JSONObject jsonObject, Class targetClazz, Type[] actualTypeArguments) throws Exception {

        Type keyType = null;
        Type valueType = null;

        if (!CollectionUtils.isEmpty(actualTypeArguments)) {
            keyType = actualTypeArguments[0];
            valueType = actualTypeArguments[1];
        }


        Map map = (Map) targetClazz.newInstance();

        Set<Map.Entry<String, Object>> entrySet = jsonObject.entrySet();

        for (Map.Entry<String, Object> entry : entrySet) {

            Object key = entry.getKey();
            Object value = entry.getValue();

            if (keyType != null) {
                key = toTypeOf(key, (Class) keyType);
            }

            if (valueType != null) {
                value = jsonObjectToJavaObject(value, valueType);
            }

            map.put(key, value);
        }
        return map;
    }


    public static boolean isBasicType(Class type) {
        if (type == Integer.TYPE ||
                type == Long.TYPE ||
                type == Short.TYPE ||
                type == Double.TYPE ||
                type == Float.TYPE ||
                type == Boolean.TYPE ||
                type == Character.TYPE ||
                type == Byte.TYPE) {
            return true;
        }
        return false;
    }
}
