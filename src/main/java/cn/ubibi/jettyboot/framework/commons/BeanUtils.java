package cn.ubibi.jettyboot.framework.commons;

import cn.ubibi.jettyboot.framework.commons.annotation.JSONTextBean;
import cn.ubibi.jettyboot.framework.commons.annotation.JSONTextBeanArray;
import cn.ubibi.jettyboot.framework.commons.ifs.Convertible;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;

public class BeanUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanUtils.class);


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

        if (CollectionUtils.isEmpty(values)) {
            return new ArrayList<>();
        }


        List<BeanField> beanFields = BeanFieldUtils.getBeanFields(clazz);
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
        List<BeanField> beanFields = BeanFieldUtils.getBeanFields(clazz);
        return mapToBean(clazz, map, beanFields);
    }


    private static <T> T mapToBean(Class<? extends T> clazz, Map<String, Object> map, List<BeanField> beanFields) throws IllegalAccessException, InstantiationException {

        if (clazz == null || map == null || beanFields == null) {
            return null;
        }

        T bean = clazz.newInstance();
        if (CollectionUtils.isEmpty(beanFields)) {
            return bean;
        }


        for (BeanField beanField : beanFields) {

            String filedName = beanField.getFieldName();

            //1.得到数据
            Object value = map.get(filedName);
            if (value == null) {
                String filedName2 = beanField.getFieldNameUnderline();
                if (!filedName2.equals(filedName)) {
                    value = map.get(filedName2);
                }
            }

            //2.类型转换
            value = castValueType(value, beanField.getField(), map);
            if (value != null) {
                beanField.setBeanValue(bean, value);
            }

        }

        return bean;
    }


    /**
     * 转换数据类型
     *
     * @param value 原始数据
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


    /**
     * 浅复制
     *
     * @param targetObject 目标对象
     * @param fromObject   源对象
     * @param <T>          类型
     */
    public static <T> void copyField(T targetObject, T fromObject) {
        if (fromObject == null) {
            return;
        }

        try {
            List<BeanField> fields = BeanFieldUtils.getBeanFields(fromObject.getClass());
            if (!CollectionUtils.isEmpty(fields)) {
                for (BeanField field : fields) {
                    Object value = field.getBeanValue(fromObject);
                    field.setBeanValue(targetObject, value);
                }
            }
        } catch (IllegalAccessException e) {
            LOGGER.error("",e);
        }
    }


}
