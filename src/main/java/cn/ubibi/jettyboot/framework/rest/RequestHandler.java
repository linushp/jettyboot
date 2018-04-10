package cn.ubibi.jettyboot.framework.rest;

import cn.ubibi.jettyboot.framework.rest.ifs.RequestAspect;
import cn.ubibi.jettyboot.framework.rest.ifs.ExceptionHandler;
import cn.ubibi.jettyboot.framework.rest.impl.TextRender;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RequestHandler extends AbstractHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestHandler.class);

    private List<ControllerHandler> restHandlers = new ArrayList<>();
    private List<ExceptionHandler> exceptionHandlers = new ArrayList<>();
    private List<ServletWrapper> httpServletHandlers = new ArrayList<>();
    private List<RequestAspect> methodAspectList = new ArrayList<>();


    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {


        // 1. check Servlet Handler
        for (ServletWrapper httpServletWrapper : httpServletHandlers) {
            if (httpServletWrapper.matched(target)) {
                try {
                    httpServletWrapper.handle(request, response,methodAspectList);
                } catch (Exception e) {
                    handleException2(e,request,response);
                }finally {
                    baseRequest.setHandled(true);
                }
                return;
            }
        }




        //2. check rest controller handler
        for (ControllerHandler restHandler : restHandlers) {
            try {
                if (restHandler.handle(request, response, methodAspectList)) {
                    baseRequest.setHandled(true);
                    return;
                }
            } catch (Exception e) {
                handleException2(e,request,response);
                baseRequest.setHandled(true);
                return;
            }
        }


    }

    private void handleException2(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        boolean isHandled = handleException(e, request, response);
        if (!isHandled) {

            LOGGER.info("",e);

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
        for (ExceptionHandler exceptionHandler : exceptionHandlers) {
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
        restHandlers.add(new ControllerHandler(path, clazz));
    }

    public void addController(String path, Object restController) throws Exception {
        if (restController == null) {
            throw new Exception("addController can not null");
        }
        LOGGER.info("addController " + path + "  :  " + restController.getClass().getName());
        restHandlers.add(new ControllerHandler(path, restController));
    }


    public void addExceptionHandler(ExceptionHandler exceptionHandler) throws Exception {
        if (exceptionHandler == null) {
            throw new Exception("addExceptionHandler can not null");
        }
        LOGGER.info("addExceptionHandler " +  exceptionHandler.getClass().getName());
        exceptionHandlers.add(exceptionHandler);
    }

    public void addServlet(String path, HttpServlet httpServlet) throws Exception {
        if (httpServlet == null || path == null || path.isEmpty()) {
            throw new Exception("addExceptionHandler can not null");
        }

        LOGGER.info("addServlet " + path + "  :  " + httpServlet.getClass().getName());
        httpServletHandlers.add(new ServletWrapper(path, httpServlet));
    }

    public void addRequestAspect(RequestAspect methodAspect) throws Exception {
        if (methodAspect == null) {
            throw new Exception("addRequestAspect can not null");
        }
        LOGGER.info("addRequestAspect " + methodAspect.getClass().getName());
        methodAspectList.add(methodAspect);
    }
}
