package cn.ubibi.jettyboot.framework.rest;


import cn.ubibi.jettyboot.framework.commons.StringUtils;
import cn.ubibi.jettyboot.framework.ioc.ServiceManager;
import cn.ubibi.jettyboot.framework.rest.annotation.*;
import cn.ubibi.jettyboot.framework.rest.ifs.MethodArgumentResolver;
import cn.ubibi.jettyboot.framework.rest.ifs.RequestAspect;
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


    public ControllerHandler(String path, Class<?> clazz, List<RequestAspect> methodAspectList, List<MethodArgumentResolver> methodArgumentResolvers) {
        this.restControllerClazz = clazz;
        this.path = path;
        this.controllerMethodList = buildMethodHandlerList(methodAspectList, methodArgumentResolvers);
    }


    public ControllerHandler(String path, Object restController, List<RequestAspect> methodAspectList, List<MethodArgumentResolver> methodArgumentResolvers) {
        this.restController = restController;
        this.path = path;
        this.controllerMethodList = buildMethodHandlerList(methodAspectList, methodArgumentResolvers);
    }


    private List<ControllerMethodHandler> buildMethodHandlerList(List<RequestAspect> methodAspectList, List<MethodArgumentResolver> methodArgumentResolvers) {

        List<ControllerMethodHandler> methodList = new ArrayList<>();
        Class<?> clazz = this.getControllerClass();
        Method[] methods = clazz.getMethods();


        if (methods != null) {
            for (Method method : methods) {

                String classPath = this.getClassPath();


                GetMapping methodAnnotation1 = method.getAnnotation(GetMapping.class);
                PostMapping methodAnnotation2 = method.getAnnotation(PostMapping.class);
                PutMapping methodAnnotation3 = method.getAnnotation(PutMapping.class);
                DeleteMapping methodAnnotation4 = method.getAnnotation(DeleteMapping.class);


                ControllerMethodHandler controllerMethodHandler = null;
                if (methodAnnotation1 != null) {
                    controllerMethodHandler = new ControllerMethodHandler(methodAnnotation1.value(), "get", classPath, method, methodAspectList, methodArgumentResolvers);
                } else if (methodAnnotation2 != null) {
                    controllerMethodHandler = new ControllerMethodHandler(methodAnnotation2.value(), "post", classPath, method, methodAspectList, methodArgumentResolvers);
                } else if (methodAnnotation3 != null) {
                    controllerMethodHandler = new ControllerMethodHandler(methodAnnotation3.value(), "put", classPath, method, methodAspectList, methodArgumentResolvers);
                } else if (methodAnnotation4 != null) {
                    controllerMethodHandler = new ControllerMethodHandler(methodAnnotation4.value(), "delete", classPath, method, methodAspectList, methodArgumentResolvers);
                }


                if (controllerMethodHandler != null) {
                    methodList.add(controllerMethodHandler);
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



        for (ControllerMethodHandler methodHandler:methodList){
            LOGGER.info("ControllerMethodHandler : " + methodHandler.toString());
        }


        return methodList;
    }



    public boolean handle(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String reqPath = request.getPathInfo();
        String classPath = this.getClassPath();

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


    private String getClassPath() {
        if (StringUtils.isEmpty(this.path)) {
            return "/";
        }
        return this.path;
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
