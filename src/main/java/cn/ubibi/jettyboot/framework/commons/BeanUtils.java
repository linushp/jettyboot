package cn.ubibi.jettyboot.framework.commons;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class BeanUtils {


    /**
     * map to bean list
     * @param clazz the type of target bean
     * @param values from values
     * @param <T> type
     * @return the target bean list
     * @throws Exception 异常
     */
    public static <T> List<T> mapListToBeanList(Class<T> clazz, List<Map<String, Object>> values) throws Exception {

        if (values == null || values.isEmpty()) {
            return new ArrayList<>();
        }


        Field[] fields = clazz.getDeclaredFields();
        BeanField[] beanFields = toBeanFieldExtend(fields);
        List<T> result = new ArrayList<>();

        for (Map<String, Object> m : values) {
            //通过反射创建一个其他类的对象
            T bean = BeanUtils.mapToBean(clazz, m, beanFields);
            result.add(bean);
        }

        return result;
    }


    public static <T> T mapToBean(Class<? extends T> clazz, Map<String, Object> map) throws InstantiationException, IllegalAccessException {
        if (clazz == null || map == null) {
            return null;
        }
        Field[] fields = clazz.getDeclaredFields();
        BeanField[] beanFields = toBeanFieldExtend(fields);
        return mapToBean(clazz, map, beanFields);
    }


    private static <T> T mapToBean(Class<? extends T> clazz, Map<String, Object> map, BeanField[] beanFields) throws IllegalAccessException, InstantiationException {

        if (clazz == null || map == null || beanFields == null) {
            return null;
        }

        T bean = clazz.newInstance();
        if (map.isEmpty() || beanFields.length == 0) {
            return bean;
        }


        for (BeanField beanField : beanFields) {

            Field field = beanField.getField();
            String filedName = beanField.getFiledName();

            Object value = map.get(filedName);
            if (value == null) {
                String filedName2 = beanField.getFiledNameUnderline();
                if (!filedName2.equals(filedName)) {
                    value = map.get(filedName2);
                }
            }

            value = castValueType(value, field.getType(), map);
            if (value != null) {
                field.setAccessible(true);
                field.set(bean, value);
            }
        }

        return bean;
    }


    /**
     * 转换数据类型
     *
     * @param value      原始数据
     * @param targetType 要转换成的目标数据类型
     * @return
     */
    private static Object castValueType(Object value, Class<?> targetType, Map<String, Object> map) throws IllegalAccessException, InstantiationException {


        if (IConvertible.class.isAssignableFrom(targetType)) {
            IConvertible beanCustomField = (IConvertible) targetType.newInstance();
            beanCustomField.convertFrom(value, map);
            return beanCustomField;
        }


        if (value == null) {
            return null;
        }

        Class<? extends Object> valueType = value.getClass();
        if (isManageAndNeedCast(targetType, valueType, String.class, String.class)) {
            value = value.toString();
        } else if (isManageAndNeedCast(targetType, valueType, Integer.class, int.class)) {
            value = new StringWrapper(value.toString()).toInteger();
        } else if (isManageAndNeedCast(targetType, valueType, Float.class, float.class)) {
            value = new StringWrapper(value.toString()).toFloat();
        } else if (isManageAndNeedCast(targetType, valueType, Double.class, double.class)) {
            value = new StringWrapper(value.toString()).toDouble();
        } else if (isManageAndNeedCast(targetType, valueType, Long.class, long.class)) {
            value = new StringWrapper(value.toString()).toLong();
        } else if (isManageAndNeedCast(targetType, valueType, Boolean.class, boolean.class)) {
            value = new StringWrapper(value.toString()).toBoolean();
        } else if (isManageAndNeedCast(targetType, valueType, Short.class, short.class)) {
            value = new StringWrapper(value.toString()).toShort();
        } else if (targetType == BigDecimal.class) {
            value = new StringWrapper(value.toString()).toBigDecimal();
        } else if (isManageAndNeedCast(targetType, valueType, Timestamp.class, Date.class)) {
            value = new StringWrapper(value.toString()).toTimestamp();
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


    private static BeanField[] toBeanFieldExtend(Field[] fields) {
        BeanField[] result = new BeanField[fields.length];

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            result[i] = new BeanField(field);
        }

        return result;
    }


    private static class BeanField {

        private Field field;
        private String filedName;
        private String filedNameUnderline;

        private BeanField(Field field) {
            this.field = field;
            String filedName = field.getName();
            String underlineFiledName = StringUtils.camel2Underline(field.getName());
            this.filedName = filedName;
            if (filedName.equals(underlineFiledName)) {
                this.filedNameUnderline = filedName;//为了加快下次比较速度。
            } else {
                this.filedNameUnderline = underlineFiledName;
            }
        }

        public Field getField() {
            return field;
        }

        public String getFiledName() {
            return filedName;
        }

        public String getFiledNameUnderline() {
            return filedNameUnderline;
        }

    }
}
