package cn.ubibi.jettyboot.framework.rest;

import cn.ubibi.jettyboot.framework.commons.CastTypeUtils;
import cn.ubibi.jettyboot.framework.commons.CollectionUtils;
import cn.ubibi.jettyboot.framework.commons.StringUtils;
import cn.ubibi.jettyboot.framework.commons.StringWrapper;
import cn.ubibi.jettyboot.framework.rest.annotation.*;
import cn.ubibi.jettyboot.framework.rest.ifs.MethodArgumentResolver;
import cn.ubibi.jettyboot.framework.rest.ifs.ControllerInterceptor;
import cn.ubibi.jettyboot.framework.rest.ifs.RequestParser;
import cn.ubibi.jettyboot.framework.rest.ifs.ResponseRender;
import cn.ubibi.jettyboot.framework.rest.impl.JsonRender;
import cn.ubibi.jettyboot.framework.rest.impl.TextRender;
import cn.ubibi.jettyboot.framework.rest.model.MethodArgument;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

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

public class ControllerMethodHandler implements Comparable<ControllerMethodHandler> {

    private String targetPath;
    private String supportRequestMethod;
    private Method method;

    private List<MethodArgumentResolver> methodArgumentResolvers;
    private List<ControllerInterceptor> requestAspectList;


    ControllerMethodHandler(String methodPath, String supportRequestMethod, String classPath, Method method, List<ControllerInterceptor> methodAspectList, List<MethodArgumentResolver> methodArgumentResolvers) {
        this.targetPath = pathJoin(classPath, methodPath);
        this.supportRequestMethod = supportRequestMethod;
        this.method = method;
        this.methodArgumentResolvers = methodArgumentResolvers;
        this.requestAspectList = methodAspectList;
    }


    //判断是否支持
    boolean isSupportRequest(HttpServletRequest request) {

        String supportRequestMethod = this.supportRequestMethod;

        //DWR使用的是POST请求
        if ("dwr".equals(supportRequestMethod)) {
            supportRequestMethod = "post";
        }


        if (supportRequestMethod.equalsIgnoreCase(request.getMethod())) {
            String reqPath = request.getPathInfo();
            return isHandleMethodPath(targetPath, reqPath);
        }

        return false;
    }


    //处理请求
    void doHandleRequest(Object controller, HttpServletRequest request, HttpServletResponse response) throws Exception {

        Object invokeResult;
        try {

            List<ControllerInterceptor> methodWrappers = this.requestAspectList;

            ControllerRequest jbRequest = ControllerRequest.getInstance(request, response, targetPath);

            //Aspect前置
            for (ControllerInterceptor methodWrapper : methodWrappers) {
                methodWrapper.invokeBefore(method, jbRequest);
            }


            //准备参数
            Object[] paramsObjects = getMethodParamsObjects(method, jbRequest, request, response);

            //方法调用
            invokeResult = method.invoke(controller, paramsObjects);


            //Aspect后置
            for (ControllerInterceptor methodWrapper : methodWrappers) {
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


    private Object[] getMethodParamsObjects(Method method, ControllerRequest jbRequest, HttpServletRequest request, HttpServletResponse response) throws Exception {

        boolean isDWR = "dwr".equals(supportRequestMethod);
        JSONArray dwrArgArray = null;
        if (isDWR) {
            dwrArgArray = jbRequest.getRequestBodyArray();
        }


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

            //使用DWR的参数来补充
            if (object == null && isDWR) {
                object = getDwrMethodParamObject(methodArgument, dwrArgArray, i);
            }


            objects.add(object);
        }


        return objects.toArray();
    }


    private Object getDwrMethodParamObject(MethodArgument methodArgument, JSONArray requestBodyArray, int index) {

        if (requestBodyArray == null) {
            return null;
        }


        Object obj = requestBodyArray.get(index);
        if (obj == null) {
            return null;
        }


        Class typeClazz = (Class) methodArgument.getType();

        if (typeClazz.equals(Object.class)){
            return obj;
        }

        if (typeClazz.equals(String.class)){
            return obj.toString();
        }


        if (obj instanceof JSONObject) {

            if (typeClazz.equals(JSONObject.class)){//无需转换
                return obj;
            }

            JSONObject jsonObject = (JSONObject) obj;
            return jsonObject.toJavaObject(typeClazz);
        }

        if (obj instanceof JSONArray) {
            return obj;
        }

        return CastTypeUtils.castValueType(obj, typeClazz);
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
     * @param methodArgument 类型
     * @param jbRequest      请求参数
     * @param request        请求对象
     * @param response       响应
     * @return
     * @throws IOException
     */
    private Object getMethodParamsObject(MethodArgument methodArgument, ControllerRequest jbRequest, HttpServletRequest request, HttpServletResponse response) throws Exception {


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
                String sw = jbRequest.getPathVariable(requestPath.value());
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
            } else if (typeClazz.equals(ControllerRequest.class)) {
                object = jbRequest;
            }
        }

        return object;
    }


    private List getListParamValue(ControllerRequest jettyBootReqParams, String paramName, Class elementType) {
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


    private Object[] getArrayParamValue(ControllerRequest jettyBootReqParams, String paramName, Class elementType) {
        List list = getListParamValue(jettyBootReqParams, paramName, elementType);
        return list.toArray();
    }

    private Set getSetParamValue(ControllerRequest jettyBootReqParams, String paramName, Class elementType) {
        List list = getListParamValue(jettyBootReqParams, paramName, elementType);
        return new HashSet(list);
    }


    private boolean isHandleMethodPath(String targetPath, String reqPath) {
        if (targetPath.equals(reqPath)) {
            return true;
        }

        //   /user/abc
        //   /user/:id
        //   /user/23332
        List<String> path1Array = CollectionUtils.removeEmptyString(targetPath.split("/"));
        List<String> path2Array = CollectionUtils.removeEmptyString(reqPath.split("/"));
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

        List<String> path1Arr = CollectionUtils.removeEmptyString(path1.split("/"));
        List<String> path2Arr = CollectionUtils.removeEmptyString(path2.split("/"));


        List<String> pathList = new ArrayList<>();

        pathList.addAll(path1Arr);
        pathList.addAll(path2Arr);

        String result = "/" + StringUtils.join(pathList, "/");
        return result;
    }


    @Override
    public int compareTo(ControllerMethodHandler o) {
        return this.targetPath.compareTo(o.targetPath);
    }

    @Override
    public String toString() {
        return this.targetPath;
    }


    public String getTargetPath() {
        return targetPath;
    }

    public String getSupportRequestMethod() {
        return supportRequestMethod;
    }

    public Method getMethod() {
        return method;
    }
}
