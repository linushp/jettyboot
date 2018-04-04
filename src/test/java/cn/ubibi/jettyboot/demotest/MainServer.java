package cn.ubibi.jettyboot.demotest;

import cn.ubibi.jettyboot.demotest.controller.MyExceptionHandler;
import cn.ubibi.jettyboot.demotest.controller.UserController;
import cn.ubibi.jettyboot.demotest.dao.base.MyConnectionFactory;
import cn.ubibi.jettyboot.demotest.servlets.HelloServlet;
import cn.ubibi.jettyboot.framework.rest.IRestMethodAspect;
import cn.ubibi.jettyboot.framework.rest.ReqParams;
import cn.ubibi.jettyboot.framework.rest.RestContextHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import java.lang.reflect.Method;


public class MainServer {

    private static Logger logger = Log.getLogger(MainServer.class);


    public static void main(String[] args) throws Exception {

        long t1 = System.currentTimeMillis();

        MyConnectionFactory.getInstance().init();


        Server server = new Server(8001);
        RestContextHandler context = new RestContextHandler("/api");

        context.addController("/user",new UserController());
        context.addServlet("/hello*",new HelloServlet());
        context.addMethodAspect(new IRestMethodAspect() {

            @Override
            public void invokeBefore(Method method, ReqParams reqParams) throws Exception {
                System.out.println(method.getName());
            }

            @Override
            public void invokeAfter(Method method, ReqParams reqParams, Object invokeResult) throws Exception {
                System.out.println(method.getName());
            }

        });
        context.addExceptionHandler(new MyExceptionHandler());



//
//        DefaultSessionIdManager idManager = new DefaultSessionIdManager(server);
//        server.setSessionIdManager(idManager);
//
//
//        SessionHandler sessions = new SessionHandler();
//        sessions.setHandler(restHandler);
//
//
//        ContextHandler context = new ContextHandler("/api");
//        context.setHandler(restHandler);




        server.setHandler(context);
        server.start();

        long t2 = System.currentTimeMillis();

        logger.info("Server Started success , cost time " + (t2 - t1) + " ms");
        server.join();

    }


}
