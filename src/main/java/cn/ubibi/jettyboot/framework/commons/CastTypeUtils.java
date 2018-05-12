package cn.ubibi.jettyboot.framework.commons;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;


public class CastTypeUtils {

    // 基本数据类型转换
    public static Object toTypeOf(Object value, Class targetType) throws Exception {
        return new BasicConverter(value).toTypeOf(targetType);
    }


    // 基本数据类型转换
    public static Object toTypeArrayOf(Collection jsonArray, Class elementType) throws Exception {
        if (jsonArray == null) {
            return null;
        }

        Object result = Array.newInstance(elementType, jsonArray.size());
        int index = 0;
        for (Object obj : jsonArray) {
            Object obj2 = toTypeOf(obj, elementType);
            Array.set(result, index, obj2);
            index++;
        }
        return result;

    }


    // JSON对象类型转换
    public static Object jsonArrayToJavaArray(Collection jsonArray, Class elementType) throws Exception {
        if (jsonArray == null) {
            return null;
        }
        Object result = Array.newInstance(elementType, jsonArray.size());
        int index = 0;
        for (Object obj : jsonArray) {
            Object obj2 = jsonObjectToJavaObject(obj, elementType);
            Array.set(result, index, obj2);
            index++;
        }
        return result;
    }


    // JSON对象类型转换
    public static Object jsonObjectToJavaObject(Object obj, Class targetClazz) throws Exception {

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

            if (targetClazz.equals(JSONObject.class) || targetClazz.equals(Map.class)) {//无需转换
                return obj;
            }

            JSONObject jsonObject = (JSONObject) obj;
            return jsonObject.toJavaObject(targetClazz);
        }


        //JSONArray 也是一个 Collection
        if (obj instanceof Collection) {
            if (targetClazz.isArray()) {
                Class elementType = targetClazz.getComponentType();
                return jsonArrayToJavaArray((Collection) obj, elementType);
            }
            return obj;
        }

        return toTypeOf(obj, targetClazz);
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
