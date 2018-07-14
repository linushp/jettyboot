package cn.ubibi.jettyboot.framework.rest;

import cn.ubibi.jettyboot.framework.commons.FrameworkConfig;
import cn.ubibi.jettyboot.framework.ioc.ServiceManager;
import cn.ubibi.jettyboot.framework.rest.annotation.Controller;
import cn.ubibi.jettyboot.framework.rest.annotation.Service;
import cn.ubibi.jettyboot.framework.rest.ifs.*;
import cn.ubibi.jettyboot.framework.rest.impl.DefaultDwrScriptController;
import cn.ubibi.jettyboot.framework.slot.SlotComponentManager;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.session.SessionHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

public class ControllerContextHandler extends ContextHandler {

    private RequestHandler requestHandler;

    private HandlerCollection handlerCollection;

    public ControllerContextHandler() {
        this("/",null);
    }

    public ControllerContextHandler(String context) {
        this(context,null);
    }

    public ControllerContextHandler(String context, SessionHandler sessionHandler) {
        super(context);
        this.requestHandler = new RequestHandler();
        this.handlerCollection = new HandlerCollection(this.requestHandler);


        if (sessionHandler!=null) {
            sessionHandler.setHandler(this.handlerCollection);
            this.setHandler(sessionHandler);
        } else {
            this.setHandler(this.handlerCollection);
        }


        ServiceManager.getInstance().addService(this);
    }


    public void addController(String path, Class<?> clazz) throws Exception {
        this.requestHandler.addController(path, clazz);
    }

    public void addController(String path, Object restController) throws Exception {
        this.requestHandler.addController(path, restController);
    }


    public void addController(Object restController) throws Exception {
        Controller x = restController.getClass().getAnnotation(Controller.class);
        String[] pathArr = x.value();
        for(String path : pathArr){
            this.requestHandler.addController(path, restController);
        }
    }


    public void addDwrController(Object restController) throws Exception {
        String path = FrameworkConfig.getInstance().getDwrPrefix() + restController.getClass().getSimpleName();
        FrameworkConfig.getInstance().addDwrControllerName(restController.getClass().getSimpleName());
        this.requestHandler.addController(path, restController);
    }

    public void addExceptionHandler(ControllerExceptionHandler exceptionHandler) throws Exception {
        this.requestHandler.addExceptionHandler(exceptionHandler);
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


    public void addControllerAspect(ControllerAspect controllerAspect) {
        SlotComponentManager.getInstance().getControllerAspects().add(controllerAspect);
    }


    public void addMethodArgumentResolver(MethodArgumentResolver resolver) {
        SlotComponentManager.getInstance().getMethodArgumentResolverList().add(resolver);
    }

    public void setSlotHttpParsedRequestFactory(HttpParsedRequestFactory httpParsedRequestFactory) {
        SlotComponentManager.getInstance().setHttpParsedRequestFactory(httpParsedRequestFactory);
    }

    public void setSlotHttpPathComparator(HttpPathComparator httpPathComparator) {
        SlotComponentManager.getInstance().setHttpPathComparator(httpPathComparator);
    }

    public void usingDefaultDwrScript() throws Exception {
        addController(FrameworkConfig.getInstance().getDwrScriptPath(), new DefaultDwrScriptController());
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
