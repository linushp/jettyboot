package cn.ubibi.jettyboot.framework.rest.impl;

import cn.ubibi.jettyboot.framework.rest.ifs.ResponseRender;
import com.alibaba.fastjson.JSON;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class JsonRender implements ResponseRender {

    private Object data;

    public JsonRender(Object data) {
        this.data = data;
    }

    @Override
    public void doRender(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String jsonText = JSON.toJSONString(this.data);

        response.setContentType("application/json; charset=UTF-8");

        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter writer = response.getWriter();
        writer.print(jsonText);
        writer.flush();
        writer.close();
        response.flushBuffer();
    }
}
