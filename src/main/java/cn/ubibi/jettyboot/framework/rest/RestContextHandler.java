package cn.ubibi.jettyboot.framework.rest;

import cn.ubibi.jettyboot.framework.ioc.ServiceManager;
import cn.ubibi.jettyboot.framework.rest.ifs.MethodArgumentResolver;
import cn.ubibi.jettyboot.framework.rest.ifs.RequestAspect;
import cn.ubibi.jettyboot.framework.rest.ifs.ExceptionHandler;
import cn.ubibi.jettyboot.framework.rest.model.MethodArgument;
import org.eclipse.jetty.server.handler.ContextHandler;

import javax.servlet.http.HttpServlet;

public class RestContextHandler extends ContextHandler{

    private RequestHandler requestHandler;

    public RestContextHandler(String context) {
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

    public void addMethodArgumentResolver(MethodArgumentResolver argumentResolver) throws Exception {
        this.requestHandler.addMethodArgumentResolver(argumentResolver);
    }


    public void addService(Object service){
        ServiceManager.getInstance().addService(service);
    }
}
