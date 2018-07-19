package cn.ubibi.jettyboot.framework.rest.impl;

import cn.ubibi.jettyboot.framework.rest.ifs.ControllerAspect;
import cn.ubibi.jettyboot.framework.rest.ifs.ResponseRender;
import cn.ubibi.jettyboot.framework.slot.SlotComponentManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;

public class ResultRenderMisc {
    public static void render(Object invokeResult, Method method, HttpServletRequest httpParsedRequest, HttpServletResponse response) throws Exception {
        //Aspect后置
        List<ControllerAspect> methodWrappers = SlotComponentManager.getInstance().getControllerAspects();
        for (ControllerAspect methodWrapper : methodWrappers) {
            methodWrapper.afterInvoke(method, httpParsedRequest, invokeResult, response);
        }

        if (invokeResult instanceof ResponseRender) {
            ((ResponseRender) invokeResult).doRender(httpParsedRequest, response);
        } else if (invokeResult instanceof String) {
            new TextRender(invokeResult.toString()).doRender(httpParsedRequest, response);
        } else {
            new JsonRender(invokeResult).doRender(httpParsedRequest, response);
        }

        //Aspect后置
        for (ControllerAspect methodWrapper : methodWrappers) {
            methodWrapper.afterRender(method, httpParsedRequest, invokeResult, response);
        }
    }
}
