package cn.ubibi.jettyboot.demotest;

import cn.ubibi.jettyboot.demotest.controller.UserController;
import cn.ubibi.jettyboot.framework.rest.RestHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;


public class MainServer {

    private static Logger logger = Log.getLogger(MainServer.class);


    public static void main(String[] args) throws Exception {

        long t1 = System.currentTimeMillis();

        RestHandler restHandler = new RestHandler();
        restHandler.addController(new UserController());


        Server server = new Server(8001);

//
//        DefaultSessionIdManager idManager = new DefaultSessionIdManager(server);
//        server.setSessionIdManager(idManager);
//
//
//        SessionHandler sessions = new SessionHandler();
//        sessions.setHandler(restHandler);
//
//
        ContextHandler context = new ContextHandler("/");
        context.setHandler(restHandler);


        server.setHandler(context);
//        server.setHandler(restHandler);
        server.start();

        long t2 = System.currentTimeMillis();

        logger.info("Server Started success , cost time " + (t2-t1) + " ms");
        server.join();

    }


}
