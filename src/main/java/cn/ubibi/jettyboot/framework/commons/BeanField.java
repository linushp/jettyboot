package cn.ubibi.jettyboot.framework.commons;

import java.lang.reflect.Field;

public class BeanField {


    private Field field;
    private String fieldName;
    private String fieldNameUnderline;

    public BeanField(Field field) {
        field.setAccessible(true);

        this.field = field;
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
        field.set(bean, value);
    }

    public Object getBeanValue(Object bean) throws IllegalAccessException {
        return field.get(bean);
    }
}

