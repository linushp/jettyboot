package cn.ubibi.jettyboot.framework.rest.impl;

import cn.ubibi.jettyboot.framework.rest.ifs.HttpParsedRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class AsyncContextTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncContextTask.class);

    private List<AsyncContext> callbackAsyncContext = new ArrayList<>();
    private String taskKey;
    private AsyncResultCallback asyncResultCallback;  //just for renderAndAfterInvoke
    private Callable callable;

    public AsyncContextTask(String taskKey, AsyncResultCallback asyncResultCallback, Callable callable) {
        this.taskKey = taskKey;
        this.asyncResultCallback = asyncResultCallback;
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

                ServletRequest request = asyncContext.getRequest();
                ServletResponse response = asyncContext.getResponse();
                asyncResultCallback.callback(invokeResult,request,response);

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