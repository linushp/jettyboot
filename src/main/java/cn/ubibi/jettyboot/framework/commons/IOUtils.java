package cn.ubibi.jettyboot.framework.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(IOUtils.class);

    public static byte[] inputStreamToByteArray(InputStream inStream)
            throws IOException {

        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = inStream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] in2b = swapStream.toByteArray();

        swapStream.flush();
        swapStream.close();


        return in2b;
    }

    public static void tryClose(InputStream inStream) {
        if (inStream == null){
            return;
        }
        try {
            inStream.close();
        }catch (Exception e){
            LOGGER.info("tryClose exception");
        }
    }

}
