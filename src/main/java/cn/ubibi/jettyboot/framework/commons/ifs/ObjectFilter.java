package cn.ubibi.jettyboot.framework.commons.ifs;

public interface ObjectFilter<T> {
    boolean isOK(T obj);
}