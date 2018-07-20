package cn.ubibi.jettyboot.framework.rest.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import java.io.IOException;

public class AsyncContextListener implements AsyncListener {

    private static final Logger logger = LoggerFactory.getLogger(AsyncContextListener.class);

    private void println(String ss){
        logger.info(ss);
    }


    @Override
    public void onComplete(AsyncEvent event) throws IOException {
        println("onComplete:" + event.toString());
    }

    @Override
    public void onTimeout(AsyncEvent event) throws IOException {
        println("onTimeout:" + event.toString());
    }

    @Override
    public void onError(AsyncEvent event) throws IOException {
        println("onError:" + event.toString());
    }

    @Override
    public void onStartAsync(AsyncEvent event) throws IOException {
        println("onStartAsync:" + event.toString());
    }
}
