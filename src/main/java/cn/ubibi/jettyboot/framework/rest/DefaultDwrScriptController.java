package cn.ubibi.jettyboot.framework.rest;

import cn.ubibi.jettyboot.framework.rest.annotation.GetMapping;
import cn.ubibi.jettyboot.framework.rest.annotation.RequestParam;
import cn.ubibi.jettyboot.framework.rest.dwr.DwrControllerScript;
import cn.ubibi.jettyboot.framework.rest.impl.ScriptRender;

public class DefaultDwrScriptController {

    @GetMapping("/")
    public ScriptRender toDwrScript(@RequestParam("controllers") String controllers){
        String[] controllerArray = controllers.split(";");
        String script = DwrControllerScript.toDwrScript(controllerArray);
        return new ScriptRender(script);
    }
}
