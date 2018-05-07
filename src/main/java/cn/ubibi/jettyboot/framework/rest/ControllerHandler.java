package cn.ubibi.jettyboot.framework.rest;


import cn.ubibi.jettyboot.framework.commons.StringUtils;
import cn.ubibi.jettyboot.framework.ioc.ServiceManager;
import cn.ubibi.jettyboot.framework.rest.annotation.*;
import cn.ubibi.jettyboot.framework.rest.ifs.MethodArgumentResolver;
import cn.ubibi.jettyboot.framework.rest.ifs.ControllerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ControllerHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerHandler.class);


    private Class<?> restControllerClazz;
    private Object restController;
    private String path;

    private List<ControllerMethodHandler> controllerMethodList;


    public ControllerHandler(String path, Class<?> clazz, List<ControllerInterceptor> methodAspectList, List<MethodArgumentResolver> methodArgumentResolvers) {
        this.restControllerClazz = clazz;
        this.path = formatClassPath(path);
        this.controllerMethodList = buildMethodHandlerList(methodAspectList, methodArgumentResolvers);
    }


    public ControllerHandler(String path, Object restController, List<ControllerInterceptor> methodAspectList, List<MethodArgumentResolver> methodArgumentResolvers) {
        this.restController = restController;
        this.path = formatClassPath(path);
        this.controllerMethodList = buildMethodHandlerList(methodAspectList, methodArgumentResolvers);
    }


    private List<ControllerMethodHandler> buildMethodHandlerList(List<ControllerInterceptor> methodAspectList, List<MethodArgumentResolver> methodArgumentResolvers) {

        List<ControllerMethodHandler> methodList = new ArrayList<>();
        Class<?> clazz = this.getControllerClass();
        Method[] methods = clazz.getMethods();


        if (methods != null) {
            for (Method method : methods) {

                String classPath = this.path;


                GetMapping methodAnnotation1 = method.getAnnotation(GetMapping.class);
                PostMapping methodAnnotation2 = method.getAnnotation(PostMapping.class);
                PutMapping methodAnnotation3 = method.getAnnotation(PutMapping.class);
                DeleteMapping methodAnnotation4 = method.getAnnotation(DeleteMapping.class);
                DwrFunction methodAnnotation5 = method.getAnnotation(DwrFunction.class);


                List<ControllerMethodHandler> controllerMethodHandler = null;
                if (methodAnnotation1 != null) {
                    controllerMethodHandler = toControllerMethodHandler(methodAnnotation1.value(), "get", classPath, method, methodAspectList, methodArgumentResolvers);
                } else if (methodAnnotation2 != null) {
                    controllerMethodHandler = toControllerMethodHandler(methodAnnotation2.value(), "post", classPath, method, methodAspectList, methodArgumentResolvers);
                } else if (methodAnnotation3 != null) {
                    controllerMethodHandler = toControllerMethodHandler(methodAnnotation3.value(), "put", classPath, method, methodAspectList, methodArgumentResolvers);
                } else if (methodAnnotation4 != null) {
                    controllerMethodHandler = toControllerMethodHandler(methodAnnotation4.value(), "delete", classPath, method, methodAspectList, methodArgumentResolvers);
                } else if (methodAnnotation5 !=null){
                    controllerMethodHandler = toControllerMethodHandler(null,"dwr",classPath,method, methodAspectList, methodArgumentResolvers);
                }

                if (controllerMethodHandler != null && !controllerMethodHandler.isEmpty()) {
                    methodList.addAll(controllerMethodHandler);
                }

            }
        }


        //排序之后路径比较长的优先匹配到
        methodList.sort(new Comparator<ControllerMethodHandler>() {
            @Override
            public int compare(ControllerMethodHandler o1, ControllerMethodHandler o2) {
                return o2.compareTo(o1);
            }
        });


        for (ControllerMethodHandler methodHandler : methodList) {
            LOGGER.info("ControllerMethodHandler : " + methodHandler.toString());
        }


        return methodList;
    }


    //对于一个方法配置多个路径的情况
    private List<ControllerMethodHandler> toControllerMethodHandler(String[] value, String request_method, String classPath, Method method, List<ControllerInterceptor> methodAspectList, List<MethodArgumentResolver> methodArgumentResolvers) {
        List<ControllerMethodHandler> result = new ArrayList<>(1);

        if ("dwr".equals(request_method)){
            String methodPath = "dwr_" + method.getName();
            result.add(new ControllerMethodHandler(methodPath, request_method, classPath, method, methodAspectList, methodArgumentResolvers));
        }

        else if (value != null) {
            for (String methodPath : value) {
                result.add(new ControllerMethodHandler(methodPath, request_method, classPath, method, methodAspectList, methodArgumentResolvers));
            }
        }
        return result;
    }



    public boolean handle(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String reqPath = request.getPathInfo();
        String classPath = this.path;

        List<ControllerMethodHandler> methods = this.controllerMethodList;

        if (isMatchClassPath(reqPath, classPath) && !methods.isEmpty()) {

            for (ControllerMethodHandler method : methods) {
                if (method.isSupportRequest(request)) {
                    Object controller = this.getControllerObject();
                    method.doHandleRequest(controller, request, response);
                    return true;
                }
            }
        }

        //路径不匹配
        return false;
    }


    private String formatClassPath(String path) {
        if (StringUtils.isEmpty(path)) {
            return "/";
        }
        if (!path.startsWith("/")) {
            return "/" + path;
        }
        return path;
    }


    public Class<?> getControllerClass() {
        Class<?> clazz = this.restControllerClazz;
        if (clazz == null) {
            clazz = this.restController.getClass();
        }
        return clazz;
    }


    /**
     * 得到Controller对象，并依赖注入
     *
     * @return
     * @throws Exception
     */
    private Object getControllerObject() throws Exception {
        Object controller = this.restController;
        if (controller == null) {
            controller = restControllerClazz.newInstance();
        }

        //依赖注入
        ServiceManager.getInstance().injectDependency(controller);
        return controller;
    }


    private boolean isMatchClassPath(String reqPath, String classPath) {
        if (classPath == null || classPath.isEmpty() || "/".equals(classPath)) {
            return true;
        }

        if (reqPath.equals(classPath)) {
            return true;
        }

        if (reqPath.startsWith(classPath)) {
            return true;
        }

        return false;
    }


    public List<ControllerMethodHandler> getControllerMethodList() {
        return controllerMethodList;
    }

}
