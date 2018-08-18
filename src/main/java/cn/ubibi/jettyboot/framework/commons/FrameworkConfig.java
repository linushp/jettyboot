package cn.ubibi.jettyboot.framework.commons;


import cn.ubibi.jettyboot.framework.jdbc.ConnectionFactory;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrameworkConfig {

    public static final String DEFAULT_CONNECTION_FACTORY_NAME = "default";
    private static FrameworkConfig instance = new FrameworkConfig();


    private FrameworkConfig() {
    }

    public static FrameworkConfig getInstance() {
        return instance;
    }


    //RPC脚本文件的路径
    private String rpcScriptPath = "/script_rpc_controller";

    //RpcController的路径前缀
    private String rpcPrefix = "/rpc_controller/";

    //获取所有rpc方法时的秘密参数
    private String rpcGetAllMethodSecretKey = "get_all_controllers";

    //所有的Controller的名称都会放在这里
    private List<String> rpcControllerNameList = new ArrayList<>();

    //jdbc查询中，最多页面条数
    private int jbdcMaxPageRowSize = 50;

    //HTTP Request Max Body
    private int maxRequestBodySize = 10000;

    //默认字符集
    private Charset charset = Charset.forName("UTF-8");

    private Charset requestBodyCharset = Charset.forName("UTF-8");

    //HTTP Header 输出的Server字段
    private String responseServerName = "jetty_boot";

    //Controller的请求是否可以被缓存
    private boolean cacheAnnotation = true;

    //FileProxyForwardHandler缓存文件路径
    private String fileProxyTempCachePath = "/tmp/FileProxyForwardHandler";

    //ConnectionFactory存储
    private Map<String, ConnectionFactory> connectionFactoryMap = new HashMap<>();


    public boolean isCacheAnnotation() {
        return cacheAnnotation;
    }

    public void setCacheAnnotation(boolean cacheAnnotation) {
        this.cacheAnnotation = cacheAnnotation;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public void setMaxRequestBodySize(int maxRequestBodySize) {
        this.maxRequestBodySize = maxRequestBodySize;
    }

    public int getMaxRequestBodySize() {
        return maxRequestBodySize;
    }

    public String getRpcPrefix() {
        return rpcPrefix;
    }

    public void setRpcPrefix(String rpcPrefix) {
        this.rpcPrefix = rpcPrefix;
    }

    public String getRpcScriptPath() {
        return rpcScriptPath;
    }

    public void setRpcScriptPath(String rpcScriptPath) {
        this.rpcScriptPath = rpcScriptPath;
    }

    public String getResponseServerName() {
        return responseServerName;
    }

    public void setResponseServerName(String responseServerName) {
        this.responseServerName = responseServerName;
    }

    public Charset getRequestBodyCharset() {
        return requestBodyCharset;
    }

    public void setRequestBodyCharset(Charset requestBodyCharset) {
        this.requestBodyCharset = requestBodyCharset;
    }

    public void addRpcControllerName(String simpleName) {
        this.rpcControllerNameList.add(simpleName);
    }

    public List<String> getRpcControllerNameList() {
        return rpcControllerNameList;
    }

    public String getRpcGetAllMethodSecretKey() {
        return rpcGetAllMethodSecretKey;
    }

    public void setRpcGetAllMethodSecretKey(String rpcGetAllMethodSecretKey) {
        this.rpcGetAllMethodSecretKey = rpcGetAllMethodSecretKey;
    }

    public int getJbdcMaxPageRowSize() {
        return jbdcMaxPageRowSize;
    }

    public void setJbdcMaxPageRowSize(int jbdcMaxPageRowSize) {
        this.jbdcMaxPageRowSize = jbdcMaxPageRowSize;
    }

    public void addConnectionFactory(ConnectionFactory instance) {
        connectionFactoryMap.put(DEFAULT_CONNECTION_FACTORY_NAME, instance);
    }

    public void addConnectionFactory(String connectionFactoryName, ConnectionFactory instance) {
        connectionFactoryMap.put(connectionFactoryName, instance);
    }

    public ConnectionFactory getConnectionFactory(String connectionFactoryName) {
        return connectionFactoryMap.get(connectionFactoryName);
    }

    public String getFileProxyTempCachePath() {
        return fileProxyTempCachePath;
    }

    public void setFileProxyTempCachePath(String fileProxyTempCachePath) {
        this.fileProxyTempCachePath = fileProxyTempCachePath;
    }
}
