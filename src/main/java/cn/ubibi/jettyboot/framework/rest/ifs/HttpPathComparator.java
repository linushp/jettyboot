package cn.ubibi.jettyboot.framework.rest.ifs;

public interface HttpPathComparator {
    boolean isMatch(String targetPath, String requestPathInfo);
}
