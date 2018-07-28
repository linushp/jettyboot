package cn.ubibi.jettyboot.framework.rest.dwr;

import cn.ubibi.jettyboot.framework.commons.CollectionUtils;
import cn.ubibi.jettyboot.framework.commons.FrameworkConfig;
import cn.ubibi.jettyboot.framework.ioc.ServiceManager;
import cn.ubibi.jettyboot.framework.rest.ControllerContextHandler;
import cn.ubibi.jettyboot.framework.rest.ControllerMethodHandler;
import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;


public class DwrControllerScript {


    public static String toDwrScript(List<String> controllerArray, String exportAs, String controllerPrefix) throws Exception {

        List<ApiModel> apis = getApiModelList(controllerArray);

        String controllerJSONString = JSON.toJSONString(controllerArray);

        String functionJSONString = JSON.toJSONString(apis);

        return toScript(controllerJSONString, functionJSONString, exportAs, controllerPrefix);

    }


    private static List<ApiModel> getApiModelList(List<String> controllerArray) throws Exception {

        if (CollectionUtils.isEmpty(controllerArray)) {
            return new ArrayList<>();
        }

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

                    if (!url.startsWith("/")) {
                        url = "/" + url;
                    }

                    ApiModel xxx = new ApiModel(methodHandler.getSupportRequestMethod(), url, methodName, controllerName);
                    apis.add(xxx);

                }
            }
        }

        return apis;
    }


    private static boolean isIncludeController(List<String> controllerArray, String controllerName) {
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


    //如果浏览器不支持Promise，返回一个非常简单的Promise，只支持一层then函数
    private static String toScript(String controllers, String functions, String exportAs, String controllerPrefix) {

        return "(function (exports) {\n" +
                "\n" +
                "\n" +
                "    var controllers = " + controllers + ";\n" +
                "    var functions = " + functions + ";\n" +
                "\n" +
                "\n" +
                "" +
                "    var Promise = window.Promise || function (methodRunner) {\n" +
                "        var that = this;\n" +
                "        this.cbOk = null;\n" +
                "        this.cbError = null;\n" +
                "        this.then = function(cbOk,cbError){\n" +
                "            this.cbOk = cbOk;\n" +
                "            this.cbError = cbError;\n" +
                "        };\n" +
                "        methodRunner(function (data) {\n" +
                "            that.cbOk && that.cbOk(data);\n" +
                "        },function(err){\n" +
                "            that.cbError && that.cbError(err);\n" +
                "        });\n" +
                "    };\n" +
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
                "                    callback(null, responseText,xhr);\n" +
                "                } else {\n" +
                "                    callback(xhr.status,null,xhr);\n" +
                "                }\n" +
                "            }\n" +
                "        };\n" +
                "        xhr.send(data);\n" +
                "    }\n" +
                "" +
                "" +
                "        function default_promise_factory(func) {\n" +
                "            return new Promise(func);\n" +
                "        }\n" +
                "" +
                "" +
                "        function toFunction(url){\n" +
                "            return function (){\n" +
                "                var args = Array.prototype.slice.call(arguments);\n" +
                "                var promise_factory  = window.jb_dwr_promise_factory || default_promise_factory;\n" +
                "                return promise_factory(function (resolve, reject) {\n" +
                "                    ajaxPost(url, args, function (err, data, xhr) {\n" +
                "                        if (err) {\n" +
                "                            reject({err:err,xhr:xhr});\n" +
                "                        } else {\n" +
                "                            resolve({data:data,xhr:xhr});\n" +
                "                        }\n" +
                "                    });\n" +
                "                });\n" +
                "            }\n" +
                "        }\n" +
                "" +
                "" +
                "" +
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
                "        exports[\"" + controllerPrefix + "\"+controllerName] = buildController(controllerName);\n" +
                "    }\n" +
                "    exports['jb_dwr_controllers'] = controllers ;\n" +
                "    exports['jb_dwr_functions'] = functions ;\n" +
                "})(" + exportAs + ");";
    }

}
