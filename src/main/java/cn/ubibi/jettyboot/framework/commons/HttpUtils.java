package cn.ubibi.jettyboot.framework.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;


public class HttpUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtils.class);


    public static HttpResult sendGetRequestInputStream(String path) throws IOException {
        URL url = new URL(path.trim());
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(30000);
        urlConnection.setReadTimeout(30000);
        urlConnection.setRequestMethod("GET");
        int responseCode = urlConnection.getResponseCode();
        InputStream inputStream = urlConnection.getInputStream();
        return new HttpResult(responseCode,inputStream);
    }



    public static byte[] sendGetRequestBytes(String path) throws IOException {

        HttpResult httpResult = sendGetRequestInputStream(path);

        InputStream inputStream = httpResult.getInputStream();

        byte[] bytes = IOUtils.inputStreamToByteArray(inputStream);

        inputStream.close();

        return bytes;
    }



    public static class HttpResult{
        private InputStream inputStream;
        private int responseCode;

        public HttpResult(int responseCode,InputStream inputStream) {
            this.inputStream = inputStream;
            this.responseCode = responseCode;
        }

        public InputStream getInputStream() {
            return inputStream;
        }

        public void setInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        public int getResponseCode() {
            return responseCode;
        }

        public void setResponseCode(int responseCode) {
            this.responseCode = responseCode;
        }
    }


}
