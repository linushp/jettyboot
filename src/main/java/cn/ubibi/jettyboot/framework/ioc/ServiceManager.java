package cn.ubibi.jettyboot.framework.ioc;

import cn.ubibi.jettyboot.framework.commons.BeanField;
import cn.ubibi.jettyboot.framework.commons.BeanFieldUtils;
import cn.ubibi.jettyboot.framework.commons.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceManager.class);

    private static final ServiceManager instance = new ServiceManager();

    public static ServiceManager getInstance() {
        return instance;
    }

    private ServiceManager() {
    }


    private List<Object> realServiceList = new ArrayList<>();
    private List<Object> proxyServiceList = new ArrayList<>();
    private Map<Object, Object> proxyRealServiceMap = new HashMap<>();

    public void addService(Object realServiceObject) {
        if (realServiceObject == null) {
            return;
        }
        realServiceList.add(realServiceObject);
        LOGGER.info("addService:" + realServiceObject.getClass().getName());


        //add interface
        Class<?>[] interfaces = realServiceObject.getClass().getInterfaces();
        if (interfaces != null && interfaces.length > 0) {
            ClassLoader classLoader = realServiceObject.getClass().getClassLoader();
            Object proxyServiceObject = Proxy.newProxyInstance(classLoader, interfaces, new ServiceProxyHandler(realServiceObject));
            proxyServiceList.add(proxyServiceObject);
            proxyRealServiceMap.put(proxyServiceObject, realServiceObject);
        }


    }


    //执行注入依赖的过程
    public void injectDependency(Object serviceObject) throws Exception {

        Object realServiceObject = getRealServiceObject(serviceObject);


        //获取连同父类的字段,这样就能够连同继承的父类的字段也可以注入了。
        List<BeanField> beanFields = BeanFieldUtils.getBeanFields(realServiceObject.getClass());


        if (!CollectionUtils.isEmpty(beanFields)) {

            for (BeanField beanField : beanFields) {

                Field field = beanField.getField();

                //有注解，Autowired注解可被继承
                Autowired autowired = field.getAnnotation(Autowired.class);
                if (autowired != null) {

                    //只会自动注入null的字段
                    Object filedValue = beanField.getBeanValue(realServiceObject);
                    if (filedValue == null) {


                        Object service = findServiceByField(field);
                        if (service != null) {

                            beanField.setBeanValue(realServiceObject, service);
//                            markBeanObjectFieldSettled(beanObject,beanField);

                            //放在set后面，允许循环依赖，只要调用不循环就行。
                            injectDependency(service);

                        } else {
                            throw new Exception("ServiceNotFound :" + field.getType().getName());
                        }
                    }
                }
            }
        }

    }


    private Object getRealServiceObject(Object serviceObject) {
        Object realServiceObject = proxyRealServiceMap.get(serviceObject);
        if (realServiceObject != null) {
            return realServiceObject;
        }
        return serviceObject;
    }


    private Object findServiceByField(Field field) throws Exception {
        Class<?> fieldType = field.getType();
        return getServiceInner(fieldType);
    }


    public Object getService(Class<?> type) throws Exception {
        Object serviceObj = getServiceInner(type);
        if (serviceObj == null) {
            return null;
        }
        injectDependency(serviceObj);
        return serviceObj;
    }


    private Object getServiceInner(Class<?> type) {

        //通过接口获取对对象是代理对象
        if (type.isInterface()) {

            for (Object service : this.proxyServiceList) {
                if (type.isInstance(service)) {
                    return service;
                }
            }

        } else {
            for (Object service : this.realServiceList) {
                if (type.isInstance(service)) {
                    return service;
                }
            }
        }


        return null;
    }

}
