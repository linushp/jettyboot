package cn.ubibi.jettyboot.framework.rest;


import cn.ubibi.jettyboot.framework.commons.FrameworkConfig;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;


/**
 * 对于Session管理,可以参考 SessionHandler.doStart 方法
 * <p>
 * 默认情况下使用了： DefaultSessionCache , NullSessionDataStore , DefaultSessionIdManager
 * <p>
 * 用户可以通过使用 Server.addBean 自定义它的实现方式。
 */
public class JettyBootServer extends Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(JettyBootServer.class);

    private static final String bannerString = "" +
            "\n\n\n" +
            "      #  #######  #######  #######  #     #  ######   #######  #######  #######\n" +
            "      #  #           #        #      #   #   #     #  #     #  #     #     #\n" +
            "      #  #           #        #       # #    #     #  #     #  #     #     #\n" +
            "      #  #####       #        #        #     ######   #     #  #     #     #\n" +
            "#     #  #           #        #        #     #     #  #     #  #     #     #\n" +
            "#     #  #           #        #        #     #     #  #     #  #     #     #\n" +
            " #####   #######     #        #        #     ######   #######  #######     #" +
            "\n\n";

    private HandlerCollection handlerCollection = new HandlerCollection();

    private SessionHandler controllerSessionHandler = null;
    private String controllerContext = "/";
    private ControllerContextHandler controllerContextHandler;


    public JettyBootServer(int port) {
        super(port);
        this.init();
    }

    public JettyBootServer(InetSocketAddress addr) {
        super(addr);
        this.init();
    }

    public JettyBootServer(ThreadPool pool) {
        super(pool);
        this.init();
    }


    private void init() {
        SessionHandler sessionHandler = new SessionHandler();
        sessionHandler.setMaxInactiveInterval(30 * 60); //30 分钟
        this.controllerSessionHandler = sessionHandler; //默认的
    }


    public void setServerName(String name) {
        FrameworkConfig.getInstance().setResponseServerName(name);
    }


    public void setControllerContext(String controllerContext) {
        this.controllerContext = controllerContext;
    }

    public void addContextHandler(ContextHandler contextHandler) {
        this.handlerCollection.addHandler(contextHandler);
    }

    public void addHandler(Handler handler) {
        this.handlerCollection.addHandler(handler);
    }

    public void doScanPackage(Class mainServerClass) throws Exception {

        String packageName = mainServerClass.getPackage().getName();

        ControllerContextHandler controllerContextHandler = this.getControllerContextHandler();

        controllerContextHandler.usingDefaultDwrScript();

        PackageScannerUtils.addByPackageScanner(packageName, controllerContextHandler, this);

        addContextHandler(controllerContextHandler);
    }


    public void setControllerSessionHandler(SessionHandler sessionHandler) {
        this.controllerSessionHandler = sessionHandler;
    }


    public ControllerContextHandler getControllerContextHandler() {
        if (this.controllerContextHandler == null) {
            this.controllerContextHandler = new ControllerContextHandler(this.controllerContext, this.controllerSessionHandler);
        }
        return this.controllerContextHandler;
    }


    public void startAndJoin() throws Exception {
        this.setHandler(handlerCollection);
        this.start();
        this.join();
    }

}
