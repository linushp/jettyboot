package cn.ubibi.jettyboot.framework.rest;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;

public class JettyBootServer{


    private HandlerCollection handlerCollection = new HandlerCollection();

    private Class mainServerClass;
    private String controllerContext;


    public JettyBootServer(Class mainServerClass){
        this(mainServerClass,"/");
    }


    public JettyBootServer(Class mainServerClass,String controllerContext){
        this.mainServerClass = mainServerClass;
        this.controllerContext = controllerContext;
    }


    public void addContextHandler(ContextHandler contextHandler){
        this.handlerCollection.addHandler(contextHandler);
    }


    public void doScanPackage() throws Exception {
        ControllerContextHandler controllerContextHandler = new ControllerContextHandler(controllerContext);
        addContextHandler(controllerContextHandler);

        String packageName = mainServerClass.getPackage().getName();
        PackageScannerUtils.addByPackageScanner(packageName,controllerContextHandler,this);
    }



    public void listen(int port) throws Exception {
        Server server = new Server(port);
        server.setHandler(handlerCollection);
        server.start();
        server.join();
    }

}
