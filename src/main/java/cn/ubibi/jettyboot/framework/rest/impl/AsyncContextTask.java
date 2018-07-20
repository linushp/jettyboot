package cn.ubibi.jettyboot.framework.rest.impl;

import cn.ubibi.jettyboot.framework.rest.ifs.HttpParsedRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class AsyncContextTask implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(AsyncContextTask.class);

    private List<AsyncContext> callbackAsyncContext = new ArrayList<>();
    private String taskKey;
    private Method method;  //just for renderAndAfterInvoke
    private Callable callable;

    public AsyncContextTask(String taskKey, Method method, Callable callable) {
        this.taskKey = taskKey;
        this.method = method;
        this.callable = callable;
    }

    public void addCallbackAsyncContext(AsyncContext deferredResult) {
        callbackAsyncContext.add(deferredResult);
    }

    @Override
    public void run() {
        try {

            Object invokeResult = callable.call();

            AsyncContextTaskManager.removeTask(taskKey);

            for (AsyncContext asyncContext : callbackAsyncContext) {

                HttpParsedRequest request = (HttpParsedRequest) asyncContext.getRequest();
                HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();

                ResultRenderMisc.renderAndAfterInvoke(invokeResult, method, request, response);

                asyncContext.complete();

            }
        } catch (Exception e) {
            logger.error("", e);
        }
    }
}