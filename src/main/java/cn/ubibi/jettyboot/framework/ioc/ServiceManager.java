package cn.ubibi.jettyboot.framework.ioc;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ServiceManager {
    private static final ServiceManager instance = new ServiceManager();

    public static ServiceManager getInstance() {
        return instance;
    }

    private ServiceManager() {
    }


    private List<Object> serviceList = new ArrayList<>();


    public void addService(Object object) {
        serviceList.add(object);
    }


    //执行注入依赖的过程
    public Object injectDependency(Object controller) throws Exception {
        Field[] fields = controller.getClass().getDeclaredFields();

        if (fields != null && fields.length > 0) {
            for (Field field : fields) {

                //有注解
                Autowired autowired = field.getDeclaredAnnotation(Autowired.class);
                if (autowired != null) {
                    field.setAccessible(true);
                    Object filedValue = field.get(controller);
                    if (filedValue == null) {
                        Object service = findServiceByField(field);
                        if (service != null) {

                            field.set(controller, service);

                            //放在set后面，允许循环依赖，只要调用不循环就行。
                            injectDependency(service);

                        } else {
                            throw new Exception("ServiceNotFound :" + field.getType().getName());
                        }
                    }
                }
            }
        }

        return controller;
    }


    private Object findServiceByField(Field field) throws Exception {
        Class<?> fieldType = field.getType();
        for (Object service : this.serviceList) {
            if (fieldType.isAssignableFrom(service.getClass())) {
                return service;
            }
        }
        return null;
    }

}
