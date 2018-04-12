package cn.ubibi.jettyboot.framework.rest;


import cn.ubibi.jettyboot.framework.commons.*;
import cn.ubibi.jettyboot.framework.ioc.ServiceManager;
import cn.ubibi.jettyboot.framework.rest.annotation.*;
import cn.ubibi.jettyboot.framework.rest.ifs.MethodArgumentResolver;
import cn.ubibi.jettyboot.framework.rest.ifs.RequestAspect;
import cn.ubibi.jettyboot.framework.rest.ifs.ResponseRender;
import cn.ubibi.jettyboot.framework.rest.ifs.RequestParser;
import cn.ubibi.jettyboot.framework.rest.impl.JsonRender;
import cn.ubibi.jettyboot.framework.rest.impl.TextRender;
import cn.ubibi.jettyboot.framework.rest.model.MethodArgument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ControllerHandler {

    private static Logger logger = LoggerFactory.getLogger(ControllerHandler.class);

    private List<RequestAspect> methodAspectList;
    private List<MethodArgumentResolver> methodArgumentResolvers;


    private Class<?> restControllerClazz;
    private Object restController;
    private String path;

    public ControllerHandler(String path, Class<?> clazz, List<RequestAspect> methodAspectList, List<MethodArgumentResolver> methodArgumentResolvers) {
        this.restControllerClazz = clazz;
        this.path = path;
        this.methodAspectList = methodAspectList;
        this.methodArgumentResolvers = methodArgumentResolvers;
    }

    public ControllerHandler(String path, Object restController, List<RequestAspect> methodAspectList, List<MethodArgumentResolver> methodArgumentResolvers) {
        this.restController = restController;
        this.path = path;
        this.methodAspectList = methodAspectList;
        this.methodArgumentResolvers = methodArgumentResolvers;
    }


    public boolean handle(HttpServletRequest request, HttpServletResponse response) throws Exception {


        String reqPath = request.getPathInfo();
        Class<?> clazz = this.getControllerClass();
        String classPath = this.getClassPath();

        if (isMatchClassPath(reqPath, classPath)) {

            Method[] methods = clazz.getMethods();
            if (methods != null) {
                for (Method method : methods) {


                    GetMapping methodAnnotation1 = method.getAnnotation(GetMapping.class);
                    PostMapping methodAnnotation2 = method.getAnnotation(PostMapping.class);
                    PutMapping methodAnnotation3 = method.getAnnotation(PutMapping.class);
                    DeleteMapping methodAnnotation4 = method.getAnnotation(DeleteMapping.class);
                    boolean handleResult = false;
                    if (methodAnnotation1 != null) {
                        handleResult = handleMethodPath(classPath, methodAnnotation1.value(), "get", request, response, method);
                    } else if (methodAnnotation2 != null) {
                        handleResult = handleMethodPath(classPath, methodAnnotation2.value(), "post", request, response, method);
                    } else if (methodAnnotation3 != null) {
                        handleResult = handleMethodPath(classPath, methodAnnotation3.value(), "put", request, response, method);
                    } else if (methodAnnotation4 != null) {
                        handleResult = handleMethodPath(classPath, methodAnnotation4.value(), "delete", request, response, method);
                    }

                    if (handleResult) {
                        return true;
                    }
                }
            }
        }


        //路径不匹配
        return false;
    }


    private String getClassPath() {
        if (this.path == null || this.path.isEmpty()) {
            return "/";
        }
        return this.path;
    }


    private Class<?> getControllerClass() {
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
    private Object getRestController() throws Exception {
        Object controller = this.restController;
        if (controller == null) {
            controller = restControllerClazz.newInstance();
        }

        //依赖注入
        ServiceManager.getInstance().injectDependency(controller);

        return controller;
    }


    private boolean handleMethodPath(String classPath, String methodPath, String method, HttpServletRequest request, HttpServletResponse response, Method methodFunc) throws Exception {
        if (method.equalsIgnoreCase(request.getMethod())) {

            String reqPath = request.getPathInfo();

            String targetPath = pathJoin(classPath, methodPath);
            boolean isMethodMatchOK = isHandleMethodPath(targetPath, reqPath);
            if (isMethodMatchOK) {
                handleMethod(request, response, methodFunc, targetPath);
                return true;
            }
        }

        return false;
    }


    private void handleMethod(HttpServletRequest request, HttpServletResponse response, Method method, String targetPath) throws Exception {

        Object controller = this.getRestController();

        Object invokeResult;
        try {

            List<RequestAspect> methodWrappers = this.methodAspectList;

            Request jbRequest = getJBRequestInstance(request, targetPath);

            //准备参数
            Object[] paramsObjects = getMethodParamsObjects(method, jbRequest, request, response);

            //Aspect前置
            for (RequestAspect methodWrapper : methodWrappers) {
                methodWrapper.invokeBefore(method, jbRequest);
            }

            //方法调用
            invokeResult = method.invoke(controller, paramsObjects);


            //Aspect后置
            for (RequestAspect methodWrapper : methodWrappers) {
                methodWrapper.invokeAfter(method, jbRequest, invokeResult);
            }


        } catch (Exception e) {
            throw e;
        }


        //2.执行Render
        if (invokeResult instanceof ResponseRender) {
            ((ResponseRender) invokeResult).doRender(request, response);
        } else if (invokeResult instanceof String) {
            new TextRender(invokeResult.toString()).doRender(request, response);
        } else {
            new JsonRender(invokeResult).doRender(request, response);
        }
    }


    private boolean isHandleMethodPath(String configPath, String reqPath) {
        if (configPath.equals(reqPath)) {
            return true;
        }

        //   /user/:id
        //   /user/23332
        List<String> path1Array = CollectionUtils.removeEmpty(configPath.split("/"));
        List<String> path2Array = CollectionUtils.removeEmpty(reqPath.split("/"));
        if (path1Array.size() != path2Array.size()) {
            return false;
        }

        for (int i = 0; i < path1Array.size(); i++) {
            String pp1 = path1Array.get(i);
            String pp2 = path2Array.get(i);
            if (!isPathEquals(pp1, pp2)) {
                return false;
            }
        }

        return true;
    }

    private boolean isPathEquals(String configPath, String pp2) {
        if (configPath.equals(pp2)) {
            return true;
        }

        if (configPath.startsWith(":")) {
            return true;
        }

        return false;
    }


    private String pathJoin(String path1, String path2) {

        List<String> path1Arr = CollectionUtils.removeEmpty(path1.split("/"));
        List<String> path2Arr = CollectionUtils.removeEmpty(path2.split("/"));


        List<String> pathList = new ArrayList<>();

        pathList.addAll(path1Arr);
        pathList.addAll(path2Arr);

        String result = "/" + StringUtils.join(pathList, "/");
        return result;
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


    private Object[] getMethodParamsObjects(Method method, Request jbRequest, HttpServletRequest request, HttpServletResponse response) throws Exception {

        int paramsCount = method.getParameterCount();
        Type[] paramsTypes = method.getGenericParameterTypes();
        Annotation[][] paramAnnotations = method.getParameterAnnotations();


        if (paramsCount == 0) {
            return new Object[]{};
        }


        List<Object> objects = new ArrayList<>();

        for (int i = 0; i < paramsCount; i++) {

            Type type = paramsTypes[i];
            Annotation[] annotations = paramAnnotations[i];

            MethodArgument methodArgument = new MethodArgument(method, type, annotations);

            Object object;
            MethodArgumentResolver methodArgumentResolver = findMethodArgumentResolver(methodArgument);
            if (methodArgumentResolver != null) {
                object = methodArgumentResolver.resolveArgument(methodArgument, jbRequest);
            } else {
                object = getMethodParamsObject(methodArgument, jbRequest, request, response);
            }

            objects.add(object);
        }

        return objects.toArray();
    }



    private MethodArgumentResolver findMethodArgumentResolver(MethodArgument methodArgument) {
        for (MethodArgumentResolver resolver : methodArgumentResolvers) {
            if (resolver.isSupport(methodArgument)) {
                return resolver;
            }
        }
        return null;
    }


    /**
     * 获取某个方法参数的默认实现
     *
     * @param methodArgument     类型
     * @param jbRequest 请求参数
     * @param request            请求对象
     * @param response           响应
     * @return
     * @throws IOException
     */
    private Object getMethodParamsObject(MethodArgument methodArgument, Request jbRequest, HttpServletRequest request, HttpServletResponse response) throws Exception {


        Class typeClazz = (Class) methodArgument.getType();

        Annotation[] annotations = methodArgument.getAnnotations();

        Annotation annotation = null;
        if (annotations != null && annotations.length > 0) {
            annotation = annotations[0];
        }


        Object object = null;

        //1.通过注解注入
        if (annotation != null) {

            Class<? extends Annotation> annotationType = annotation.annotationType();
            if (annotationType == RequestParam.class) {

                RequestParam requestParam = (RequestParam) annotation;
                String paramName = requestParam.value();
                Class elementType = requestParam.elementType();
                if (typeClazz.isArray()) {
                    elementType = typeClazz.getComponentType();
                    object = getArrayParamValue(jbRequest, paramName, elementType);
                } else if (List.class.isAssignableFrom(typeClazz)) {
                    object = getListParamValue(jbRequest, paramName, elementType);
                } else if (Set.class.isAssignableFrom(typeClazz)) {
                    object = getSetParamValue(jbRequest, paramName, elementType);
                } else {
                    StringWrapper sw = jbRequest.getRequestParam(paramName, requestParam.defaultValue());
                    object = CastTypeUtils.castValueType(sw, typeClazz);
                }

            } else if (annotationType == RequestParams.class) {
                object = jbRequest.getRequestParamObject(typeClazz);
            } else if (annotationType == RequestBody.class) {
                object = jbRequest.getRequestBodyObject(typeClazz);
            } else if (annotationType == PathVariable.class) {
                PathVariable requestPath = (PathVariable) annotation;
                String sw = jbRequest.getPathVariable(requestPath.name());
                object = CastTypeUtils.castValueType(sw, typeClazz);
            } else if (annotationType == AspectVariable.class) {
                AspectVariable aspectVariable = (AspectVariable) annotation;
                String aspectVariableName = aspectVariable.value();
                if (!aspectVariableName.isEmpty()) {
                    object = jbRequest.getAspectVariable(aspectVariableName);
                } else {
                    object = jbRequest.getAspectVariableByClassType(typeClazz);
                }
            }
        }


        //2.通过类型注入
        if (object == null) {
            if (RequestParser.class.isAssignableFrom(typeClazz)) {
                RequestParser m = (RequestParser) typeClazz.newInstance();
                m.doParse(jbRequest, request);
                object = m;
            } else if (typeClazz.equals(ServletRequest.class) || typeClazz.equals(HttpServletRequest.class)) {
                object = request;
            } else if (typeClazz.equals(ServletResponse.class) || typeClazz.equals(HttpServletResponse.class)) {
                object = response;
            } else if (typeClazz.equals(Request.class)) {
                object = jbRequest;
            }
        }

        return object;
    }


    private List getListParamValue(Request jettyBootReqParams, String paramName, Class elementType) {
        StringWrapper[] swArray = jettyBootReqParams.getRequestParams(paramName);
        if (swArray == null || swArray.length == 0) {
            return new ArrayList();
        }


        List<Object> objectList = new ArrayList<>();

        for (int i = 0; i < swArray.length; i++) {

            StringWrapper sw = swArray[i];

            Object value = CastTypeUtils.castValueType(sw, elementType);

            objectList.add(value);
        }


        return objectList;
    }


    private Object[] getArrayParamValue(Request jettyBootReqParams, String paramName, Class elementType) {
        List list = getListParamValue(jettyBootReqParams, paramName, elementType);
        return list.toArray();
    }

    private Set getSetParamValue(Request jettyBootReqParams, String paramName, Class elementType) {
        List list = getListParamValue(jettyBootReqParams, paramName, elementType);
        return new HashSet(list);
    }


    private Request getJBRequestInstance(HttpServletRequest request, String targetPath) {
        return Request.getInstance(request, targetPath);
    }


}