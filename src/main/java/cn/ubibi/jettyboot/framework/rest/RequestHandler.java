package cn.ubibi.jettyboot.framework.rest;

import cn.ubibi.jettyboot.framework.rest.ifs.MethodArgumentResolver;
import cn.ubibi.jettyboot.framework.rest.ifs.ControllerAspect;
import cn.ubibi.jettyboot.framework.rest.ifs.ControllerExceptionHandler;
import cn.ubibi.jettyboot.framework.rest.impl.TextRender;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RequestHandler extends AbstractHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestHandler.class);

    private final List<ControllerHandler> controllerHandlers = new ArrayList<>();
    private final List<ControllerExceptionHandler> exceptionHandlers = new ArrayList<>();
    private final List<ControllerAspect> methodAspectList = new ArrayList<>();
    private final List<MethodArgumentResolver> methodArgumentResolvers = new ArrayList<>();


    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {


        // check rest controller handler
        for (ControllerHandler restHandler : controllerHandlers) {
            try {
                if (restHandler.handle(request, response)) {
                    baseRequest.setHandled(true);
                    return;
                }
            } catch (Exception e) {
                handleException2(e, request, response);
                baseRequest.setHandled(true);
                return;
            }
        }


    }

    private void handleException2(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        boolean isHandled = handleException(e, request, response);
        if (!isHandled) {

            LOGGER.info("", e);

            //如果异常没有被处理
            if (e instanceof IOException) {
                throw (IOException) e;
            } else if (e instanceof ServletException) {
                throw (ServletException) e;
            } else {
                String nextLine = "    \n   ";
                String exMsg = e.toString() + nextLine + e.getMessage() + nextLine + e.getCause();
                new TextRender(exMsg).doRender(request, response);
            }
        }
    }


    private boolean handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        for (ControllerExceptionHandler exceptionHandler : exceptionHandlers) {
            boolean isHandled = exceptionHandler.handle(e, request, response);
            if (isHandled) {
                return true;
            }
        }
        return false;
    }


    public void addController(String path, Class<?> clazz) throws Exception {
        if (clazz == null) {
            throw new Exception("addController can not null");
        }
        LOGGER.info("addController " + path + "  :  " + clazz.getName());
        controllerHandlers.add(new ControllerHandler(path, clazz, methodAspectList, methodArgumentResolvers));
    }

    public void addController(String path, Object restController) throws Exception {
        if (restController == null) {
            throw new Exception("addController can not null");
        }
        LOGGER.info("addController " + path + "  :  " + restController.getClass().getName());
        controllerHandlers.add(new ControllerHandler(path, restController, methodAspectList, methodArgumentResolvers));
    }


    public void addExceptionHandler(ControllerExceptionHandler exceptionHandler) throws Exception {
        if (exceptionHandler == null) {
            throw new Exception("addExceptionHandler can not null");
        }
        LOGGER.info("addExceptionHandler " + exceptionHandler.getClass().getName());
        exceptionHandlers.add(exceptionHandler);
    }


    public void addRequestAspect(ControllerAspect methodAspect) throws Exception {
        if (methodAspect == null) {
            throw new Exception("addRequestAspect can not null");
        }
        LOGGER.info("addRequestAspect " + methodAspect.getClass().getName());
        methodAspectList.add(methodAspect);
    }


    public void addMethodArgumentResolver(MethodArgumentResolver methodArgumentResolver) throws Exception {
        if (methodArgumentResolver == null) {
            throw new Exception("addMethodArgumentResolver can not null");
        }
        LOGGER.info("addMethodArgumentResolver " + methodArgumentResolver.getClass().getName());
        methodArgumentResolvers.add(methodArgumentResolver);
    }


    public List<ControllerMethodHandler> getControllerMethodHandlers() {
        List<ControllerMethodHandler> result = new ArrayList<>();

        for (ControllerHandler controllerHandler : controllerHandlers) {
            List<ControllerMethodHandler> methods = controllerHandler.getControllerMethodList();
            if (methods != null && !methods.isEmpty()) {
                result.addAll(methods);
            }
        }

        return result;

    }

}
