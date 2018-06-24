package cn.ubibi.jettyboot.framework.commons;

import java.lang.reflect.Method;
import java.util.List;


public class ReflectObject {

    private final List<BeanField> beanFields;
    private final List<Method> methodList;
    private final Object object;

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
        invokeMethod(setter_name,value);
    }

    public void invokeMethod(String methodName,Object... values) throws Exception {
        Method method = getBeanMethod(methodName,values);
        method.setAccessible(true);
        method.invoke(this.object, values);
    }


    private Method getBeanMethod(String methodName,Object... values) {
        for (Method method : this.methodList) {
            if (methodName.equals(method.getName()) && values.length == method.getParameterCount()) {
                if (isParameterTypesMatched(method,values)){
                    return method;
                }
            }
        }
        return null;
    }


    private boolean isParameterTypesMatched(Method beanField ,Object... values){
        Class<?>[] types = beanField.getParameterTypes();
        if (types.length == 0 || values.length ==0){
            return true;
        }

        for (int i=0 ; i < types.length;i++){
            Class<?> paramType = types[i];
            Object value = values[i];
            Class<? extends Object> valueType = value.getClass();
            if (!isClassEqualsOrAssignableFrom(paramType,valueType)){
                return false;
            }
        }

        return true;
    }



    private boolean isClassEqualsOrAssignableFrom(Class<?> paramType, Class<?> valueType) {
        if (paramType.equals(valueType) || paramType.isAssignableFrom(valueType)){
            return true;
        }
        return false;
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
