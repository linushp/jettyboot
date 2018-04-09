package cn.ubibi.jettyboot.framework.commons;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

public class CastTypeUtils {


    public static Object castValueType(Object value, Class<?> targetType) {
        Class<? extends Object> valueType = value.getClass();
        if (isManageAndNeedCast(targetType, valueType, String.class, String.class)) {
            value = value.toString();
        } else {
            value = castValueType(value, targetType);
        }
        return value;
    }


    public static Object castValueType(StringWrapper value, Class<?> targetType) {
        if (value == null) {
            return null;
        }

        if(targetType == StringWrapper.class){
            return value;
        }

        Object result = value.toString();

        Class<? extends Object> valueType = value.getClass();
        if (isManageAndNeedCast(targetType, valueType, String.class, String.class)) {
            result = value.toString();
        } else if (isManageAndNeedCast(targetType, valueType, Integer.class, int.class)) {
            result = value.toInteger();
        } else if (isManageAndNeedCast(targetType, valueType, Float.class, float.class)) {
            result = value.toFloat();
        } else if (isManageAndNeedCast(targetType, valueType, Double.class, double.class)) {
            result = value.toDouble();
        } else if (isManageAndNeedCast(targetType, valueType, Long.class, long.class)) {
            result = value.toLong();
        } else if (isManageAndNeedCast(targetType, valueType, Boolean.class, boolean.class)) {
            result = value.toBoolean();
        } else if (isManageAndNeedCast(targetType, valueType, Short.class, short.class)) {
            result = value.toShort();
        } else if (targetType == BigDecimal.class) {
            result = value.toBigDecimal();
        } else if (isManageAndNeedCast(targetType, valueType, Timestamp.class, Date.class)) {
            result = value.toTimestamp();
        }
        return result;
    }


    /**
     * 判断数据类型是否需要转换
     *
     * @param fieldType
     * @param valueType
     * @param targetClass1
     * @param targetClass2
     * @return
     */
    private static boolean isManageAndNeedCast(Class fieldType, Class valueType, Class targetClass1, Class targetClass2) {
        if (fieldType == targetClass1 || fieldType == targetClass2) {
            if (valueType == fieldType || valueType == targetClass1 || valueType == targetClass2) {
                return false;
            }
            return true;
        }
        return false;
    }


}
