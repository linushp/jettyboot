package cn.ubibi.jettyboot.framework.commons;


import java.nio.charset.Charset;

public class FrameworkConfig {

    private static FrameworkConfig instance = new FrameworkConfig();
    private FrameworkConfig(){}
    public static FrameworkConfig getInstance(){
        return instance;
    }




    //HTTP Request Max Body
    private int maxRequestBodySize = 10000;

    //默认字符集
    private Charset charset = Charset.forName("UTF-8");



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
}
