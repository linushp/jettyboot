package cn.ubibi.jettyboot.framework.rest.impl;


import cn.ubibi.jettyboot.framework.rest.annotation.AsyncMergeCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncContextTaskManager {
    private static Logger logger = LoggerFactory.getLogger(AsyncContextTaskManager.class);

    private static ExecutorService execPools = Executors.newFixedThreadPool(10);

    //正在执行的
    private static final Map<String, DeferredResultUnionTask> runningTaskMap = new HashMap<>();

    public static synchronized void addTask(String taskKey, AsyncContext deferredResult, Callable callable) {
        DeferredResultUnionTask deferredResultUnionTask = runningTaskMap.get(taskKey);
        if (deferredResultUnionTask == null) {
            deferredResultUnionTask = new DeferredResultUnionTask(taskKey);
            deferredResultUnionTask.doRun(callable);
            runningTaskMap.put(taskKey, deferredResultUnionTask);
        }
        deferredResultUnionTask.addCallbackDeferredResult(deferredResult);
    }


    private static synchronized void removeTask(String taskKey) {
        runningTaskMap.remove(taskKey);
    }


    public static String toTaskKey(Method method, AsyncMergeCall unionMethodCall, Object[] params) {

        StringBuilder sb = new StringBuilder();
        sb.append(method.getDeclaringClass().getSimpleName());
        sb.append(":");
        sb.append(method.getName());

        int[] paramsKey = unionMethodCall.paramKey();
        if (paramsKey.length == 0) {
            return sb.toString();
        }

        sb.append(":");
        for (int i = 0; i < paramsKey.length; i++) {
            sb.append("_");
            sb.append(String.valueOf(params[paramsKey[i]]));
        }
        return sb.toString();
    }


    private static class DeferredResultUnionTask {
        private List<AsyncContext> deferredResultList = new ArrayList<>();
        private String taskKey;

        public DeferredResultUnionTask(String taskKey) {
            this.taskKey = taskKey;
        }

        public void addCallbackDeferredResult(AsyncContext deferredResult) {
            deferredResultList.add(deferredResult);
        }

        public void doRun(Callable callable) {
            execPools.execute(new Runnable() {
                @Override
                public void run() {
                    try {

                        Object invokeResult = callable.call();

                        AsyncContextTaskManager.removeTask(taskKey);

                        for (AsyncContext asyncContext : deferredResultList) {

                            HttpServletRequest request = (HttpServletRequest) asyncContext.getRequest();
                            HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();

                            ResultRenderMisc.renderAndAfterInvoke(invokeResult, null, request, response);

                            asyncContext.complete();
                        }
                    } catch (Exception e) {
                        logger.error("", e);
                    }
                }
            });
        }
    }
}
