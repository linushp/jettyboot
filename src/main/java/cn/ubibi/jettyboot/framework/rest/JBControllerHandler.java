package cn.ubibi.jettyboot.framework.rest;


import cn.ubibi.jettyboot.framework.commons.*;
import cn.ubibi.jettyboot.framework.ioc.JBServiceManager;
import cn.ubibi.jettyboot.framework.rest.annotation.*;
import cn.ubibi.jettyboot.framework.rest.ifs.JBRequestAspect;
import cn.ubibi.jettyboot.framework.rest.ifs.JBResponseRender;
import cn.ubibi.jettyboot.framework.rest.ifs.JBRequestParser;
import cn.ubibi.jettyboot.framework.rest.impl.JBTextRender;
import com.alibaba.fastjson.JSON;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JBControllerHandler {

    private static Logger logger = Log.getLogger(JBControllerHandler.class);


    private Class<?> restControllerClazz;
    private Object restController;
    private String path;

    public JBControllerHandler(String path, Class<?> clazz) {
        this.restControllerClazz = clazz;
        this.path = path;
    }

    public JBControllerHandler(String path, Object restController) {
        this.restController = restController;
        this.path = path;
    }


    public boolean handle(HttpServletRequest request, HttpServletResponse response, List<JBRequestAspect> methodWrappers) throws Exception {

        String reqPath = request.getPathInfo();

        Class<?> clazz = this.getControllerClass();
        String[] classPathList = this.getClassPaths();

        for (String classPath : classPathList) {

            if (isMatchClassPath(reqPath, classPath)) {

                Method[] methods = clazz.getMethods();
                if (methods != null) {
                    for (Method method : methods) {

                        JBGetMapping methodAnnotation1 = method.getAnnotation(JBGetMapping.class);
                        JBPostMapping methodAnnotation2 = method.getAnnotation(JBPostMapping.class);
                        boolean handleResult = false;
                        if (methodAnnotation1 != null) {
                            handleResult = handleMethodPath(classPath, methodAnnotation1.path(), methodAnnotation1.method(), request, response, method, methodWrappers);
                        } else if (methodAnnotation2 != null) {
                            handleResult = handleMethodPath(classPath, methodAnnotation2.path(), methodAnnotation2.method(), request, response, method, methodWrappers);
                        }

                        if (handleResult) {
                            return true;
                        }
                    }
                }
            }
        }


        //路径不匹配
        return false;
    }


    private Class<?> getControllerClass() {
        Class<?> clazz = this.restControllerClazz;
        if (clazz == null) {
            clazz = this.restController.getClass();
        }
        return clazz;
    }

    private Object getRestController() throws Exception {
        Object controller = this.restController;
        if (controller == null) {
            controller = restControllerClazz.newInstance();
        }

        controller = JBServiceManager.getInstance().injectDependency(controller);
        return controller;
    }


    private String[] getClassPaths() {
        if (this.path != null && !this.path.isEmpty()) {
            return new String[]{this.path};
        }
        return new String[]{"/"};
    }


    private boolean handleMethodPath(String classPath, String[] path2, String method, HttpServletRequest request, HttpServletResponse response, Method methodFunc, List<JBRequestAspect> methodWrappers) throws Exception {
        if (method.equalsIgnoreCase(request.getMethod())) {

            String reqPath = request.getPathInfo();

            for (String path22 : path2) {
                String path = pathJoin(classPath, path22);
                boolean isMethodMatchOK = isHandleMethodPath(path, reqPath);
                if (isMethodMatchOK) {
                    handleMethod(request, response, methodFunc, path, methodWrappers);
                    return true;
                }
            }
        }

        return false;
    }


    private void handleMethod(HttpServletRequest request, HttpServletResponse response, Method method, String targetPath, List<JBRequestAspect> methodWrappers) throws Exception {

        Object controller = this.getRestController();

        Object invokeResult;
        try {


            int paramsCount = method.getParameterCount();
            Type[] paramsTypes = method.getGenericParameterTypes();
            Annotation[][] paramAnnotations = method.getParameterAnnotations();
            Object[] paramsObjects = getParamsObjects(paramsCount, paramsTypes, paramAnnotations, request, response, targetPath);


            JBRequest jbRequest = getJBRequestInstance(request, targetPath);


            for (JBRequestAspect methodWrapper : methodWrappers) {
                methodWrapper.invokeBefore(method, jbRequest);
            }

            invokeResult = method.invoke(controller, paramsObjects);

            for (JBRequestAspect methodWrapper : methodWrappers) {
                methodWrapper.invokeAfter(method, jbRequest, invokeResult);
            }


        } catch (Exception e) {
            throw e;
        }

        if (invokeResult instanceof JBResponseRender) {
            ((JBResponseRender) invokeResult).doRender(request, response);
        } else if (invokeResult instanceof String) {
            new JBTextRender(invokeResult.toString()).doRender(request, response);
        } else {
            String jsonString = JSON.toJSONString(invokeResult);
            new JBTextRender(jsonString).doRender(request, response);
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


    private Object[] getParamsObjects(int paramsCount, Type[] paramsTypes, Annotation[][] paramAnnotations, HttpServletRequest request, HttpServletResponse response, String targetPath) throws Exception {

        if (paramsCount == 0) {
            return new Object[]{};
        }


        //同一个请求中只有一个ReqParams实例
        JBRequest jettyBootReqParams = getJBRequestInstance(request, targetPath);

        List<Object> objects = new ArrayList<>();


        for (int i = 0; i < paramsCount; i++) {

            Type type = paramsTypes[i];
            Class typeClazz = (Class) type;

            Annotation[] annotations = paramAnnotations[i];

            Annotation annotation = null;
            if (annotations != null && annotations.length > 0) {
                annotation = annotations[0];
            }


            Object object = null;


            //1.通过注解注入
            if (annotation != null) {

                Class<? extends Annotation> annotationType = annotation.annotationType();
                if (annotationType == JBRequestParam.class) {

                    JBRequestParam requestParam = (JBRequestParam) annotation;
                    String paramName = requestParam.name();
                    Class elementType = requestParam.elementType();
                    if (typeClazz.isArray()) {
                        elementType = typeClazz.getComponentType();
                        object = getArrayParamValue(jettyBootReqParams, paramName, elementType);
                    } else if (List.class.isAssignableFrom(typeClazz)) {
                        object = getListParamValue(jettyBootReqParams, paramName, elementType);
                    } else if (Set.class.isAssignableFrom(typeClazz)) {
                        object = getSetParamValue(jettyBootReqParams, paramName, elementType);
                    } else {
                        StringWrapper sw = jettyBootReqParams.getRequestParam(paramName, requestParam.defaultValue());
                        object = CastTypeUtils.castValueType(sw, typeClazz);
                    }

                } else if (annotationType == JBRequestParams.class) {
                    object = jettyBootReqParams.getRequestParamObject(typeClazz);
                } else if (annotationType == JBRequestBody.class) {
                    object = jettyBootReqParams.getRequestBodyObject(typeClazz);
                } else if (annotationType == JBRequestPath.class) {
                    JBRequestPath requestPath = (JBRequestPath) annotation;
                    String sw = jettyBootReqParams.getPathVariable(requestPath.name());
                    object = CastTypeUtils.castValueType(sw, typeClazz);
                }
            }


            //2.通过类型注入
            if (object == null) {
                if (JBRequestParser.class.isAssignableFrom(typeClazz)) {
                    JBRequestParser m = (JBRequestParser) typeClazz.newInstance();
                    m.doParse(jettyBootReqParams, request, targetPath);
                    object = m;
                } else if (type.equals(ServletRequest.class) || type.equals(HttpServletRequest.class)) {
                    object = request;
                } else if (type.equals(ServletResponse.class) || type.equals(HttpServletResponse.class)) {
                    object = response;
                } else if (type.equals(JBRequest.class)) {
                    object = jettyBootReqParams;
                }
            }


            objects.add(object);

        }

        return objects.toArray();
    }


    private List getListParamValue(JBRequest jettyBootReqParams, String paramName, Class elementType) {
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


    private Object[] getArrayParamValue(JBRequest jettyBootReqParams, String paramName, Class elementType) {
        List list = getListParamValue(jettyBootReqParams, paramName, elementType);
        return list.toArray();
    }

    private Set getSetParamValue(JBRequest jettyBootReqParams, String paramName, Class elementType) {
        List list = getListParamValue(jettyBootReqParams, paramName, elementType);
        return new HashSet(list);
    }


    private JBRequest getJBRequestInstance(HttpServletRequest request, String targetPath) {
        return JBRequest.getInstance(request, targetPath);
    }


}
