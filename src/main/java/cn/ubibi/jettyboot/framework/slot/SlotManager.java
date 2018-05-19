package cn.ubibi.jettyboot.framework.slot;

import cn.ubibi.jettyboot.framework.rest.ifs.ControllerAspect;
import cn.ubibi.jettyboot.framework.rest.ifs.HttpParsedRequestFactory;
import cn.ubibi.jettyboot.framework.rest.ifs.MethodArgumentResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SlotManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(SlotManager.class);
    private static final SlotManager instance = new SlotManager();

    public static SlotManager getInstance() {
        return instance;
    }


    private HttpParsedRequestFactory httpParsedRequestFactory;
    private List<ControllerAspect> controllerAspects = new ArrayList<>();
    private List<MethodArgumentResolver> methodArgumentResolverList = new ArrayList<>();


    public List<MethodArgumentResolver> getMethodArgumentResolverList() {
        return methodArgumentResolverList;
    }

    public List<ControllerAspect> getControllerAspects() {
        return controllerAspects;
    }

    public void setHttpParsedRequestFactory(HttpParsedRequestFactory httpParsedRequestFactory) {
        this.httpParsedRequestFactory = httpParsedRequestFactory;
    }

    public HttpParsedRequestFactory getHttpParsedRequestFactory() {
        return httpParsedRequestFactory;
    }
}
