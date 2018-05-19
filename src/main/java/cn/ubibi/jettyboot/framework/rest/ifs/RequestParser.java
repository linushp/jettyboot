package cn.ubibi.jettyboot.framework.rest.ifs;

public interface RequestParser {
    void doParse(HttpParsedRequest request) throws Exception;
}
