package cn.ubibi.jettyboot.framework.rest.impl;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public interface AsyncResultCallback {

    void callback(Object invokeResult, ServletRequest request, ServletResponse response) throws Exception;
}



