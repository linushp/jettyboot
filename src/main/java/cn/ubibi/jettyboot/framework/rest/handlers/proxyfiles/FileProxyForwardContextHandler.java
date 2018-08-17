package cn.ubibi.jettyboot.framework.rest.handlers.proxyfiles;


import org.eclipse.jetty.server.handler.ContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileProxyForwardContextHandler extends ContextHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileProxyForwardContextHandler.class);

    public FileProxyForwardContextHandler(String contextPath, HttpProxyEntityGetter httpProxyEntityGetter) {
        super(contextPath);
        FileProxyForwardHandler proxyForwardHandler = new FileProxyForwardHandler(httpProxyEntityGetter);
        this.setHandler(proxyForwardHandler);
    }

}

