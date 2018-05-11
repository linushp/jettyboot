package cn.ubibi.jettyboot.framework.rest.impl;

import cn.ubibi.jettyboot.framework.commons.FrameworkConfig;
import cn.ubibi.jettyboot.framework.commons.ResponseUtils;
import cn.ubibi.jettyboot.framework.rest.ifs.ResponseRender;
import cn.ubibi.jettyboot.framework.rest.impl.base.TextRespRenderAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ScriptRender extends TextRespRenderAdapter {

    private String script;
    public ScriptRender(String script) {
        this.script = script;
    }

    @Override
    public void doRender(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Cache-Control","public, max-age=31536000");
        super.doRender(request,response);
    }


    @Override
    public byte[] getContentBytes() {
        byte[] contentBytes = this.script.getBytes(FrameworkConfig.getInstance().getCharset());
        return contentBytes;
    }

    @Override
    public String getContentType() {
        return "text/javascript";
    }
}
