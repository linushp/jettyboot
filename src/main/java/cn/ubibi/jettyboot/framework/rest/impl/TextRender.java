package cn.ubibi.jettyboot.framework.rest.impl;

import cn.ubibi.jettyboot.framework.commons.FrameworkConfig;
import cn.ubibi.jettyboot.framework.rest.impl.base.TextRespRenderAdapter;

public class TextRender extends TextRespRenderAdapter {

    private String text;

    public TextRender(String text) {
        this.text = text;
    }

    @Override
    public byte[] getContentBytes() {
        byte[] contentBytes = this.text.getBytes(FrameworkConfig.getInstance().getCharset());
        return contentBytes;
    }

    @Override
    public String getContentType() {
        return "text/html";
    }
}
