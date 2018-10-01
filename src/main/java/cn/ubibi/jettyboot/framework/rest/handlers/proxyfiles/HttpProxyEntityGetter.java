package cn.ubibi.jettyboot.framework.rest.handlers.proxyfiles;

import java.util.List;
import java.util.Map;

public interface HttpProxyEntityGetter {
    List<HttpProxyEntity> getHttpProxyEntityList();

    String wrapperHttpTargetPath(String http_target_path, Map<String, String[]> parameterMap);
}
