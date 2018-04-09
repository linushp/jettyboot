package cn.ubibi.jettyboot.framework.rest;

import cn.ubibi.jettyboot.framework.ioc.JBServiceManager;
import cn.ubibi.jettyboot.framework.rest.ifs.JBRequestAspect;
import cn.ubibi.jettyboot.framework.rest.ifs.JBExceptionHandler;
import org.eclipse.jetty.server.handler.ContextHandler;

import javax.servlet.http.HttpServlet;

public class JBContextHandler extends ContextHandler{

    private JBRequestHandler requestHandler;

    public JBContextHandler(String context) {
        super(context);
        this.requestHandler = new JBRequestHandler();
        this.setHandler(this.requestHandler);
    }


    public void addController(String path,Class<?> clazz) throws Exception {
        this.requestHandler.addController(path,clazz);
    }

    public void addController(String path ,Object restController) throws Exception {
        this.requestHandler.addController(path,restController);
    }


    public void addExceptionHandler(JBExceptionHandler exceptionHandler) throws Exception {
        this.requestHandler.addExceptionHandler(exceptionHandler);
    }

    public void addServlet(String path, HttpServlet httpServlet) throws Exception {
        this.requestHandler.addServlet(path,httpServlet);
    }

    public void addRequestAspect(JBRequestAspect methodAspect) throws Exception {
        this.requestHandler.addRequestAspect(methodAspect);
    }

    public void addService(Object service){
        JBServiceManager.getInstance().addService(service);
    }
}
