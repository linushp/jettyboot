package cn.ubibi.jettyboot.framework.ioc;

import cn.ubibi.jettyboot.framework.commons.FrameworkConfig;
import cn.ubibi.jettyboot.framework.jdbc.utils.Transactional;
import cn.ubibi.jettyboot.framework.commons.cache.CacheAnnotationUtils;
import cn.ubibi.jettyboot.framework.jdbc.ConnectionFactory;
import cn.ubibi.jettyboot.framework.jdbc.utils.TransactionUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ServiceProxyHandler implements InvocationHandler {



    private Object realServiceObject;

    public ServiceProxyHandler(Object realServiceObject) {
        this.realServiceObject = realServiceObject;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] paramsObjects) throws Throwable {

        //先从缓存里取
        Object invokeResult = CacheAnnotationUtils.getResultFromCacheAnnotation(method, paramsObjects);
        if (invokeResult == null) {
            invokeResult = handleTransactional(method, paramsObjects);
            CacheAnnotationUtils.saveResultToCacheAnnotation(method, paramsObjects, invokeResult);
        }

        return invokeResult;
    }


    //处理事务Transactional注解
    private Object handleTransactional(Method method, Object[] paramsObjects) throws Exception {

        Object invokeResult;
        Transactional trans = method.getDeclaredAnnotation(Transactional.class);
        if (trans != null) {
            String connectionFactoryName = trans.connectionFactoryName();
            //前置条件，徐涛在启动时，执行FrameworkConfig.getInstance().addConnectionFactory
            ConnectionFactory connectionFactory = FrameworkConfig.getInstance().getConnectionFactory(connectionFactoryName);
            if (connectionFactory == null) {
                throw new Exception("failed to getConnectionFactory, name is " + connectionFactoryName);
            }
            TransactionUtil.beginTransaction(connectionFactory);
            try {
                invokeResult = handleOther(method, paramsObjects);
                TransactionUtil.commitTransaction();
            } catch (Exception e) {
                TransactionUtil.rollbackTransaction();
                throw e;
            } finally {
                TransactionUtil.endTransaction();
            }

        } else {
            invokeResult = handleOther(method, paramsObjects);
        }

        return invokeResult;
    }


    private Object handleOther(Method method, Object[] paramsObjects) throws Exception {
        Object invokeResult = method.invoke(this.realServiceObject, paramsObjects);
        return invokeResult;
    }



}
