package cn.ubibi.jettyboot.demotest;

import cn.ubibi.jettyboot.demotest.controller.MyExceptionHandler;
import cn.ubibi.jettyboot.demotest.controller.UserController;
import cn.ubibi.jettyboot.demotest.servlets.HelloServlet;
import cn.ubibi.jettyboot.framework.rest.HttpServletWrapper;
import cn.ubibi.jettyboot.framework.rest.IExceptionHandler;
import cn.ubibi.jettyboot.framework.rest.RestControllerHandler;
import cn.ubibi.jettyboot.framework.rest.RestHandler;
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
}
