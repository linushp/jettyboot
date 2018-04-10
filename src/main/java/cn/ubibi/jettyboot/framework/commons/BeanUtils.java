package cn.ubibi.jettyboot.framework.commons;

import cn.ubibi.jettyboot.framework.commons.annotation.JSONTextBean;
import cn.ubibi.jettyboot.framework.commons.annotation.JSONTextBeanArray;
import cn.ubibi.jettyboot.framework.commons.ifs.Convertible;
import com.alibaba.fastjson.JSON;

import java.lang.reflect.Field;
import java.util.*;

public class BeanUtils {


    /**
     * map to bean list
     *
     * @param clazz  the type of target bean
     * @param values from values
     * @param <T>    type
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


            //1.得到数据
            Object value = map.get(filedName);
            if (value == null) {
                String filedName2 = beanField.getFiledNameUnderline();
                if (!filedName2.equals(filedName)) {
                    value = map.get(filedName2);
                }
            }


            //2.类型转换
            value = castValueType(value, field, map);
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
     * @param field 要转换成的目标数据类型
     * @return
     */
    private static Object castValueType(Object value, Field field, Map<String, Object> map) throws IllegalAccessException, InstantiationException {

        Class<?> targetType = field.getType();
        //1. 可以自定义一个类型转换器
        if (Convertible.class.isAssignableFrom(targetType)) {
            Convertible beanCustomField = (Convertible) targetType.newInstance();
            beanCustomField.convertFrom(value, map);
            return beanCustomField;
        }

        if (value == null) {
            return null;
        }


        //2. 根据JSONTextBean注解转换，此时原始的value必须是字符串
        JSONTextBean jsonTextBeanAnnotation = field.getAnnotation(JSONTextBean.class);
        if (jsonTextBeanAnnotation != null) {
            return JSON.parseObject(value.toString(), targetType);
        }

        JSONTextBeanArray jsonTextBeanArrayAnnotation = field.getAnnotation(JSONTextBeanArray.class);
        if (jsonTextBeanArrayAnnotation != null && List.class.isAssignableFrom(targetType)) {
            return JSON.parseArray(value.toString(), jsonTextBeanArrayAnnotation.elementType());
        }


        //3. 简单数据类型转换
        return CastTypeUtils.castValueType(value, targetType);
    }


    private static BeanField[] toBeanFieldExtend(Field[] fields) {
        BeanField[] result = new BeanField[fields.length];

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            result[i] = new BeanField(field);
        }

        return result;
    }


    /**
     * 浅复制
     * @param targetObject
     * @param fromObject
     * @param <T>
     */
    public static <T> void copyField(T targetObject, T fromObject) {
        if (fromObject == null) {
            return;
        }

        try {
            Field[] fields = fromObject.getClass().getDeclaredFields();
            if (fields.length > 0) {
                for (Field field : fields) {
                    field.setAccessible(true);
                    Object value = field.get(fromObject);
                    field.set(targetObject, value);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
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
