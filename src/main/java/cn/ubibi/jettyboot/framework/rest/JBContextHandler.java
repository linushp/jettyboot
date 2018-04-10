package cn.ubibi.jettyboot.framework.rest;

import cn.ubibi.jettyboot.framework.ioc.ServiceManager;
import cn.ubibi.jettyboot.framework.rest.ifs.RequestAspect;
import cn.ubibi.jettyboot.framework.rest.ifs.ExceptionHandler;
import org.eclipse.jetty.server.handler.ContextHandler;

import javax.servlet.http.HttpServlet;

public class JBContextHandler extends ContextHandler{

    private RequestHandler requestHandler;

    public JBContextHandler(String context) {
        super(context);
        this.requestHandler = new RequestHandler();
        this.setHandler(this.requestHandler);
    }


    public void addController(String path,Class<?> clazz) throws Exception {
        this.requestHandler.addController(path,clazz);
    }

    public void addController(String path ,Object restController) throws Exception {
        this.requestHandler.addController(path,restController);
    }

    public void addExceptionHandler(ExceptionHandler exceptionHandler) throws Exception {
        this.requestHandler.addExceptionHandler(exceptionHandler);
    }

    public void addServlet(String path, HttpServlet httpServlet) throws Exception {
        this.requestHandler.addServlet(path,httpServlet);
    }

    public void addRequestAspect(RequestAspect methodAspect) throws Exception {
        this.requestHandler.addRequestAspect(methodAspect);
    }

    public void addService(Object service){
        ServiceManager.getInstance().addService(service);
    }
}
