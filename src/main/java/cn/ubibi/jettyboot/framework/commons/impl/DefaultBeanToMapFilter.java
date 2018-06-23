package cn.ubibi.jettyboot.framework.commons.impl;

import cn.ubibi.jettyboot.framework.commons.BeanField;
import cn.ubibi.jettyboot.framework.commons.annotation.TextBean;
import cn.ubibi.jettyboot.framework.commons.annotation.TextBeanTypeEnum;
import cn.ubibi.jettyboot.framework.commons.ifs.BeanToMapFilter;
import com.alibaba.fastjson.JSON;

import java.lang.reflect.Field;


public class DefaultBeanToMapFilter implements BeanToMapFilter {
    private boolean isUnderlineKey;

    public DefaultBeanToMapFilter(boolean isUnderlineKey) {
        this.isUnderlineKey = isUnderlineKey;
    }

    public boolean isInclude(Object value, BeanField beanField) throws Exception {
        return true;
    }

    public String getMapKey(BeanField beanField) {
        if (isUnderlineKey) {
            return beanField.getFieldNameUnderline();
        }
        return beanField.getFieldName();
    }

    @Override
    public Object toMapValueType(Object value, BeanField beanField) {

        Field field = beanField.getField();

        // 根据JSONTextBean注解转换，此时原始的value必须是字符串
        TextBean textBean = field.getAnnotation(TextBean.class);
        if (textBean != null) {
            if (textBean.textType() == TextBeanTypeEnum.JSON){
                return JSON.toJSONString(value);
            }
        }

        return value;
    }

}