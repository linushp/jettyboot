package cn.ubibi.jettyboot.framework.rest.ifs;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface JBExceptionHandler {

    boolean handle(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException;
}
