package cn.ubibi.jettyboot.framework.commons;

import java.lang.reflect.Method;
import java.util.List;


public class ReflectObject {

    private final List<BeanField> beanFields;
    private final List<Method> methodList;
    private Object object;

    public ReflectObject(Object object) {
        this.object = object;
        Class aClass = object.getClass();
        this.beanFields = BeanFieldUtils.getBeanFields(aClass);
        this.methodList = BeanFieldUtils.getBeanMethods(aClass);
    }


    public void setFieldValue(String fieldName, Object value) throws Exception {
        BeanField beanField = getBeanField(fieldName);
        beanField.setBeanValue_autoConvert(this.object, value);
    }


    public void invokeSetter(String setter_name, Object value) throws Exception {
        Method method = getBeanMethod(setter_name);
        method.setAccessible(true);
        method.invoke(this.object, value);
    }


    private Method getBeanMethod(String methodName) {
        for (Method beanField : this.methodList) {
            if (methodName.equals(beanField.getName()) && beanField.getParameterCount() == 1) {
                return beanField;
            }
        }
        return null;
    }


    private BeanField getBeanField(String fieldName) {
        for (BeanField beanField : this.beanFields) {
            if (fieldName.equals(beanField.getFieldName())) {
                return beanField;
            }
        }
        return null;
    }
}
