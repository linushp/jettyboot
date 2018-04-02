package cn.ubibi.jettyboot.framework.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface IRestRender {
    void doRender(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
