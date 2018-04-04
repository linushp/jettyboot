package cn.ubibi.jettyboot.framework.rest;


import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class HttpServletWrapper {
    private String path;

    private boolean pathEndWithX;
    private String pathEndWithXBefore;

    private boolean pathStartWithX;
    private String pathStartWithXAfter;


    private boolean isMatchAll = false;

    private boolean isInit;
    private HttpServlet httpServlet;

    HttpServletWrapper(String path, HttpServlet httpServlet) {

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


    private void tryClose(HttpServletResponse response){
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

    public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        if (!this.isInit) {
            httpServlet.init();
            this.isInit = true;
        }

        httpServlet.service(request, response);

        tryClose(response);
    }
}
