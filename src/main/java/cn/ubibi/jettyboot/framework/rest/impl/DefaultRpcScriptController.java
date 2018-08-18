package cn.ubibi.jettyboot.framework.rest.impl;

import cn.ubibi.jettyboot.framework.commons.CollectionUtils;
import cn.ubibi.jettyboot.framework.commons.FrameworkConfig;
import cn.ubibi.jettyboot.framework.commons.StringUtils;
import cn.ubibi.jettyboot.framework.commons.cache.CacheMethod;
import cn.ubibi.jettyboot.framework.rest.annotation.GetMapping;
import cn.ubibi.jettyboot.framework.rest.annotation.RequestParam;
import cn.ubibi.jettyboot.framework.rest.rpc.RpcControllerScript;

import java.util.List;

public class DefaultRpcScriptController {


    @GetMapping("/")
    @CacheMethod(cacheKey = "DefaultRpcScriptController_toRpcScript", activeTime = 1000L * 60 * 5, paramKey = {0, 1, 2})
    public ScriptRender toRpcScript(@RequestParam("controllers") String controllers,
                                    @RequestParam("exportAs") String exportAs,
                                    @RequestParam("controllerPrefix") String controllerPrefix) throws Exception {

        if (StringUtils.isBlank(exportAs)) {
            exportAs = "window";
        }

        if (StringUtils.isBlank(controllerPrefix)) {
            controllerPrefix = "";
        }

        List<String> controllerArray = null;
        if (FrameworkConfig.getInstance().getRpcGetAllMethodSecretKey().equals(controllers)) {
            controllerArray = FrameworkConfig.getInstance().getRpcControllerNameList();
        } else {
            controllerArray = CollectionUtils.toList(controllers.split(";"));
        }


        String script = RpcControllerScript.toRpcScript(controllerArray, exportAs, controllerPrefix);
        return new ScriptRender(script);
    }
}
