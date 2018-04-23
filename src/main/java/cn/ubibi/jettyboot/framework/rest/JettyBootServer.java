package cn.ubibi.jettyboot.framework.rest;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;

public class JettyBootServer{


    private HandlerCollection handlerCollection = new HandlerCollection();

    private String controllerContext;


    public JettyBootServer(){
        this("/");
    }


    public JettyBootServer(String controllerContext){
        this.controllerContext = controllerContext;
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
        return this;
    }



    public void listen(int port) throws Exception {
        Server server = new Server(port);
        server.setHandler(handlerCollection);
        server.start();
        server.join();
    }

}
