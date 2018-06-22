package cn.ubibi.jettyboot.framework.commons;


import java.nio.charset.Charset;

public class FrameworkConfig {

    private static FrameworkConfig instance = new FrameworkConfig();


    private FrameworkConfig() {
    }

    public static FrameworkConfig getInstance() {
        return instance;
    }


    //DWR脚本文件的路径
    private String dwrScriptPath = "/script_dwr_controller";

    //DwrController的路径前缀
    private String dwrPrefix = "/dwr_controller/";

    //HTTP Request Max Body
    private int maxRequestBodySize = 10000;

    //默认字符集
    private Charset charset = Charset.forName("UTF-8");

    private Charset requestBodyCharset = Charset.forName("UTF-8");

    //HTTP Header 输出的Server字段
    private String responseServerName = "jetty_boot";


    //Controller的请求是否可以被缓存
    private boolean cacheAnnotation = true;


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

    public String getDwrPrefix() {
        return dwrPrefix;
    }

    public void setDwrPrefix(String dwrPrefix) {
        this.dwrPrefix = dwrPrefix;
    }

    public String getDwrScriptPath() {
        return dwrScriptPath;
    }

    public void setDwrScriptPath(String dwrScriptPath) {
        this.dwrScriptPath = dwrScriptPath;
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
}
