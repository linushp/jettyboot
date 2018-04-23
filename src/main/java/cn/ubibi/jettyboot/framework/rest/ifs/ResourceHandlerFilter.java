package cn.ubibi.jettyboot.framework.rest.ifs;

import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface ResourceHandlerFilter {
    boolean isOK(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException;
}