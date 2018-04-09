package cn.ubibi.jettyboot.framework.rest;

import cn.ubibi.jettyboot.framework.rest.ifs.JBRequestAspect;
import cn.ubibi.jettyboot.framework.rest.ifs.JBExceptionHandler;
import cn.ubibi.jettyboot.framework.rest.impl.JBTextRender;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JBRequestHandler extends AbstractHandler {

    private static Logger logger = Log.getLogger(JBRequestHandler.class);


    private List<JBControllerHandler> restHandlers = new ArrayList<>();
    private List<JBExceptionHandler> exceptionHandlers = new ArrayList<>();
    private List<JBServletWrapper> httpServletHandlers = new ArrayList<>();
    private List<JBRequestAspect> methodAspectList = new ArrayList<>();


    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {


        // 1. check Servlet Handler
        for (JBServletWrapper httpServletWrapper : httpServletHandlers) {
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
        for (JBControllerHandler restHandler : restHandlers) {
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

            logger.info(e);

            //如果异常没有被处理
            if (e instanceof IOException) {
                throw (IOException) e;
            } else if (e instanceof ServletException) {
                throw (ServletException) e;
            } else {
                String nextLine = "    \n   ";
                String exMsg = e.toString() + nextLine + e.getMessage() + nextLine + e.getCause();
                new JBTextRender(exMsg).doRender(request, response);
            }
        }
    }


    private boolean handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        for (JBExceptionHandler exceptionHandler : exceptionHandlers) {
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
        restHandlers.add(new JBControllerHandler(path, clazz));
    }

    public void addController(String path, Object restController) throws Exception {
        if (restController == null) {
            throw new Exception("addController can not null");
        }
        restHandlers.add(new JBControllerHandler(path, restController));
    }


    public void addExceptionHandler(JBExceptionHandler exceptionHandler) throws Exception {
        if (exceptionHandler == null) {
            throw new Exception("addExceptionHandler can not null");
        }
        exceptionHandlers.add(exceptionHandler);
    }

    public void addServlet(String path, HttpServlet httpServlet) throws Exception {
        if (httpServlet == null || path == null || path.isEmpty()) {
            throw new Exception("addExceptionHandler can not null");
        }
        httpServletHandlers.add(new JBServletWrapper(path, httpServlet));
    }

    public void addRequestAspect(JBRequestAspect methodAspect) throws Exception {
        if (methodAspect == null) {
            throw new Exception("addMethodWrapper can not null");
        }
        methodAspectList.add(methodAspect);
    }
}
