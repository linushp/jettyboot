package cn.ubibi.jettyboot.framework.rest.handlers;

import cn.ubibi.jettyboot.framework.rest.ifs.ResourceHandlerFilter;
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


    public static class ContextFileSystemResourceHandler extends ContextClassPathResourceHandler {
        public ContextFileSystemResourceHandler(String context, String resourceBase) {
            super(context);
            this.baseResourceHandler = new FileSystemResourceHandler(resourceBase);
            this.setHandler(this.baseResourceHandler);
        }
    }


    public static class FileSystemResourceHandler extends BaseResourceHandler {
        public FileSystemResourceHandler(String resourceBase) {
            super();
            this.setResourceBase(resourceBase);
        }
    }


    public static class ContextClassPathResourceHandler extends ContextHandler {


        protected BaseResourceHandler baseResourceHandler;

        public ContextClassPathResourceHandler() {
            this("/public");
        }

        public ContextClassPathResourceHandler(String context) {
            this(context, context);
        }

        public ContextClassPathResourceHandler(String context, String path) {
            super(context);
            this.baseResourceHandler = new ClassPathResourceHandler(path);
            this.setHandler(this.baseResourceHandler);
        }


        public ResourceHandlerFilter getResourceHandlerFilter() {
            return this.baseResourceHandler.getResourceHandlerFilter();
        }

        public void setResourceHandlerFilter(ResourceHandlerFilter resourceHandlerFilter) {
            this.baseResourceHandler.setResourceHandlerFilter(resourceHandlerFilter);
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


    private static class BaseResourceHandler extends ResourceHandler {


        private ResourceHandlerFilter resourceHandlerFilter = null;


        public BaseResourceHandler() {
            this.setCacheControl("max-age=31536000,public");
        }


        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

            if (baseRequest.isHandled()) {
                return;
            }

            response.setHeader("Server", ("boot-" + System.currentTimeMillis()));

            if (resourceHandlerFilter != null) {
                if (resourceHandlerFilter.isOK(target, baseRequest, request, response)) {
                    super.handle(target, baseRequest, request, response);
                }
            } else {
                super.handle(target, baseRequest, request, response);
            }

        }


        public ResourceHandlerFilter getResourceHandlerFilter() {
            return resourceHandlerFilter;
        }


        public void setResourceHandlerFilter(ResourceHandlerFilter resourceHandlerFilter) {
            this.resourceHandlerFilter = resourceHandlerFilter;
        }

    }

}
