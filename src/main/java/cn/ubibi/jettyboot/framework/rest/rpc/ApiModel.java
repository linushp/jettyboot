package cn.ubibi.jettyboot.framework.rest.rpc;

public class ApiModel {
    private String method;
    private String url;
    private String func;
    private String controller;

    public ApiModel(String method, String url, String func, String controller) {
        this.method = method;
        this.url = url;
        this.func = func;
        this.controller = controller;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public String getController() {
        return controller;
    }

    public void setController(String controller) {
        this.controller = controller;
    }
}
