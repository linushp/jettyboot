package cn.ubibi.jettyboot.framework.rest.impl;


import cn.ubibi.jettyboot.framework.rest.annotation.AsyncMergeMethod;

import javax.servlet.AsyncContext;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class AsyncContextTaskManager {


    //线程池
    private static Executor execPools = null;

    //正在执行的任务
    private static final Map<String, AsyncContextTask> runningTaskMap = new HashMap<>();

    public static synchronized void addTask(String taskKey, AsyncResultCallback asyncResultCallback, AsyncContext asyncContext, Callable callable) {

        AsyncContextTask asyncRequestTask = runningTaskMap.get(taskKey);

        if (asyncRequestTask == null) {

            asyncRequestTask = new AsyncContextTask(taskKey, asyncResultCallback, callable);

            asyncRequestTask.addCallbackAsyncContext(asyncContext);

            getExecutor().execute(asyncRequestTask);

            runningTaskMap.put(taskKey, asyncRequestTask);
        } else {
            asyncRequestTask.addCallbackAsyncContext(asyncContext);
        }
    }


    public static synchronized void removeTask(String taskKey) {
        runningTaskMap.remove(taskKey);
    }


    public static void setExecutor(Executor execPools) {
        AsyncContextTaskManager.execPools = execPools;
    }


    private static Executor getExecutor() {
        if (AsyncContextTaskManager.execPools == null) {
            AsyncContextTaskManager.execPools = new ThreadPoolExecutor(10, 100,  5L, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(10000));
        }
        return AsyncContextTaskManager.execPools;
    }

    public static String toTaskKey(Method method, AsyncMergeMethod unionMethodCall, Object[] params) {

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


}
