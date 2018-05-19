package cn.ubibi.jettyboot.framework.rest.ifs;

import cn.ubibi.jettyboot.framework.rest.model.MethodArgument;

public interface MethodArgumentResolver {

    boolean isSupport(MethodArgument methodArgument);

    Object resolveArgument(MethodArgument methodArgument, HttpParsedRequest request);

}