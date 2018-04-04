package cn.ubibi.jettyboot.framework.rest;

import org.eclipse.jetty.server.handler.ContextHandler;

import javax.servlet.http.HttpServlet;

public class RestContextHandler extends ContextHandler{

    private RestHandler restHandler;

    public RestContextHandler(String context) {
        super(context);
        this.restHandler = new RestHandler();
        this.setHandler(this.restHandler);
    }


    public void addController(Class<?> clazz) throws Exception {
        this.restHandler.addController(clazz);
    }

    public void addController(Object restController) throws Exception {
        this.restHandler.addController(restController);
    }


    public void addController(String path,Class<?> clazz) throws Exception {
        this.restHandler.addController(path,clazz);
    }

    public void addController(String path ,Object restController) throws Exception {
        this.restHandler.addController(path,restController);
    }


    public void addExceptionHandler(IExceptionHandler exceptionHandler) throws Exception {
        this.restHandler.addExceptionHandler(exceptionHandler);
    }

    public void addServlet(String path, HttpServlet httpServlet) throws Exception {
        this.restHandler.addServlet(path,httpServlet);
    }

    public void addMethodAspect(IRestMethodAspect methodAspect) throws Exception {
        this.restHandler.addMethodAspect(methodAspect);
    }
}
