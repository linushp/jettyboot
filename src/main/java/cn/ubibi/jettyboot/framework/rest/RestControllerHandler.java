package cn.ubibi.jettyboot.framework.rest;

import cn.ubibi.jettyboot.framework.commons.BeanUtils;
import cn.ubibi.jettyboot.framework.commons.CollectionUtils;
import cn.ubibi.jettyboot.framework.commons.StringUtils;
import cn.ubibi.jettyboot.framework.ioc.ServiceManager;
import cn.ubibi.jettyboot.framework.rest.impl.RestTextRender;
import com.alibaba.fastjson.JSON;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RestControllerHandler {

    private static Logger logger = Log.getLogger(RestControllerHandler.class);


    private Class<?> restControllerClazz;
    private Object restController;
    private String path;

    public RestControllerHandler(String path, Class<?> clazz) {
        this.restControllerClazz = clazz;
        this.path = path;
    }

    public RestControllerHandler(String path, Object restController) {
        this.restController = restController;
        this.path = path;
    }


    public boolean handle(HttpServletRequest request, HttpServletResponse response, List<IRestMethodAspect> methodWrappers) throws Exception {

        String reqPath = request.getPathInfo();

        Class<?> clazz = this.getControllerClass();
        String[] classPathList = this.getClassPaths();

        for (String classPath : classPathList) {

            if (isMatchClassPath(reqPath, classPath)) {

                Method[] methods = clazz.getMethods();
                if (methods != null) {
                    for (Method method : methods) {

                        RestGetMapping methodAnnotation1 = method.getAnnotation(RestGetMapping.class);
                        RestPostMapping methodAnnotation2 = method.getAnnotation(RestPostMapping.class);
                        RestMapping methodAnnotation3 = method.getAnnotation(RestMapping.class);
                        boolean handleResult = false;
                        if (methodAnnotation1 != null) {
                            handleResult = handleMethodPath(classPath, methodAnnotation1.path(), methodAnnotation1.method(), request, response, method, methodWrappers);
                        } else if (methodAnnotation2 != null) {
                            handleResult = handleMethodPath(classPath, methodAnnotation2.path(), methodAnnotation2.method(), request, response, method, methodWrappers);
                        } else if (methodAnnotation3 != null) {
                            handleResult = handleMethodPath(classPath, methodAnnotation3.path(), methodAnnotation3.method(), request, response, method, methodWrappers);
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

        controller = ServiceManager.getInstance().injectDependency(controller);
        return controller;
    }




    private String[] getClassPaths() {
        if (this.path != null && !this.path.isEmpty()) {
            return new String[]{this.path};
        }
        Class<?> clazz = this.getControllerClass();
        RestMapping classRestMappings = clazz.getAnnotation(RestMapping.class);
        if (classRestMappings != null && classRestMappings.path() != null) {
            return classRestMappings.path();
        }
        return new String[]{"/"};
    }


    private boolean handleMethodPath(String classPath, String[] path2, String method, HttpServletRequest request, HttpServletResponse response, Method methodFunc, List<IRestMethodAspect> methodWrappers) throws Exception {
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


    private void handleMethod(HttpServletRequest request, HttpServletResponse response, Method method, String targetPath, List<IRestMethodAspect> methodWrappers) throws Exception {

        Object controller = this.getRestController();

        Object invokeResult;
        try {
            Type[] paramsTypes = method.getGenericParameterTypes();
            Object[] paramsObjects = getParamsObjects(paramsTypes, request, response, targetPath);


            ReqParams reqParams = getRestParams(request, targetPath);

            for (IRestMethodAspect methodWrapper : methodWrappers) {
                methodWrapper.invokeBefore(method, reqParams);
            }

            invokeResult = method.invoke(controller, paramsObjects);


            for (IRestMethodAspect methodWrapper : methodWrappers) {
                methodWrapper.invokeAfter(method, reqParams, invokeResult);
            }


        } catch (Exception e) {
            throw e;
        }

        if (invokeResult instanceof IRestRender) {
            ((IRestRender) invokeResult).doRender(request, response);
        } else if (invokeResult instanceof String) {
            new RestTextRender(invokeResult.toString()).doRender(request, response);
        } else {
            String jsonString = JSON.toJSONString(invokeResult);
            new RestTextRender(jsonString).doRender(request, response);
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


    private Object[] getParamsObjects(Type[] paramsTypes, HttpServletRequest request, HttpServletResponse response, String targetPath) throws Exception {

        //同一个请求中只有一个ReqParams实例
        ReqParams jettyBootReqParams = getRestParams(request, targetPath);

        List<Object> objects = new ArrayList<Object>();

        for (Type type : paramsTypes) {


            if (IReqBodyParser.class.isAssignableFrom((Class<?>) type)) {
                Object obj = ((Class<?>) type).newInstance();

                try {
                    BeanUtils.copyField(obj, jettyBootReqParams.getRequestBodyObject(obj.getClass()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                objects.add(obj);

            } else if (IReqParamParser.class.isAssignableFrom((Class<?>) type)) {

                Object obj = ((Class<?>) type).newInstance();
                BeanUtils.copyField(obj, jettyBootReqParams.getRequestParamObject(obj.getClass()));
                objects.add(obj);

            } else if (IReqParser.class.isAssignableFrom((Class<?>) type)) {

                Class clazz = (Class) type;
                IReqParser m = (IReqParser) clazz.newInstance();

                m.doParse(jettyBootReqParams, request, targetPath);

                objects.add(m);

            } else if (type.equals(ServletRequest.class) || type.equals(HttpServletRequest.class)) {
                objects.add(request);
            } else if (type.equals(ServletResponse.class) || type.equals(HttpServletResponse.class)) {
                objects.add(response);
            } else if (type.equals(ReqParams.class)) {
                objects.add(jettyBootReqParams);
            } else {
                objects.add(null);
            }
        }

        return objects.toArray();
    }


    private ReqParams getRestParams(HttpServletRequest request, String targetPath) {
        ReqParams jettyBootReqParams = (ReqParams) request.getAttribute("jettyBootReqParams");
        if (jettyBootReqParams == null) {
            jettyBootReqParams = new ReqParams(request, targetPath);
            request.setAttribute("jettyBootReqParams", jettyBootReqParams);
        }
        return jettyBootReqParams;
    }
}
