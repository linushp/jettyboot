package cn.ubibi.jettyboot.framework.rest.impl;

import cn.ubibi.jettyboot.framework.commons.StringUtils;
import cn.ubibi.jettyboot.framework.rest.annotation.GetMapping;
import cn.ubibi.jettyboot.framework.rest.annotation.RequestParam;
import cn.ubibi.jettyboot.framework.rest.dwr.DwrControllerScript;

public class DefaultDwrScriptController {

    @GetMapping("/")
    public ScriptRender toDwrScript(@RequestParam("controllers") String controllers,
                                    @RequestParam("exportAs") String exportAs,
                                    @RequestParam("controllerPrefix") String controllerPrefix) throws Exception {

        if (StringUtils.isBlank(exportAs)) {
            exportAs = "window";
        }

        if (StringUtils.isBlank(controllerPrefix)) {
            controllerPrefix = "";
        }

        String[] controllerArray = controllers.split(";");
        String script = DwrControllerScript.toDwrScript(controllerArray, exportAs, controllerPrefix);
        return new ScriptRender(script);
    }
}
