package cn.ubibi.jettyboot.demotest.controller;

import cn.ubibi.jettyboot.framework.rest.ifs.JBExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MyExceptionHandler implements JBExceptionHandler {

    @Override
    public boolean handle(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {

        if("NotLogin11".equals(e.getMessage())){
            response.getWriter().println("User is Not Login ");
            response.getWriter().close();
            response.flushBuffer();

            return true;
        }

        return false;
    }
}
