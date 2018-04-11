package cn.ubibi.jettyboot.framework.rest;

import org.eclipse.jetty.server.Server;

public class JettyServer extends RestContextHandler {

    public JettyServer(String context) {
        super(context);
    }

    public JettyServer() {
        super("/");
    }


    public void listen(int port) throws Exception {
        Server server = new Server(port);
        server.setHandler(this);
        server.start();
        server.join();
    }

}
