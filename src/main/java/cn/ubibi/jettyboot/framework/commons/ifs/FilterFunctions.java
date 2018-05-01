package cn.ubibi.jettyboot.framework.commons.ifs;

public interface FilterFunctions {

    /**
     * 判断是否等是一个ID字符串中允许出现的字符
     * @param cc
     * @return
     */
    boolean isLegalStringIdChar(char cc);
}
