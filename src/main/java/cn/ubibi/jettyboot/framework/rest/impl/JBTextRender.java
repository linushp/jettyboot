package cn.ubibi.jettyboot.framework.rest.impl;

import cn.ubibi.jettyboot.framework.rest.ifs.JBResponseRender;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class JBTextRender implements JBResponseRender {

    private String text;

    public JBTextRender(String text) {
        this.text = text;
    }

    @Override
    public void doRender(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("text/html;charset=utf-8");
//        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter writer = response.getWriter();

        writer.println(text);

        writer.flush();
        writer.close();
        response.flushBuffer();
    }
}
