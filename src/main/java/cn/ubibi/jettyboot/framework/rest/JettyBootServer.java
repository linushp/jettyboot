package cn.ubibi.jettyboot.framework.rest;


import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JettyBootServer{

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

    private String controllerContext;


    public JettyBootServer(){
        this("/");
    }


    public JettyBootServer(String controllerContext){
        this.controllerContext = controllerContext;
        LOGGER.info(bannerString);
    }


    public JettyBootServer addContextHandler(ContextHandler contextHandler){
        this.handlerCollection.addHandler(contextHandler);
        return this;
    }


    public JettyBootServer doScanPackage(Class mainServerClass) throws Exception {
        ControllerContextHandler controllerContextHandler = new ControllerContextHandler(controllerContext);
        addContextHandler(controllerContextHandler);

        String packageName = mainServerClass.getPackage().getName();
        PackageScannerUtils.addByPackageScanner(packageName,controllerContextHandler,this);


        controllerContextHandler.addController("/script_dwr_controller",new DefaultDwrScriptController());

        return this;
    }



    public void listen(int port) throws Exception {

        Server server = new Server(port);
        server.setHandler(handlerCollection);
        server.start();
        server.join();
    }

}
