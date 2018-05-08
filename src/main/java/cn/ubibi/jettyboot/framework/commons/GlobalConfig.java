package cn.ubibi.jettyboot.framework.commons;


import java.nio.charset.Charset;

public class GlobalConfig {
    private static GlobalConfig instance = new GlobalConfig();
    private GlobalConfig(){}
    public static GlobalConfig getInstance(){
        return instance;
    }


    private Charset charset = Charset.forName("UTF-8");

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }
}
