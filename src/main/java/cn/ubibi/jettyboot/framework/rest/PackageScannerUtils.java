package cn.ubibi.jettyboot.framework.rest;

import cn.ubibi.jettyboot.framework.commons.scan.ClasspathPackageScanner;
import cn.ubibi.jettyboot.framework.commons.scan.PackageScanner;
import cn.ubibi.jettyboot.framework.rest.annotation.*;
import cn.ubibi.jettyboot.framework.rest.ifs.*;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

public class PackageScannerUtils {

    public static void addByPackageScanner(String packageName, ControllerContextHandler controllerContextHandler) throws Exception {
        PackageScanner packageScanner = new ClasspathPackageScanner(packageName);
        addByPackageScanner(packageScanner, controllerContextHandler, null);
    }


    public static void addByPackageScanner(String packageName, ControllerContextHandler controllerContextHandler, JettyBootServer restServer) throws Exception {
        PackageScanner packageScanner = new ClasspathPackageScanner(packageName);
        addByPackageScanner(packageScanner, controllerContextHandler, restServer);
    }


    public static void addByPackageScanner(PackageScanner packageScanner, ControllerContextHandler controllerContextHandler, JettyBootServer restServer) throws Exception {

        List<String> classNameList = packageScanner.getFullyQualifiedClassNameList();


        for (String className : classNameList) {

            Class<?> clazz = Class.forName(className, true, controllerContextHandler.getClass().getClassLoader());
            Annotation[] annotations = clazz.getAnnotations();
            if (annotations != null && annotations.length > 0) {

                for (Annotation annotation : annotations) {


                    //1
                    if (annotation.annotationType() == Controller.class) {
                        Controller controllerAnnotation = (Controller) annotation;
                        String[] controllerPath = controllerAnnotation.value();

                        boolean isSingleton = controllerAnnotation.singleton();
                        if (isSingleton) {

                            Object controllerObject = clazz.newInstance();

                            for (String controllerPath1 : controllerPath) {
                                controllerContextHandler.addController(controllerPath1, controllerObject);
                            }
                        } else {
                            for (String controllerPath1 : controllerPath) {
                                controllerContextHandler.addController(controllerPath1, clazz);
                            }
                        }
                    } else if (annotation.annotationType() == RpcController.class) {
                        Object controllerObject = clazz.newInstance();
                        controllerContextHandler.addRpcController(controllerObject);
                    }

                    //2
                    else if (annotation.annotationType() == Service.class) {
                        Object serviceObject = clazz.newInstance();
                        controllerContextHandler.addService(serviceObject);
                    } else if (annotation.annotationType() == ServiceFactory.class) {
                        Object serviceFactory = clazz.newInstance();
                        controllerContextHandler.addServiceByFactory(serviceFactory);
                    }

                    //3
                    else if (annotation.annotationType() == Component.class) {
                        Object object = clazz.newInstance();
                        addByComponent(object, controllerContextHandler, restServer);
                    }

                    //4
                    else if (annotation.annotationType() == ComponentFactory.class) {
                        addByComponentFactory(clazz, controllerContextHandler, restServer);
                    }

                }

            }

        }// for end


    }


    private static void addByComponentFactory(Class<?> clazz, ControllerContextHandler controllerContextHandler, JettyBootServer restServer) throws Exception {
        Object componentFactory = clazz.newInstance();
        Method[] methods = clazz.getDeclaredMethods();

        if (methods != null) {
            for (Method method : methods) {
                Component componentAnnotation = method.getAnnotation(Component.class);
                if (componentAnnotation != null) {
                    method.setAccessible(true);
                    Object object = method.invoke(componentFactory);
                    if (object != null) {
                        addByComponent(object, controllerContextHandler, restServer);
                    }
                }
            }
        }

    }


    private static void addByComponent(Object object, ControllerContextHandler controllerContextHandler, JettyBootServer restServer) throws Exception {
        if (object == null) {
            return;
        }

        // 3.1
        if (object instanceof ControllerExceptionHandler) {
            controllerContextHandler.addExceptionHandler((ControllerExceptionHandler) object);
        }


        //3.2
        else if (object instanceof ControllerAspect) {
            controllerContextHandler.addControllerAspect((ControllerAspect) object);
        }

        //3.3
        else if (object instanceof MethodArgumentResolver) {
            controllerContextHandler.addMethodArgumentResolver((MethodArgumentResolver) object);
        }

        //3.4
        else if (object instanceof ContextHandler) {
            if (restServer != null) {
                restServer.addContextHandler((ContextHandler) object);
            }
        }

        //3.4
        else if (object instanceof ResourceHandler) {
            controllerContextHandler.addResourceHandler((ResourceHandler) object);
        }

        //3.5
        else if (object instanceof Handler) {
            controllerContextHandler.addHandler((Handler) object);
        }

        //3.6
        else if (object instanceof HttpParsedRequestFactory) {
            controllerContextHandler.setSlotHttpParsedRequestFactory((HttpParsedRequestFactory) object);
        }

        //3.7
        else if (object instanceof HttpPathComparator) {
            controllerContextHandler.setSlotHttpPathComparator((HttpPathComparator) object);
        }

        //其他不认识的就不处理
    }
}
