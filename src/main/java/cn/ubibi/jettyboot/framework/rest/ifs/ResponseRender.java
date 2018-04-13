package cn.ubibi.jettyboot.framework.rest.ifs;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


//输出浏关闭，必须在render手动关闭。框架不会给你懂关闭
public interface ResponseRender {
    void doRender(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
