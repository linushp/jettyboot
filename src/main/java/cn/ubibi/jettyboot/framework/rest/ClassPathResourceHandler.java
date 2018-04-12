package cn.ubibi.jettyboot.framework.rest;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ClassPathResourceHandler extends ResourceHandler {
    private String pathPrefix;

    public ClassPathResourceHandler(String pathPrefix) {
        super();
        this.pathPrefix = pathPrefix;
        this.setResourceBase(ClassPathResourceHandler.class.getClassLoader().getResource("").getPath());
        this.setCacheControl("max-age=31536000,public");
    }


    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        if (target.startsWith(this.pathPrefix)){
            super.handle(target,baseRequest,request,response);
        }
    }

}
