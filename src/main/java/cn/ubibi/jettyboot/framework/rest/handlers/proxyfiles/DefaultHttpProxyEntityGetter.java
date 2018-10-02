package cn.ubibi.jettyboot.framework.rest.handlers.proxyfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DefaultHttpProxyEntityGetter implements HttpProxyEntityGetter {

    private String ossDomain;
    private String uploadFolder;

    public DefaultHttpProxyEntityGetter(String ossDomain, String uploadFolder) {
        this.ossDomain = ossDomain;
        this.uploadFolder = uploadFolder;
    }

    @Override
    public List<HttpProxyEntity> getHttpProxyEntityList() {

        String proxyTargetPath = ossDomain + "/" + uploadFolder + "/";

        HttpProxyEntity httpProxyEntity1 = new HttpProxyEntity();
        httpProxyEntity1.setPath("/");
        httpProxyEntity1.setTarget(proxyTargetPath);  //形如：  http://www.baidu.com/upload/


        List<HttpProxyEntity> result = new ArrayList<>();
        result.add(httpProxyEntity1);

        return result;
    }

    @Override
    public String wrapperHttpTargetPath(String http_target_path, Map<String, String[]> parameterMap) {
        return http_target_path;
    }


}
