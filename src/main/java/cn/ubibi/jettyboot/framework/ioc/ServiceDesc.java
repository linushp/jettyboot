package cn.ubibi.jettyboot.framework.ioc;

public class ServiceDesc {
    private Object serviceObject;
    private Object proxyServiceObject;
    public ServiceDesc(Object serviceObject, Object proxyServiceObject) {
        this.serviceObject = serviceObject;
        this.proxyServiceObject = proxyServiceObject;
    }

    public Object getServiceObject() {
        return serviceObject;
    }

    public void setServiceObject(Object serviceObject) {
        this.serviceObject = serviceObject;
    }

    public Object getProxyServiceObject() {
        return proxyServiceObject;
    }

    public void setProxyServiceObject(Object proxyServiceObject) {
        this.proxyServiceObject = proxyServiceObject;
    }
}
