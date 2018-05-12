package cn.ubibi.jettyboot.framework.commons;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Array;
import java.util.List;



public class CastTypeUtils {

    // 基本数据类型转换
    public static Object toTypeOf(Object value, Class targetType) throws Exception {
        return new BasicConverter(value).toTypeOf(targetType);
    }


    // 基本数据类型转换
    public static Object toTypeArrayOf(List<Object> jsonArray, Class elementType) throws Exception {

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
    public static Object jsonArrayToJavaArray(JSONArray jsonArray, Class elementType) throws Exception {
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
    public static Object jsonObjectToJavaObject(Object obj, Class typeClazz) throws Exception {

        if (obj == null) {
            if (isBasicType(typeClazz)){
                return new BasicConverter(0).toTypeOf(typeClazz);
            }
            return null;
        }


        if (typeClazz.equals(Object.class)) {
            return obj;
        }

        if (typeClazz.equals(String.class)) {
            return obj.toString();
        }


        if (obj instanceof JSONObject) {

            if (typeClazz.equals(JSONObject.class)) {//无需转换
                return obj;
            }

            JSONObject jsonObject = (JSONObject) obj;
            return jsonObject.toJavaObject(typeClazz);
        }

        if (obj instanceof JSONArray) {
            if (typeClazz.isArray()) {
                Class elementType = typeClazz.getComponentType();
                return jsonArrayToJavaArray((JSONArray) obj, elementType);
            }
            return obj;
        }

        return toTypeOf(obj, typeClazz);
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
