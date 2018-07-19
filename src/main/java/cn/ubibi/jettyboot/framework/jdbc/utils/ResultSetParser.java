package cn.ubibi.jettyboot.framework.jdbc.utils;

import java.sql.ResultSet;
import java.util.List;

public interface ResultSetParser<T> {
    List<T> parseResultSet(ResultSet resultSet) throws Exception;
}
