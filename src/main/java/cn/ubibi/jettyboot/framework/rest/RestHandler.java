package cn.ubibi.jettyboot.framework.rest;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class RestHandler extends AbstractHandler {

    private static Logger logger = Log.getLogger(RestHandler.class);


    private List<RestControllerHandler> restHandlers = new ArrayList<>();

    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        for (RestControllerHandler restHandler : restHandlers){
            try {
                if(restHandler.handle(request,response)){
                    baseRequest.setHandled(true);
                    return;
                }
            } catch (InstantiationException e) {
                logger.info(e);
            } catch (InvocationTargetException e) {
                logger.info(e);
            } catch (IllegalAccessException e) {
                logger.info(e);
            }
        }
    }




    public void addController(Class<?> clazz) throws Exception {
        if(clazz == null){
            throw new Exception("addController can not null");
        }
        restHandlers.add(new RestControllerHandler(clazz));
    }

    public void addController(Object restController) throws Exception {
        if(restController == null){
            throw new Exception("addController can not null");
        }
        restHandlers.add(new RestControllerHandler(restController));
    }
}
