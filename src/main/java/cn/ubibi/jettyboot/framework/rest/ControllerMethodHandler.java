package cn.ubibi.jettyboot.framework.rest;

import cn.ubibi.jettyboot.framework.commons.*;
import cn.ubibi.jettyboot.framework.ioc.ServiceManager;
import cn.ubibi.jettyboot.framework.rest.annotation.*;
import cn.ubibi.jettyboot.framework.rest.ifs.*;
import cn.ubibi.jettyboot.framework.rest.impl.*;
import cn.ubibi.jettyboot.framework.rest.model.MethodArgument;
import cn.ubibi.jettyboot.framework.slot.SlotComponentManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.AsyncContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.*;

public class ControllerMethodHandler implements Comparable<ControllerMethodHandler> {

    private String targetPath;
    private String supportRequestMethod;
    private Method method;


    private Class<?> controllerClazz;
    private String controllerClazzSimpleName;


    ControllerMethodHandler(Class<?> controllerClazz, String methodPath, String supportRequestMethod, String classPath, Method method) {
        this.targetPath = pathJoin(classPath, methodPath);
        this.supportRequestMethod = supportRequestMethod;
        this.method = method;
        this.controllerClazz = controllerClazz;
        this.controllerClazzSimpleName = controllerClazz.getSimpleName();
    }


    public String getControllerClazzSimpleName() {
        return controllerClazzSimpleName;
    }

    public boolean isDWR() {
        return supportRequestMethod.startsWith("dwr_");
    }

    public boolean isDWR_JSON(){
        return "dwr_json".equals(supportRequestMethod);
    }


    //判断是否支持
    boolean isSupportRequest(HttpServletRequest request) {

        String supportRequestMethod = this.supportRequestMethod;

        //DWR使用的是POST请求
        if (isDWR()) {
            supportRequestMethod = "post";
        }

        if (supportRequestMethod.equalsIgnoreCase(request.getMethod())) {
            String requestPathInfo = request.getPathInfo();
            HttpPathComparator httpPathComparator = SlotComponentManager.getInstance().getHttpPathComparator();
            if (httpPathComparator.isMatch(targetPath, requestPathInfo)) {
                return true;
            }
        }


        return false;
    }


    //处理请求
    void doHandleRequest(Object controller, HttpServletRequest request, HttpServletResponse response) throws Exception {

        List<ControllerAspect> methodWrappers = SlotComponentManager.getInstance().getControllerAspects();

        HttpParsedRequest httpParsedRequest;
        Object invokeResult = null;
        try {

            //解析HTTP请求
            httpParsedRequest = createHttpParsedRequest(controller, method, request, targetPath);

            //Aspect前置
            for (ControllerAspect controllerAspect : methodWrappers) {
                ServiceManager.getInstance().injectDependency(controllerAspect);
                controllerAspect.beforeInvoke(method, httpParsedRequest);
            }

            //准备参数
            Object[] paramsObjects = getMethodParamsObjects(method, httpParsedRequest, response);


            //除AsyncMergeMethod注解以外的其他处理
            InvokeResultCallable invokeResultCallable = new InvokeResultCallable(method, paramsObjects, controller);

            AsyncMergeMethod unionMethodCall = method.getDeclaredAnnotation(AsyncMergeMethod.class);
            if (unionMethodCall != null) {

                String taskKey = AsyncContextTaskManager.toTaskKey(method, unionMethodCall, paramsObjects);

                AsyncContext asyncContext = request.startAsync(httpParsedRequest, response);
                asyncContext.setTimeout(unionMethodCall.timeout());
                asyncContext.addListener(new AsyncContextListener());

                AsyncResultCallback asyncResultCallback = new DefaultAsyncResultCallback(method);
                AsyncContextTaskManager.addTask(taskKey, asyncResultCallback, asyncContext, invokeResultCallable);

                invokeResult = new VoidResult();
            } else {
                invokeResult = invokeResultCallable.call();
            }


        } catch (Exception e) {
            throw e;
        }

        //2.执行Render
        if (invokeResult instanceof VoidResult) {
            //do nothing
        } else {
            ResultRenderMisc.renderAndAfterInvoke(invokeResult, method, httpParsedRequest, response);
        }
    }


    private HttpParsedRequest createHttpParsedRequest(Object controller, Method method, HttpServletRequest request, String targetPath) throws Exception {

        HttpParsedRequestFactory httpParsedRequestFactory = SlotComponentManager.getInstance().getHttpParsedRequestFactory();

        ServiceManager.getInstance().injectDependency(httpParsedRequestFactory);

        return httpParsedRequestFactory.createHttpParsedRequest(controller, method, request, targetPath);
    }


