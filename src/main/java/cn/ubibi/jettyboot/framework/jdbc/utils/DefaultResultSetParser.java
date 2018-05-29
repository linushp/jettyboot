package cn.ubibi.jettyboot.framework.jdbc.utils;

import cn.ubibi.jettyboot.framework.commons.BeanUtils;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

public class DefaultResultSetParser<T> implements ResultSetParser<T> {

    private Class<T> clazz;


    public DefaultResultSetParser(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public List<T> parseResultSet(ResultSet resultSet) throws Exception {
        List<Map<String, ?>> mapList = ResultSetUtils.resultSetToMapList(resultSet);
        return BeanUtils.mapListToBeanList(this.clazz, mapList);
    }
}
