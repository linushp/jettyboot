package cn.ubibi.jettyboot.framework.rest;

import cn.ubibi.jettyboot.framework.commons.scan.ClasspathPackageScanner;
import cn.ubibi.jettyboot.framework.commons.scan.PackageScanner;
import cn.ubibi.jettyboot.framework.ioc.ServiceManager;
import cn.ubibi.jettyboot.framework.rest.annotation.Component;
import cn.ubibi.jettyboot.framework.rest.annotation.Controller;
import cn.ubibi.jettyboot.framework.rest.annotation.Service;
import cn.ubibi.jettyboot.framework.rest.ifs.MethodArgumentResolver;
import cn.ubibi.jettyboot.framework.rest.ifs.RequestAspect;
import cn.ubibi.jettyboot.framework.rest.ifs.ExceptionHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class RestContextHandler extends ContextHandler{

    private RequestHandler requestHandler;

    private HandlerCollection handlerCollection;

    public RestContextHandler(String context) {
        super(context);
        this.requestHandler = new RequestHandler();
        this.handlerCollection = new HandlerCollection(this.requestHandler);
        this.setHandler(this.handlerCollection);

        ServiceManager.getInstance().addService(this);
    }


    public void addController(String path,Class<?> clazz) throws Exception {
        this.requestHandler.addController(path,clazz);
    }

    public void addController(String path ,Object restController) throws Exception {
        this.requestHandler.addController(path,restController);
    }

    public void addExceptionHandler(ExceptionHandler exceptionHandler) throws Exception {
        this.requestHandler.addExceptionHandler(exceptionHandler);
    }

    public void addRequestAspect(RequestAspect methodAspect) throws Exception {
        this.requestHandler.addRequestAspect(methodAspect);
    }

    public void addMethodArgumentResolver(MethodArgumentResolver argumentResolver) throws Exception {
        this.requestHandler.addMethodArgumentResolver(argumentResolver);
    }

    public void addService(Object service){
        ServiceManager.getInstance().addService(service);
    }


    public void addResourceHandler(ResourceHandler resourceHandler) {
        this.handlerCollection.addHandler(resourceHandler);
    }


    public void addHandler(Handler handler) {
        this.handlerCollection.addHandler(handler);
    }

    /**
     * 报漏给外界，为了用户扩展需要，比如用户自己实现一个DWR框架
     * @return
     */
    public List<ControllerMethodHandler> getControllerMethodHandlers(){
        return this.requestHandler.getControllerMethodHandlers();
    }


    public void addByPackageScanner(String packageName) throws Exception {
        PackageScanner packageScanner = new ClasspathPackageScanner(packageName);
        this.addByPackageScanner(packageScanner);
    }



    public void addByPackageScanner(PackageScanner packageScanner) throws Exception {

        List<String> classNameList = packageScanner.getFullyQualifiedClassNameList();

        List<Handler> otherHandlers = new ArrayList<>();

        for (String className : classNameList) {

            Class<?> clazz = Class.forName(className);
            Annotation[] annotations = clazz.getAnnotations();
            if (annotations != null && annotations.length > 0) {

                for (Annotation annotation : annotations) {


                    //1
                    if (annotation.annotationType() == Controller.class) {

                        Controller controllerAnnotation = (Controller) annotation;
                        String controllerPath = controllerAnnotation.value();
                        boolean isSingleton = controllerAnnotation.singleton();
                        if (isSingleton) {
                            Object controllerObject = clazz.newInstance();
                            this.addController(controllerPath, controllerObject);
                        } else {
                            this.addController(controllerPath, clazz);
                        }

                    }


                    //2
                    else if (annotation.annotationType() == Service.class) {
                        Object serviceObject = clazz.newInstance();
                        this.addService(serviceObject);

                    }


                    //3
                    else if (annotation.annotationType() == Component.class) {

                        // 3.1
                        if (ExceptionHandler.class.isAssignableFrom(clazz)) {
                            ExceptionHandler exceptionHandlerObject =(ExceptionHandler)clazz.newInstance();
                            this.addExceptionHandler(exceptionHandlerObject);
                        }


                        //3.2
                        else if (RequestAspect.class.isAssignableFrom(clazz)) {
                            RequestAspect requestAspectObject = (RequestAspect) clazz.newInstance();
                            this.addRequestAspect(requestAspectObject);
                        }

                        //3.3
                        else if (MethodArgumentResolver.class.isAssignableFrom(clazz)) {
                            MethodArgumentResolver methodArgumentResolver = (MethodArgumentResolver)clazz.newInstance();
                            this.addMethodArgumentResolver(methodArgumentResolver);
                        }



                        //3.4
                        else if (ResourceHandler.class.isAssignableFrom(clazz)){
                            ResourceHandler resourceHandler = (ResourceHandler)clazz.newInstance();
                            this.addResourceHandler(resourceHandler);
                        }


                        //3.5
                        else if (Handler.class.isAssignableFrom(clazz)){
                            Handler handler = (Handler)clazz.newInstance();
                            otherHandlers.add(handler);
                        }

                    }

                }

            }

        }




        if (!otherHandlers.isEmpty()){
            for (Handler handler : otherHandlers){
                this.addHandler(handler);
            }
        }



    }
}
