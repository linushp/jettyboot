package cn.ubibi.jettyboot.framework.ioc;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import cn.ubibi.jettyboot.framework.commons.BeanField;
import cn.ubibi.jettyboot.framework.commons.BeanFieldUtils;
import cn.ubibi.jettyboot.framework.commons.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceManager.class);

    private static final ServiceManager instance = new ServiceManager();

    public static ServiceManager getInstance() {
        return instance;
    }

    private ServiceManager() {
    }


    private List<Object> serviceList = new ArrayList<>();


    public void addService(Object serviceObject) {
        if (serviceObject == null) {
            return;
        }
        serviceList.add(serviceObject);
        LOGGER.info("addService:" + serviceObject.getClass().getName());
    }


    //执行注入依赖的过程
    public void injectDependency(Object beanObject) throws Exception {

        //获取连同父类的字段,这样就能够连同继承的父类的字段也可以注入了。
        List<BeanField> beanFields = BeanFieldUtils.getBeanFields(beanObject.getClass());

        if (!CollectionUtils.isEmpty(beanFields)) {

            for (BeanField beanField : beanFields) {

                Field field = beanField.getField();

                //有注解，Autowired注解可被继承
                Autowired autowired = field.getAnnotation(Autowired.class);
                if (autowired != null) {

                    //只会自动注入null的字段
                    Object filedValue = beanField.getBeanValue(beanObject);
                    if (filedValue == null) {


                        Object service = findServiceByField(field);
                        if (service != null) {

                            beanField.setBeanValue(beanObject, service);

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


    private Object findServiceByField(Field field) throws Exception {
        Class<?> fieldType = field.getType();
        return getService(fieldType);
    }


    public Object getService(Class<?> type){
        for (Object service : this.serviceList) {
            Class serviceClass = service.getClass();
            if (type == serviceClass || type.isAssignableFrom(serviceClass)) {
                return service;
            }
        }
        return null;
    }

}
