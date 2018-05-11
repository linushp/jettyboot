package cn.ubibi.jettyboot.framework.rest.impl.base;

import cn.ubibi.jettyboot.framework.commons.FrameworkConfig;
import cn.ubibi.jettyboot.framework.commons.ResponseUtils;
import cn.ubibi.jettyboot.framework.rest.ifs.ResponseRender;
import com.alibaba.fastjson.JSON;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class TextRespRenderAdapter implements ResponseRender {

    public void doRender(HttpServletRequest request, HttpServletResponse response) throws IOException {

        byte[] contentBytes = getContentBytes();

        response.setContentType(getContentType() + "; charset=" + FrameworkConfig.getInstance().getCharset().name());
        response.setHeader("Content-Length", "" + contentBytes.length);
        response.setHeader("Server", FrameworkConfig.getInstance().getResponseServerName());
        response.setHeader("Connection","keep-alive");

        response.setStatus(HttpServletResponse.SC_OK);
        response.getOutputStream().write(contentBytes);
        response.getOutputStream().close();

        ResponseUtils.tryClose(response);
    }


    public abstract byte[] getContentBytes();

    public abstract String getContentType();
}
