package cn.ubibi.jettyboot.framework.commons;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

public class CastTypeUtils {


    public static Object castValueType(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }

        Class<? extends Object> valueType = value.getClass();

        if(targetType == valueType || targetType.equals(valueType)){
            return value;
        }


        if (isManageAndNeedCast(targetType, valueType, String.class, String.class)) {
            value = value.toString();
        } else if (isManageAndNeedCast(targetType, valueType, Integer.class, Integer.TYPE)) {
            value = (new StringWrapper(value.toString())).toInteger();
        } else if (isManageAndNeedCast(targetType, valueType, Float.class, Float.TYPE)) {
            value = (new StringWrapper(value.toString())).toFloat();
        } else if (isManageAndNeedCast(targetType, valueType, Double.class, Double.TYPE)) {
            value = (new StringWrapper(value.toString())).toDouble();
        } else if (isManageAndNeedCast(targetType, valueType, Long.class, Long.TYPE)) {
            value = (new StringWrapper(value.toString())).toLong();
        } else if (isManageAndNeedCast(targetType, valueType, Boolean.class, Boolean.TYPE)) {
            value = (new StringWrapper(value.toString())).toBoolean();
        } else if (isManageAndNeedCast(targetType, valueType, Short.class, Short.TYPE)) {
            value = (new StringWrapper(value.toString())).toShort();
        } else if (targetType == BigDecimal.class) {
            value = (new StringWrapper(value.toString())).toBigDecimal();
        } else if (isManageAndNeedCast(targetType, valueType, Timestamp.class, Date.class)) {
            value = (new StringWrapper(value.toString())).toTimestamp();
        }

        return value;
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
