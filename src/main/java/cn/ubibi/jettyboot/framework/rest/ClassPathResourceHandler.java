package cn.ubibi.jettyboot.framework.rest;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;

public class ClassPathResourceHandler extends ResourceHandler {


    public ClassPathResourceHandler(){
        this("public");
    }


    public ClassPathResourceHandler(String pathPrefix)  {
        super();

        pathPrefix = (pathPrefix.charAt(0) == '/' ? pathPrefix.substring(1) : pathPrefix);

        URL xx = this.getClass().getClassLoader().getResource(pathPrefix);
        System.out.println("ClassPathResourceHandler:  " + xx);
        this.setBaseResource(Resource.newResource(xx));
        this.setCacheControl("max-age=31536000,public");
    }


    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        super.handle(target,baseRequest,request,response);
    }



}
