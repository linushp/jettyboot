package cn.ubibi.jettyboot.framework.rest;

import cn.ubibi.jettyboot.framework.commons.FrameworkConfig;
import cn.ubibi.jettyboot.framework.ioc.ServiceManager;
import cn.ubibi.jettyboot.framework.rest.annotation.Service;
import cn.ubibi.jettyboot.framework.rest.ifs.MethodArgumentResolver;
import cn.ubibi.jettyboot.framework.rest.ifs.ControllerAspect;
import cn.ubibi.jettyboot.framework.rest.ifs.ControllerExceptionHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

public class ControllerContextHandler extends ContextHandler {

    private RequestHandler requestHandler;

    private HandlerCollection handlerCollection;

    public ControllerContextHandler(String context) {
        super(context);
        this.requestHandler = new RequestHandler();
        this.handlerCollection = new HandlerCollection(this.requestHandler);
        this.setHandler(this.handlerCollection);

        ServiceManager.getInstance().addService(this);
    }


    public void addController(String path, Class<?> clazz) throws Exception {
        this.requestHandler.addController(path, clazz);
    }

    public void addController(String path, Object restController) throws Exception {
        this.requestHandler.addController(path, restController);
    }

    public void addDwrController(Object restController) throws Exception {
        String path = FrameworkConfig.getInstance().getDwrPrefix() + restController.getClass().getSimpleName();
        this.requestHandler.addController(path, restController);
    }

    public void addExceptionHandler(ControllerExceptionHandler exceptionHandler) throws Exception {
        this.requestHandler.addExceptionHandler(exceptionHandler);
    }

    public void addControllerAspect(ControllerAspect methodAspect) throws Exception {
        this.requestHandler.addControllerAspect(methodAspect);
    }

    public void addMethodArgumentResolver(MethodArgumentResolver argumentResolver) throws Exception {
        this.requestHandler.addMethodArgumentResolver(argumentResolver);
    }

    public void addService(Object service) {
        ServiceManager.getInstance().addService(service);
    }


    public void addServiceByFactory(Object serviceFactory) throws InvocationTargetException, IllegalAccessException {
        Class<?> clazz = serviceFactory.getClass();
        Method[] methods = clazz.getDeclaredMethods();

        if (methods != null) {
            for (Method method : methods) {
                Service annotation = method.getAnnotation(Service.class);
                if (annotation != null) {
                    method.setAccessible(true);
                    Object serviceResult = method.invoke(serviceFactory);
                    if (serviceResult != null) {

                        if (serviceResult instanceof Collection) {

                            Collection serviceList = (Collection) serviceResult;
                            for (Object serviceObject : serviceList) {
                                this.addService(serviceObject);
                            }

                        } else {
                            this.addService(serviceResult);
                        }

                    }
                }
            }
        }
    }


    public void addResourceHandler(ResourceHandler resourceHandler) {
        this.handlerCollection.addHandler(resourceHandler);
    }


    public void addHandler(Handler handler) {
        this.handlerCollection.addHandler(handler);
    }


    /**
     * 报漏给外界，为了用户扩展需要，比如用户自己实现一个DWR框架
     *
     * @return
     */
    public List<ControllerMethodHandler> getControllerMethodHandlers() {
        return this.requestHandler.getControllerMethodHandlers();
    }

}
