package cn.ubibi.jettyboot.framework.rest.handlers.proxyfiles;

import cn.ubibi.jettyboot.framework.commons.*;
import cn.ubibi.jettyboot.framework.rest.impl.AsyncContextTaskManager;
import cn.ubibi.jettyboot.framework.rest.impl.AsyncResultCallback;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncContext;
import javax.servlet.*;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;

public class FileProxyForwardHandler extends AbstractHandler {

    private HttpProxyEntityGetter httpProxyEntityGetter;

    public FileProxyForwardHandler(HttpProxyEntityGetter httpProxyEntityGetter) {
        this.httpProxyEntityGetter = httpProxyEntityGetter;
    }


    private static final Logger LOGGER = LoggerFactory.getLogger(FileProxyForwardHandler.class);

    @Override
    public void handle(final String request_path, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {


        LOGGER.info("handle :  " + request_path);


        try {
            final HttpProxyEntity httpProxyEntity = getMatchedHttpProxyEntity(request_path, httpProxyEntityGetter);
            if (httpProxyEntity == null) {
                return;
            }

            baseRequest.setHandled(true);

            final AsyncContext asyncContext = request.startAsync(request, response);

            String taskKey = "ProxyForwardHandler:" + request_path;

            MyAsyncResultCallback myAsyncResultCallback = new MyAsyncResultCallback();

            Map<String, String[]> parameterMap = request.getParameterMap();

            MyInvokeCallable myInvokeCallable = new MyInvokeCallable(httpProxyEntity, request_path, httpProxyEntityGetter, parameterMap);

            AsyncContextTaskManager.addTask(taskKey, myAsyncResultCallback, asyncContext, myInvokeCallable);


        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }


    private static class MyAsyncResultCallback implements AsyncResultCallback {

        @Override
        public void callback(Object invokeResult, ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
            if (invokeResult instanceof String) {
                String disk_path = (String) invokeResult;
                File disk_file = new File(disk_path);
                sendFile((HttpServletResponse) servletResponse, disk_file);
            } else if (invokeResult instanceof Integer) {
                Integer statusCode = (Integer) invokeResult;
                sendErrorResponse(statusCode, (HttpServletResponse) servletResponse);
            }
        }
    }


    private static class MyInvokeCallable implements Callable {

        private HttpProxyEntity httpProxyEntity;
        private String request_path;
        private HttpProxyEntityGetter httpProxyEntityGetter;
        private Map<String, String[]> parameterMap;

        public MyInvokeCallable(HttpProxyEntity httpProxyEntity, String request_path, HttpProxyEntityGetter httpProxyEntityGetter, Map<String, String[]> parameterMap) {
            this.httpProxyEntity = httpProxyEntity;
            this.request_path = request_path;
            this.httpProxyEntityGetter = httpProxyEntityGetter;
            this.parameterMap = parameterMap;
        }

        @Override
        public Object call() throws Exception {

            String proxy_path = httpProxyEntity.getPath();
            String http_target_path;
            if (proxy_path.endsWith("/")) {
                String suffix = request_path.substring(proxy_path.length());
                http_target_path = httpProxyEntity.getTarget() + suffix;
            } else {
                http_target_path = httpProxyEntity.getTarget();
            }

            http_target_path = httpProxyEntityGetter.wrapperHttpTargetPath(http_target_path, parameterMap);

            LOGGER.info("proxy http:" + http_target_path);

            String disk_path = getDiskCacheFilePath(http_target_path);

            File disk_file = new File(disk_path);
            if (disk_file.exists()) {
                return disk_path;
            } else {

                HttpUtils.HttpResult httpResult = HttpUtils.sendGetRequestInputStream(http_target_path);

                if (httpResult.getResponseCode() == 200) {
                    saveToFile(httpResult.getInputStream(), disk_file);
                    return disk_path;
                } else {
                    LOGGER.error("HttpGetFileRequest of " + http_target_path + " , ResponseCode:  " + httpResult.getResponseCode());
                    return httpResult.getResponseCode();
                }
            }
        }
    }


    private static void saveToFile(InputStream contentInputStream, File disk_file) throws IOException {

        File parent_file = disk_file.getParentFile();
        FileUtils.forceMkdirs(parent_file);


        FileOutputStream fileOutputStream = new FileOutputStream(disk_file);
        FileUtils.inputStream2OutputStream(contentInputStream, fileOutputStream);

        contentInputStream.close();

        fileOutputStream.flush();
        fileOutputStream.close();
    }


    private static void sendErrorResponse(int statusCode, HttpServletResponse response) throws IOException {
        response.setStatus(statusCode);
        response.getWriter().write("Error Occur");
        response.getWriter().flush();
        response.getWriter().close();
        response.flushBuffer();
    }


    private static void sendFile(HttpServletResponse response, File disk_file) throws IOException {

        long file_size = disk_file.length();

        response.setContentType(getContentType(disk_file));
        response.setHeader("Content-Length", "" + file_size);
        response.setHeader("Server", FrameworkConfig.getInstance().getResponseServerName());
        response.setHeader("Connection", "keep-alive");
        response.setHeader("cache-control", "public,max-age=25920000");
        response.setStatus(HttpServletResponse.SC_OK);

        ServletOutputStream outputStream = response.getOutputStream();
        FileInputStream inputStream = new FileInputStream(disk_file);

        FileUtils.inputStream2OutputStream(inputStream, outputStream);

        inputStream.close();
        outputStream.flush();
        outputStream.close();
        response.flushBuffer();
    }


    private static String getContentType(File disk_file) {
        String file_name = disk_file.getName();

        int indexOfW = file_name.indexOf('?');
        if (indexOfW >= 0) {
            file_name = file_name.substring(0, indexOfW);
        }

        String file_suffix = FileContentTypeEnum.getFileNameSuffix(file_name);
        String contentType = FileContentTypeEnum.getContentTypeBySuffix(file_suffix);
        boolean isTextContent = FileContentTypeEnum.isTextContentTypeBySuffix(file_suffix);
        if (isTextContent) {
            return contentType;//+ "; charset=" + FrameworkConfig.getInstance().getCharset().name();
        }
        return contentType;
    }


    private static String getDiskCacheFilePath(String http_target_path) {

        String p = http_target_path;
        if (http_target_path.startsWith("https://")) {
            p = http_target_path.substring("https://".length());
        } else if (http_target_path.startsWith("http://")) {
            p = http_target_path.substring("http://".length());
        } else if (http_target_path.startsWith("//")) {
            p = http_target_path.substring("//".length());
        }


        int indexOfW = p.indexOf("?");
        if (indexOfW > 0) {
            String p1 = p.substring(0, indexOfW);
            String p2 = p.substring(indexOfW);
            String p2m = HashCryptoUtils.encrypt_md5_base58(p2);
            p = p1 + "?pm=" + p2m;
        }


        return FrameworkConfig.getInstance().getFileProxyTempCachePath() + "/" + p;
    }


    //路由选择
    private static HttpProxyEntity getMatchedHttpProxyEntity(String request_path, HttpProxyEntityGetter httpProxyEntityGetter) throws Exception {


        List<HttpProxyEntity> proxyEntities = httpProxyEntityGetter.getHttpProxyEntityList();

        //过滤出符合条件的
        List<HttpProxyEntity> proxy1 = new ArrayList<>();
        for (HttpProxyEntity proxy : proxyEntities) {
            if (request_path.startsWith(proxy.getPath())) {
                if (!StringUtils.isEmpty(proxy.getPath()) && !StringUtils.isEmpty(proxy.getTarget())) {
                    proxy1.add(proxy);
                }
            }
        }

        //排序
        Collections.sort(proxy1, new Comparator<HttpProxyEntity>() {
            @Override
            public int compare(HttpProxyEntity o1, HttpProxyEntity o2) {
                //根据长度从大到小
                return o2.getPath().length() - o1.getPath().length();
            }
        });

        //获取匹配最佳的
        if (proxy1.isEmpty()) {
            return null;
        }
        return proxy1.get(0);
    }


}