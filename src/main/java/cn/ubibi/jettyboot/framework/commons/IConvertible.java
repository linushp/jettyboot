package cn.ubibi.jettyboot.framework.commons;

import java.util.Map;

public interface IConvertible {
    public void convertFrom(Object object, Map<String, Object> map);
}
