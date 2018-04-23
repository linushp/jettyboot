package cn.ubibi.jettyboot.framework.rest;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;


public class ResourceHandlers {


    public static class ContextFileSystemResourceHandler extends ContextHandler{
        public ContextFileSystemResourceHandler(String context,String resourceBase){
            super(context);
            this.setHandler(new FileSystemResourceHandler(resourceBase));
        }
    }



    public static class FileSystemResourceHandler extends BaseResourceHandler{
        public FileSystemResourceHandler(String resourceBase){
            super();
            this.setResourceBase(resourceBase);
        }
    }



    public static class ContextClassPathResourceHandler extends ContextHandler {
        public ContextClassPathResourceHandler() {
            this("/public");
        }

        public ContextClassPathResourceHandler(String context) {
            this(context, context);
        }

        public ContextClassPathResourceHandler(String context, String path) {
            super(context);
            this.setHandler(new ClassPathResourceHandler(path));
        }
    }




    public static class ClassPathResourceHandler extends BaseResourceHandler {


        public ClassPathResourceHandler() {
            this("public");
        }

        public ClassPathResourceHandler(String pathPrefix) {
            super();

            pathPrefix = (pathPrefix.charAt(0) == '/' ? pathPrefix.substring(1) : pathPrefix);
            URL pathUrl = this.getClass().getClassLoader().getResource(pathPrefix);
            this.setBaseResource(Resource.newResource(pathUrl));
        }
    }




    private static class BaseResourceHandler extends ResourceHandler{

        public BaseResourceHandler(){
            this.setCacheControl("max-age=31536000,public");
        }

        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            response.setHeader("Server", ("boot-" + System.currentTimeMillis()));
            super.handle(target, baseRequest, request, response);
        }
    }

}
