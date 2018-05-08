package cn.ubibi.jettyboot.framework.rest.dwr;

import cn.ubibi.jettyboot.framework.commons.CollectionUtils;
import cn.ubibi.jettyboot.framework.ioc.ServiceManager;
import cn.ubibi.jettyboot.framework.rest.ControllerContextHandler;
import cn.ubibi.jettyboot.framework.rest.ControllerMethodHandler;
import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

public class DwrControllerScript {

    public static String toDwrScript(String[] controllerArray) {

        ControllerContextHandler context = (ControllerContextHandler) ServiceManager.getInstance().getService(ControllerContextHandler.class);

        List<ControllerMethodHandler> methods = context.getControllerMethodHandlers();

        String contextPath = context.getContextPath();

        List<ApiModel> apis = new ArrayList<>();

        for (ControllerMethodHandler methodHandler : methods) {

            if (methodHandler.isDWR()) {

                String controllerName = methodHandler.getControllerClazzSimpleName();

                if (isIncludeController(controllerArray, controllerName)) {

                    String methodName = methodHandler.getMethod().getName();

                    String url = methodHandler.getTargetPath();
                    if (!"/".equals(contextPath)) {
                        url = contextPath + url;
                    }

                    ApiModel xxx = new ApiModel(methodHandler.getSupportRequestMethod(), url, methodName, controllerName);
                    apis.add(xxx);

                }
            }
        }


        String controllerJSONString = JSON.toJSONString(controllerArray);
        String functionJSONString = JSON.toJSONString(apis);
        return toScript(controllerJSONString, functionJSONString);
    }


    private static boolean isIncludeController(String[] controllerArray, String controllerName) {
        if (CollectionUtils.isEmpty(controllerArray)) {
            return false;
        }

        for (String cc : controllerArray) {
            if (controllerName.equals(cc)) {
                return true;
            }
        }
        return false;
    }


    private static String toScript(String controllers, String functions) {
        return "(function (exports) {\n" +
                "\n" +
                "\n" +
                "    var controllers = " + controllers + ";\n" +
                "    var functions = " + functions + ";\n" +
                "\n" +
                "\n" +
                "    function ajaxPost(url, args, callback) {\n" +
                "        var data = JSON.stringify(args);\n" +
                "        var xhr = new XMLHttpRequest();\n" +
                "        xhr.open(\"POST\", url, true);\n" +
                "        xhr.responseType = 'text';\n" +
                "        xhr.setRequestHeader(\"Content-Type\", \"application/json;charset=UTF-8\");\n" +
                "        xhr.onreadystatechange = function () {\n" +
                "            if (xhr.readyState == 4) {\n" +
                "                if ((xhr.status >= 200 && xhr.status < 300) || xhr.status == 304) {\n" +
                "                    var responseText = xhr.responseText;\n" +
                "                    callback(null, responseText);\n" +
                "                } else {\n" +
                "                    callback(xhr.status);\n" +
                "                }\n" +
                "            }\n" +
                "        };\n" +
                "        xhr.send(data);\n" +
                "    }\n" +
                "    function toFunction(url){\n" +
                "        return function (){\n" +
                "            var args = Array.prototype.slice.call(arguments);\n" +
                "            return new Promise(function (resolve, reject) {\n" +
                "                ajaxPost(url, args, function (err, data) {\n" +
                "                    if (err) {\n" +
                "                        reject(err);\n" +
                "                    } else {\n" +
                "                        resolve(data);\n" +
                "                    }\n" +
                "                });\n" +
                "            });\n" +
                "        }\n" +
                "    }\n" +
                "    function buildController(controller) {\n" +
                "        var map = {};\n" +
                "        for (var i = functions.length - 1; i >= 0; i--) {\n" +
                "            var funcDef = functions[i];\n" +
                "            var funController = funcDef['controller'];\n" +
                "            if (funController === controller) {\n" +
                "                var funUrl = funcDef['url'];\n" +
                "                var funName = funcDef['func'];\n" +
                "                map[funName] = toFunction(funUrl);\n" +
                "            }\n" +
                "        }\n" +
                "        return map;\n" +
                "    }\n" +
                "    for (var i = 0; i < controllers.length; i++) {\n" +
                "        var controllerName = controllers[i];\n" +
                "        exports[\"Dwr\"+controllerName] = buildController(controllerName);\n" +
                "    }\n" +
                "})(window);";
    }

}