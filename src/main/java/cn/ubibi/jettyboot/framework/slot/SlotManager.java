package cn.ubibi.jettyboot.framework.slot;

import cn.ubibi.jettyboot.framework.rest.ifs.ControllerAspect;
import cn.ubibi.jettyboot.framework.rest.ifs.HttpParsedRequestFactory;
import cn.ubibi.jettyboot.framework.rest.ifs.HttpPathComparator;
import cn.ubibi.jettyboot.framework.rest.ifs.MethodArgumentResolver;
import cn.ubibi.jettyboot.framework.rest.impl.DefaultHttpParsedRequestFactory;
import cn.ubibi.jettyboot.framework.rest.impl.DefaultHttpPathComparator;

import java.util.ArrayList;
import java.util.List;

public class SlotManager {

    private static final SlotManager instance = new SlotManager();

    public static SlotManager getInstance() {
        return instance;
    }


    private final List<ControllerAspect> controllerAspects = new ArrayList<>();
    private final List<MethodArgumentResolver> methodArgumentResolverList = new ArrayList<>();
    private HttpPathComparator httpPathComparator = new DefaultHttpPathComparator();
    private HttpParsedRequestFactory httpParsedRequestFactory = new DefaultHttpParsedRequestFactory();


    public List<MethodArgumentResolver> getMethodArgumentResolverList() {
        return methodArgumentResolverList;
    }

    public List<ControllerAspect> getControllerAspects() {
        return controllerAspects;
    }


    public void setHttpParsedRequestFactory(HttpParsedRequestFactory httpParsedRequestFactory) {
        assertNotNull(httpParsedRequestFactory);
        this.httpParsedRequestFactory = httpParsedRequestFactory;
    }

    public HttpParsedRequestFactory getHttpParsedRequestFactory() {
        return httpParsedRequestFactory;
    }

    public void setHttpPathComparator(HttpPathComparator httpPathComparator) {
        assertNotNull(httpPathComparator);
        this.httpPathComparator = httpPathComparator;
    }

    public HttpPathComparator getHttpPathComparator() {
        return httpPathComparator;
    }

    private void assertNotNull(Object object) {
        if (object == null) {
            throw new NullPointerException("params can not null");
        }
    }
}