package cn.ubibi.jettyboot.framework.commons.impl;

import cn.ubibi.jettyboot.framework.commons.BeanField;
import cn.ubibi.jettyboot.framework.commons.CastBasicTypeUtils;
import cn.ubibi.jettyboot.framework.commons.CastJsonTypeUtils;
import cn.ubibi.jettyboot.framework.commons.annotation.JSONTextBean;
import cn.ubibi.jettyboot.framework.commons.ifs.Convertible;
import cn.ubibi.jettyboot.framework.commons.ifs.MapToBeanFilter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Field;
import java.util.Map;

public class DefaultMapToBeanFilter implements MapToBeanFilter {

    @Override
    public Object getValue(BeanField beanField, Map<String, ?> map) {

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
    public Object toBeanFieldType(Object value1, BeanField beanField, Map<String, ?> map) throws Exception {
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
    private Object castValueType(Object value, Field field, Map<String, ?> map) throws Exception {

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


        //2. 对于字符串数据
        if (value instanceof String || value instanceof StringBuilder || value instanceof StringBuffer) {

            String stringValue = value.toString();
            value = stringValue;


            //2.1 目标类型是JSONObject
            if (targetType == JSONObject.class) {
                return JSON.parseObject(stringValue);
            } else if (targetType == JSONArray.class) {
                return JSON.parseArray(stringValue);
            }

            //2.2 目标类型是普通JAVA类
            JSONTextBean jsonTextBeanAnnotation = field.getAnnotation(JSONTextBean.class);
            if (jsonTextBeanAnnotation != null) {
                Object jsonObject = JSON.parse(stringValue);
                return CastJsonTypeUtils.jsonObjectToJavaObject(jsonObject, targetType);
            }
        }


        //3. 简单数据类型转换
        return CastBasicTypeUtils.toBasicTypeOf(value, targetType);
    }
}