    private Object[] getMethodParamsObjects(Method method, HttpParsedRequest httpParsedRequest, HttpServletResponse response) throws Exception {

        boolean isDwrJSON = this.isDWR_JSON();
        //如果是DWR的方式，默认只支持json的序列化协议
        //如果用其他的序列化协议，可以实现MethodArgumentResolver
        String request_method = this.supportRequestMethod;

        JSONArray dwrArgJSONArray = null;
        if (isDwrJSON) {
            Charset requestBodyCharset = FrameworkConfig.getInstance().getRequestBodyCharset();
            String jsonString = httpParsedRequest.getRequestBodyAsString(requestBodyCharset);
            if (!StringUtils.isEmpty(jsonString)) {
                dwrArgJSONArray = JSON.parseArray(jsonString);
            }
        }


        int paramsCount = method.getParameterCount();
        Type[] paramsTypes = method.getGenericParameterTypes();
        Annotation[][] paramAnnotations = method.getParameterAnnotations();


        if (paramsCount == 0) {
            return new Object[]{};
        }


        List<Object> objects = new ArrayList<>();

        for (int argIndex = 0; argIndex < paramsCount; argIndex++) {

            Type type = paramsTypes[argIndex];
            Annotation[] annotations = paramAnnotations[argIndex];

            MethodArgument methodArgument = new MethodArgument(method, type, annotations, argIndex, dwrArgJSONArray,request_method);

            Object object = null;

            MethodArgumentResolver methodArgumentResolver = findMethodArgumentResolver(methodArgument);
            if (methodArgumentResolver != null) {
                object = methodArgumentResolver.resolveArgument(methodArgument, httpParsedRequest);
            } else {
                object = getMethodParamObjectByAnnotation(methodArgument, httpParsedRequest, response);
            }

            //使用DWR的参数来补充
            if (object == null && isDwrJSON) {
                object = getDwrMethodParamJSONObject(methodArgument, dwrArgJSONArray, argIndex);
            }


            objects.add(object);
        }


        return objects.toArray();
    }


    private Object getDwrMethodParamJSONObject(MethodArgument methodArgument, JSONArray requestBodyArray, int index) throws Exception {

        if (requestBodyArray == null) {
            return null;
        }


        Object obj;

        if (index >= requestBodyArray.size()) {
            obj = null;
        } else {
            //索引没有超出
            obj = requestBodyArray.get(index);
        }

        return CastJsonTypeUtils.jsonObjectToJavaObject(obj, methodArgument.getType());
    }


    private MethodArgumentResolver findMethodArgumentResolver(MethodArgument methodArgument) throws Exception {

        List<MethodArgumentResolver> methodArgumentResolvers = SlotComponentManager.getInstance().getMethodArgumentResolverList();

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
     * @param methodArgument    类型
     * @param httpParsedRequest 请求参数
     * @param response          响应
     * @return
     * @throws IOException
     */
    private Object getMethodParamObjectByAnnotation(MethodArgument methodArgument, HttpParsedRequest httpParsedRequest, HttpServletResponse response) throws Exception {


        Type type = methodArgument.getType();
        Class typeClazz = (Class) methodArgument.getRawType();
        Type[] actualTypeArguments = methodArgument.getActualTypeArguments();

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
                String defaultValue = requestParam.defaultValue();

                List<String> values = httpParsedRequest.getParameterValuesAsList(paramName);

                if (typeClazz.isArray()) {
                    Class elementType = typeClazz.getComponentType();
                    object = CastBasicTypeUtils.toBasicTypeArrayOf(values, elementType);
                } else if (Collection.class.isAssignableFrom(typeClazz)) {

                    Class elementType = null;
                    if (!CollectionUtils.isEmpty(actualTypeArguments)) {
                        elementType = (Class) actualTypeArguments[0];
                    }

                    if (Set.class.equals(typeClazz)) {
                        typeClazz = HashSet.class;
                    } else if (Collection.class.equals(typeClazz) || List.class.equals(typeClazz)) {
                        typeClazz = ArrayList.class;
                    }

                    object = CastBasicTypeUtils.toBasicTypeCollectionOf(values, typeClazz, elementType);

                } else {

                    String value = httpParsedRequest.getParameter(paramName);
                    if (value == null && !StringUtils.isEmpty(defaultValue)) {
                        value = defaultValue;
                    }

                    if (value == null && CastBasicTypeUtils.isBasicType(typeClazz)) {
                        object = CastBasicTypeUtils.toBasicTypeOf(0, typeClazz);
                    } else {
                        object = CastBasicTypeUtils.toBasicTypeOf(value, typeClazz);
                    }
                }

            } else if (annotationType == RequestParams.class) {

                object = httpParsedRequest.getParameterValuesAsObject(typeClazz);

            } else if (annotationType == RequestBody.class) {

                Charset charset = FrameworkConfig.getInstance().getRequestBodyCharset();
                String jsonString = httpParsedRequest.getRequestBodyAsString(charset);
                JSONObject jsonObject = JSON.parseObject(jsonString);
                object = CastJsonTypeUtils.jsonObjectToJavaObject(jsonObject, type);

            } else if (annotationType == PathVariable.class) {

                PathVariable requestPath = (PathVariable) annotation;
                String sw = httpParsedRequest.getPathVariable(requestPath.value());
                object = CastBasicTypeUtils.toBasicTypeOf(sw, typeClazz);

            } else if (annotationType == AspectVariable.class) {

                AspectVariable aspectVariable = (AspectVariable) annotation;
                String aspectVariableName = aspectVariable.value();
                if (!aspectVariableName.isEmpty()) {
                    object = httpParsedRequest.getAspectVariable(aspectVariableName);
                } else {
                    object = httpParsedRequest.getAspectVariable(typeClazz);
                }

            }
        }


        //2.通过类型注入
        if (object == null) {

            if (RequestParser.class.isAssignableFrom(typeClazz)) {
                RequestParser m = (RequestParser) typeClazz.newInstance();
                m.doParse(httpParsedRequest);
                object = m;

            } else if (ServletRequest.class.isAssignableFrom(typeClazz)) {
                object = httpParsedRequest;
            } else if (typeClazz.equals(ServletResponse.class) || typeClazz.equals(HttpServletResponse.class)) {
                object = response;
            }
        }

        return object;
    }


    private String pathJoin(String path1, String path2) {

        List<String> path1Arr = CollectionUtils.removeEmptyString(path1.split(Constants.PATH_SPLIT));
        List<String> path2Arr = CollectionUtils.removeEmptyString(path2.split(Constants.PATH_SPLIT));


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
