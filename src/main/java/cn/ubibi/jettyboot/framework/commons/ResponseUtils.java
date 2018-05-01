package cn.ubibi.jettyboot.framework.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ResponseUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseUtils.class);

    public static void tryClose(ServletResponse response) {
        try {
            response.flushBuffer();
        } catch (IOException e) {
        }

        try {
            PrintWriter writer = response.getWriter();
            writer.close();
        } catch (IllegalStateException e) {
            try {
                ServletOutputStream stream = response.getOutputStream();
                stream.close();
            } catch (IllegalStateException f) {

            } catch (IOException f) {

            }
        } catch (IOException e) {

        }
    }

}
