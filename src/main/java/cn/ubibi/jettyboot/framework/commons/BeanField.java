package cn.ubibi.jettyboot.framework.commons;

import cn.ubibi.jettyboot.framework.commons.annotation.JSONTextBean;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Field;

public class BeanField {


    private Field field;
    private String fieldName;
    private String fieldNameUnderline;

    public BeanField(Field field) {
        this.field = field;
        setAccessible();

        String filedName = field.getName();
        String underlineFiledName = StringUtils.camel2Underline(field.getName());
        this.fieldName = filedName;
        if (filedName.equals(underlineFiledName)) {
            this.fieldNameUnderline = filedName;//为了加快下次比较速度。
        } else {
            this.fieldNameUnderline = underlineFiledName;
        }
    }


    public Field getField() {
        return field;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldNameUnderline() {
        return fieldNameUnderline;
    }


    public void setBeanValue(Object bean, Object value) throws IllegalAccessException {
        setAccessible();
        field.set(bean, value);
    }


    public Object getBeanValue(Object bean) throws IllegalAccessException {
        setAccessible();
        return field.get(bean);
    }

    private void setAccessible() {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
    }



    public Object valueOf(Object value) throws Exception {

        if (value == null) {
            return null;
        }


        Field field = this.field;

        Class<?> targetType = field.getType();

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

