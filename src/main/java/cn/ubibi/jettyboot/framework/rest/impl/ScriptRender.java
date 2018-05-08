package cn.ubibi.jettyboot.framework.rest.impl;

import cn.ubibi.jettyboot.framework.commons.GlobalConfig;
import cn.ubibi.jettyboot.framework.commons.ResponseUtils;
import cn.ubibi.jettyboot.framework.rest.ifs.ResponseRender;
import com.alibaba.fastjson.JSON;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ScriptRender implements ResponseRender {

    private String script;
    public ScriptRender(String script) {
        this.script = script;
    }

    @Override
    public void doRender(HttpServletRequest request, HttpServletResponse response) throws IOException {

        byte[] contentBytes = this.script.getBytes(GlobalConfig.getInstance().getCharset());


        response.setContentType("application/javascript; charset=" + GlobalConfig.getInstance().getCharset().name());
        response.setHeader("Cache-Control","public, max-age=31536000");
        response.setHeader("Content-Length",""+contentBytes.length);



        response.setStatus(HttpServletResponse.SC_OK);
        response.getOutputStream().write(contentBytes);
        response.getOutputStream().close();
//
//        PrintWriter writer = response.getWriter();
//        writer.print(this.script);
//        writer.flush();
        ResponseUtils.tryClose(response);
    }
}
