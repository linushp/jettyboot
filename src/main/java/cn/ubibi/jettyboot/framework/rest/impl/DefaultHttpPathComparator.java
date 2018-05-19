package cn.ubibi.jettyboot.framework.rest.impl;

import cn.ubibi.jettyboot.framework.commons.CollectionUtils;
import cn.ubibi.jettyboot.framework.rest.ifs.HttpPathComparator;

import java.util.HashMap;
import java.util.List;

public class DefaultHttpPathComparator implements HttpPathComparator {

    //提高对象重复利用
    private static final HashMap<String, List<String>> targetPathSplitCache = new HashMap<>();

    @Override
    public boolean isMatch(String targetPath, String requestPathInfo) {
        if (targetPath.equals(requestPathInfo)) {
            return true;
        }

        //   /user/abc
        //   /user/:id
        //   /user/{name}/3232
        //   /user/23332
        List<String> path1Array = splitTargetPath(targetPath);
        List<String> path2Array = CollectionUtils.removeEmptyString(requestPathInfo.split("/"));
        if (path1Array.size() != path2Array.size()) {
            return false;
        }

        for (int i = 0; i < path1Array.size(); i++) {
            String pp1 = path1Array.get(i);
            String pp2 = path2Array.get(i);
            if (!isPathEquals(pp1, pp2)) {
                return false;
            }
        }

        return true;
    }


    private List<String> splitTargetPath(String targetPath) {
        List<String> result = targetPathSplitCache.get(targetPath);
        if (result == null) {
            result = CollectionUtils.removeEmptyString(targetPath.split("/"));
            targetPathSplitCache.put(targetPath, result);
        }
        return result;
    }


    private boolean isPathEquals(String pp1, String pp2) {
        if (pp1.equals(pp2)) {
            return true;
        }

        /**
         * 支持两种形式的
         * @see DefaultHttpParsedRequest
         */
        if (pp1.startsWith(":") || (pp1.startsWith("{") && pp1.endsWith("}"))) {
            return true;
        }

        return false;
    }
}
