package cn.ubibi.jettyboot.framework.rest.handlers.proxyfiles;


public class HttpProxyEntity {
    private String path;
    private String target;



    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
