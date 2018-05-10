package cn.ubibi.jettyboot.framework.rest;

import cn.ubibi.jettyboot.framework.commons.StringUtils;
import cn.ubibi.jettyboot.framework.rest.annotation.GetMapping;
import cn.ubibi.jettyboot.framework.rest.annotation.RequestParam;
import cn.ubibi.jettyboot.framework.rest.dwr.DwrControllerScript;
import cn.ubibi.jettyboot.framework.rest.impl.ScriptRender;

public class DefaultDwrScriptController {

    @GetMapping("/")
    public ScriptRender toDwrScript(@RequestParam("controllers") String controllers, @RequestParam("exportAs") String exportAs) {

        if (StringUtils.isEmpty(exportAs)) {
            exportAs = "window";
        }


        String[] controllerArray = controllers.split(";");
        String script = DwrControllerScript.toDwrScript(controllerArray, exportAs);
        return new ScriptRender(script);
    }
}
