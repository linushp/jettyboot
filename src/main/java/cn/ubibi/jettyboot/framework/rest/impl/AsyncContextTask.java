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

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncContextTask.class);

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

        boolean isCallException = false;
        Object invokeResult = null;
        try {
            invokeResult = callable.call();
            AsyncContextTaskManager.removeTask(taskKey);
        } catch (Exception e) {
            isCallException = true;
            LOGGER.error("", e);
        }


        //调用出现异常
        if (isCallException) {
            completeAsyncContext();
            return;
        }


        for (AsyncContext asyncContext : callbackAsyncContext) {

            try {

                HttpParsedRequest request = (HttpParsedRequest) asyncContext.getRequest();
                HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();
                ResultRenderMisc.renderAndAfterInvoke(invokeResult, method, request, response);

            } catch (Exception e) {
                LOGGER.error("", e);
            } finally {
                asyncContext.complete();
            }

        }

    }

    private void completeAsyncContext() {
        for (AsyncContext asyncContext : callbackAsyncContext) {
            asyncContext.complete();
        }
    }
}