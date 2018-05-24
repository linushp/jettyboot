package cn.ubibi.jettyboot.framework.rest;


import cn.ubibi.jettyboot.framework.commons.FrameworkConfig;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JettyBootServer {

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

    private String context = "/";
    private ControllerContextHandler controllerContextHandler;


    public JettyBootServer() {
    }

    public JettyBootServer setServerName(String name) {
        FrameworkConfig.getInstance().setResponseServerName(name);
        return this;
    }

    public JettyBootServer setContext(String context) {
        this.context = context;
        return this;
    }

    public JettyBootServer addContextHandler(ContextHandler contextHandler) {
        this.handlerCollection.addHandler(contextHandler);
        return this;
    }


    public JettyBootServer doScanPackage(Class mainServerClass) throws Exception {

        String packageName = mainServerClass.getPackage().getName();
        ControllerContextHandler controllerContextHandler = this.getControllerContextHandler();

        controllerContextHandler.usingDefaultDwrScript();

        PackageScannerUtils.addByPackageScanner(packageName, controllerContextHandler, this);

        addContextHandler(controllerContextHandler);

        return this;
    }


    public ControllerContextHandler getControllerContextHandler() {
        if (this.controllerContextHandler == null) {
            this.controllerContextHandler = new ControllerContextHandler(this.context);
        }
        return this.controllerContextHandler;
    }


    public void listen(int port) throws Exception {

        LOGGER.info(bannerString);

        Server server = new Server(port);
        server.setHandler(handlerCollection);
        server.start();
        server.join();
    }

}
