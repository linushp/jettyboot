package cn.ubibi.jettyboot.framework.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface IExceptionHandler {

    boolean handle(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException;
}
