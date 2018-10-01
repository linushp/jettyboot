package cn.ubibi.jettyboot.framework.rest.handlers.proxyfiles;

import java.util.List;

public interface HttpProxyEntityGetter {
    List<HttpProxyEntity> getHttpProxyEntityList();

    String wrapperHttpTargetPath(String http_target_path);
}
