package cn.ubibi.jettyboot.framework.rest;


import cn.ubibi.jettyboot.framework.ioc.JBServiceManager;
import cn.ubibi.jettyboot.framework.rest.ifs.JBRequestAspect;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.List;

public class JBServletWrapper {
    private String path;

    private boolean pathEndWithX;
    private String pathEndWithXBefore;

    private boolean pathStartWithX;
    private String pathStartWithXAfter;


    private boolean isMatchAll = false;

    private boolean isInit;
    private HttpServlet httpServlet;

    JBServletWrapper(String path, HttpServlet httpServlet) {

        this.path = path;
        this.httpServlet = httpServlet;

        //是否初始化过
        this.isInit = false;


        //匹配所有
        if ("*".equals(path) || "/*".equals(path)) {
            this.isMatchAll = true;
        }


        //匹配 /hello*
        this.pathEndWithX = path.endsWith("*");
        if (this.pathEndWithX) {
            this.pathEndWithXBefore = path.substring(0, path.length() - 1);
        }


        //匹配 *.do
        this.pathStartWithX = path.startsWith("*.");
        if (this.pathStartWithX) {
            this.pathStartWithXAfter = path.substring(2, path.length());
        }

    }

    public boolean matched(String target) {

        if (isMatchAll) {
            return true;
        }

        if (this.pathEndWithX) {
            return target.startsWith(this.pathEndWithXBefore);
        }

        if (this.pathStartWithX) {
            return target.endsWith(this.pathStartWithXAfter);
        }

        if (target.equals(this.path)) {
            return true;
        }

        return false;
    }


    private void tryClose(HttpServletResponse response) {
        try {
            response.flushBuffer();
        } catch (IOException e) {
        }

        try {
            PrintWriter writer = response.getWriter();
            writer.close();
        } catch (IllegalStateException e) {
            try {
                ServletOutputStream stream = response.getOutputStream();
                stream.close();
            } catch (IllegalStateException f) {
            } catch (IOException f) {
            }
        } catch (IOException e) {
        }
    }

    public void handle(HttpServletRequest request, HttpServletResponse response, List<JBRequestAspect> methodWrappers) throws Exception {

        if (!this.isInit) {
            httpServlet.init();
            this.isInit = true;
        }


        //依赖注入
        JBServiceManager.getInstance().injectDependency(httpServlet);



        JBRequest jbRequest = JBRequest.getInstance(request,this.path);
        Method serviceMethod = httpServlet.getClass().getMethod("service", ServletRequest.class, ServletResponse.class);


        //AOP处理
        for (JBRequestAspect methodWrapper : methodWrappers) {
            methodWrapper.invokeBefore(serviceMethod, jbRequest);
        }


        httpServlet.service(request, response);


        for (JBRequestAspect methodWrapper : methodWrappers) {
            methodWrapper.invokeAfter(serviceMethod, jbRequest, null);
        }


        tryClose(response);
    }
}
