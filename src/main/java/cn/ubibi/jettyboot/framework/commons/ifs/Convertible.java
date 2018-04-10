package cn.ubibi.jettyboot.framework.commons.ifs;

import java.util.Map;

public interface Convertible {
    void convertFrom(Object object, Map<String, Object> map);
}