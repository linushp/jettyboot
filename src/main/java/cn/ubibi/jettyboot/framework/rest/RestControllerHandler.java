package cn.ubibi.jettyboot.framework.rest;

import com.alibaba.fastjson.JSON;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RestControllerHandler {

    private static Logger logger = Log.getLogger(RestControllerHandler.class);


    private Class<?> restControllerClazz;
    private Object restController;

    public RestControllerHandler(Class<?> clazz) {
        this.restControllerClazz = clazz;
    }

    public RestControllerHandler(Object restController) {
        this.restController = restController;
    }


    private boolean handleMethodPath(String path1, String[] path2, String method, HttpServletRequest request, HttpServletResponse response, Method methodFunc) throws InvocationTargetException, IOException, InstantiationException, IllegalAccessException {
        if (method.equalsIgnoreCase(request.getMethod())) {

            for (String path22 : path2) {
                String path = pathJoin(path1, path22);
                String reqPath = request.getPathInfo();

                boolean isOK = isHandleMethodPath(path, reqPath);

                if (isOK) {
                    handleMethod(request, response, methodFunc, path);
                    return true;
                }
            }
        }

        return false;
    }


    private void handleMethod(HttpServletRequest request, HttpServletResponse response, Method method, String targetPath) throws IllegalAccessException, InstantiationException, InvocationTargetException, IOException {

        Type[] paramsTypes = method.getGenericParameterTypes();
        Object[] paramsObjects = getParamsObjects(paramsTypes, request, response, targetPath);


        Object controller = this.restController;
        if (controller == null) {
            controller = restControllerClazz.newInstance();
        }


        Object invokeResult = null;
        try {
            invokeResult = method.invoke(controller, paramsObjects);
        }
        catch (Throwable e){
            logger.info(e);
//            e.printStackTrace();

            response.setContentType("text/html;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter writer = response.getWriter();
            writer.println(e);
            writer.println(e.getCause());
            writer.println(e.getMessage());

            writer.flush();
            writer.close();
            response.flushBuffer();

            return;
        }


        if (invokeResult instanceof String) {
            response.setContentType("text/html;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter writer = response.getWriter();
            writer.println(invokeResult);

            writer.flush();
            writer.close();
            response.flushBuffer();
        } else if (invokeResult !=null){

            response.setContentType("text/html;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter writer = response.getWriter();
            if (invokeResult != null) {
                writer.println(JSON.toJSONString(invokeResult));
            } else {
                writer.println("null");
            }
            writer.flush();
            writer.close();
            response.flushBuffer();
        }
    }


    private boolean isHandleMethodPath(String configPath, String reqPath) {
        if (configPath.equals(reqPath)) {
            return true;
        }

        //   /user/:id
        //   /user/23332
        String[] path1Array = configPath.split("/");
        String[] path2Array = reqPath.split("/");
        if (path1Array.length != path2Array.length) {
            return false;
        }

        for (int i = 0; i < path1Array.length; i++) {
            String pp1 = path1Array[i];
            String pp2 = path2Array[i];
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

        //    "/user"  "/sdds"
        //    "user"  "/sdds"

        if (!path1.startsWith("/")) {
            path1 = "/" + path1;
        }

        if (!path2.startsWith("/") && !path1.endsWith("/")) {
            path2 = "/" + path2;
        }

        String path = path1 + path2;

        return path;
    }


    public boolean handle(HttpServletRequest request, HttpServletResponse response) throws InstantiationException, InvocationTargetException, IllegalAccessException, IOException {


        Class<?> clazz = this.restControllerClazz;
        if (clazz == null) {
            clazz = this.restController.getClass();
        }

        RestMapping classRestMappings = clazz.getAnnotation(RestMapping.class);
        if (classRestMappings.path() != null) {
            String[] classPathList = classRestMappings.path();
            for (String classPath : classPathList) {

                Method[] methods = clazz.getMethods();

                if (methods != null) {
                    for (Method method : methods) {


                        RestGetMapping methodAnnotation1 = method.getAnnotation(RestGetMapping.class);
                        RestPostMapping methodAnnotation2 = method.getAnnotation(RestPostMapping.class);
                        RestMapping methodAnnotation3 = method.getAnnotation(RestMapping.class);
                        boolean handleResult = false;
                        if (methodAnnotation1 != null) {
                            handleResult = handleMethodPath(classPath, methodAnnotation1.path(), methodAnnotation1.method(), request, response, method);
                        } else if (methodAnnotation2 != null) {
                            handleResult = handleMethodPath(classPath, methodAnnotation2.path(), methodAnnotation2.method(), request, response, method);
                        } else if (methodAnnotation3 != null) {
                            handleResult = handleMethodPath(classPath, methodAnnotation3.path(), methodAnnotation3.method(), request, response, method);
                        }


                        if (handleResult) {
                            return true;
                        }

                    }
                }

            }
        }


        return false;
    }

    private Object[] getParamsObjects(Type[] paramsTypes, HttpServletRequest request, HttpServletResponse response, String targetPath) {

        List<Object> objects = new ArrayList<Object>();

        for (Type type : paramsTypes) {
            if (type.equals(ServletRequest.class) || type.equals(HttpServletRequest.class)) {
                objects.add(request);
            } else if (type.equals(ServletResponse.class) || type.equals(HttpServletResponse.class)) {
                objects.add(response);
            } else if (type.equals(RestParams.class)) {
                objects.add(new RestParams(request, targetPath));
            } else {
                objects.add(null);
            }
        }
        return objects.toArray();
    }
}
