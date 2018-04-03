package cn.ubibi.jettyboot.framework.rest;

import cn.ubibi.jettyboot.framework.commons.CollectionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class HttpServletWrapper {
    private String path;
    private String pathPrefix;
    private boolean pathEndWithX;
    private  HttpServlet httpServlet;
    HttpServletWrapper(String path, HttpServlet httpServlet) {
        this.path = path;
        this.pathEndWithX = this.path.endsWith("*");
        if(this.pathEndWithX){
            this.pathPrefix =  this.path.substring(0,this.path.length()-1);
        }
        this.httpServlet = httpServlet;
    }

    public boolean matched(String target) {

        if(this.pathEndWithX){
            return target.startsWith(this.pathPrefix);
        }

        if(target.equals(this.path)){
            return true;
        }
        return false;
    }

    public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        httpServlet.service(request,response);
    }
}
