package cn.ubibi.jettyboot.framework.commons.impl;

import cn.ubibi.jettyboot.framework.commons.BeanField;
import cn.ubibi.jettyboot.framework.commons.ifs.MapToBeanFilter;

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
    public Object toBeanFieldType(Object value, BeanField beanField, Map<String, ?> map) throws Exception {
        return beanField.valueOf(value);
    }
}