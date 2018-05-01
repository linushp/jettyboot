package cn.ubibi.jettyboot.framework.commons.impl;

import cn.ubibi.jettyboot.framework.commons.BeanField;
import cn.ubibi.jettyboot.framework.commons.CastTypeUtils;
import cn.ubibi.jettyboot.framework.commons.annotation.JSONTextBean;
import cn.ubibi.jettyboot.framework.commons.annotation.JSONTextBeanArray;
import cn.ubibi.jettyboot.framework.commons.ifs.Convertible;
import cn.ubibi.jettyboot.framework.commons.ifs.MapToBeanFilter;
import com.alibaba.fastjson.JSON;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class DefaultMapToBeanFilter implements MapToBeanFilter {

    @Override
    public Object getValue(BeanField beanField, Map<String, Object> map) {

        String filedName = beanField.getFieldName();
        Object value = map.get(filedName);
        if (value == null) {
            String filedName2 = beanField.getFieldNameUnderline();
            if (!filedName2.equals(filedName)) {
                value = map.get(filedName2);
            }
        }

        return value;
    }


    @Override
    public Object toBeanFieldType(Object value1, BeanField beanField, Map<String, Object> map) throws Exception {
        Field field = beanField.getField();
        return castValueType(value1, field, map);
    }


    /**
     * 转换数据类型
     *
     * @param value 原始数据
     * @param field 要转换成的目标数据类型
     * @return
     */
    private Object castValueType(Object value, Field field, Map<String, Object> map) throws IllegalAccessException, InstantiationException {


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
}