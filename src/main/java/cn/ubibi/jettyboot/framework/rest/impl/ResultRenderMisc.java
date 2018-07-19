package cn.ubibi.jettyboot.framework.rest.impl;

import cn.ubibi.jettyboot.framework.rest.ifs.ControllerAspect;
import cn.ubibi.jettyboot.framework.rest.ifs.HttpParsedRequest;
import cn.ubibi.jettyboot.framework.rest.ifs.ResponseRender;
import cn.ubibi.jettyboot.framework.slot.SlotComponentManager;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

public class ResultRenderMisc {

    public static void doRender(Object invokeResult, HttpParsedRequest httpParsedRequest, HttpServletResponse response) throws IOException {
        if (invokeResult instanceof VoidResult){
            //do nothing
        }
        else if (invokeResult instanceof ResponseRender) {
            ((ResponseRender) invokeResult).doRender(httpParsedRequest, response);
        } else if (invokeResult instanceof String) {
            new TextRender(invokeResult.toString()).doRender(httpParsedRequest, response);
        } else {
            new JsonRender(invokeResult).doRender(httpParsedRequest, response);
        }
    }


    public static void renderAndAfterInvoke(Object invokeResult, Method method, HttpParsedRequest httpParsedRequest, HttpServletResponse response) throws Exception {
        //Aspect后置
        List<ControllerAspect> methodWrappers = SlotComponentManager.getInstance().getControllerAspects();
        for (ControllerAspect methodWrapper : methodWrappers) {
            methodWrapper.afterInvoke(method, httpParsedRequest, invokeResult, response);
        }

        doRender(invokeResult,httpParsedRequest,response);

        //Aspect后置
        for (ControllerAspect methodWrapper : methodWrappers) {
            methodWrapper.afterRender(method, httpParsedRequest, invokeResult, response);
        }
    }
}
