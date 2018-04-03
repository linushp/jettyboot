package cn.ubibi.jettyboot.framework.rest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HttpServletWrapper {
    private String path;
    private  HttpServlet httpServlet;
    HttpServletWrapper(String path, HttpServlet httpServlet) {
        this.path = path;
        this.httpServlet = httpServlet;
    }

    public boolean matched(String target) {
        if(target.startsWith(path)){
            return true;
        }
        return false;
    }

    public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        httpServlet.service(request,response);
    }
}
