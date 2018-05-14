package cn.ubibi.jettyboot.framework.rest.impl;

import cn.ubibi.jettyboot.framework.commons.FrameworkConfig;
import cn.ubibi.jettyboot.framework.rest.impl.base.TextRespRenderAdapter;
import com.alibaba.fastjson.JSON;

public class JsonRender extends TextRespRenderAdapter {

    private Object data;

    public JsonRender(Object data) {
        this.data = data;
    }


    @Override
    public byte[] getContentBytes() {
        String jsonText = JSON.toJSONString(this.data);
        byte[] contentBytes = jsonText.getBytes(FrameworkConfig.getInstance().getCharset());
        return contentBytes;
    }


    @Override
    public String getContentType() {
        return "application/json";
    }
}
