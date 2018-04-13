package cn.ubibi.jettyboot.framework.rest;

import cn.ubibi.jettyboot.framework.ioc.ServiceManager;
import cn.ubibi.jettyboot.framework.rest.ifs.MethodArgumentResolver;
import cn.ubibi.jettyboot.framework.rest.ifs.RequestAspect;
import cn.ubibi.jettyboot.framework.rest.ifs.ExceptionHandler;
import cn.ubibi.jettyboot.framework.rest.model.MethodArgument;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;

import javax.servlet.http.HttpServlet;
import java.util.List;

public class RestContextHandler extends ContextHandler{

    private RequestHandler requestHandler;

    private HandlerCollection handlerCollection;

    public RestContextHandler(String context) {
        super(context);
        this.requestHandler = new RequestHandler();
        this.handlerCollection = new HandlerCollection(this.requestHandler);
        this.setHandler(this.handlerCollection);
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


    public void addResourceHandler(ResourceHandler resourceHandler) {
        this.handlerCollection.addHandler(resourceHandler);
    }

    public void addHandler(Handler handler) {
        this.handlerCollection.addHandler(handler);
    }


    /**
     * 报漏给外界，为了用户扩展需要，比如用户自己实现一个DWR框架
     * @return
     */
    public List<ControllerMethodHandler> getControllerMethodHandlers(){
        return this.requestHandler.getControllerMethodHandlers();
    }

}
