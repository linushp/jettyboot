package cn.ubibi.jettyboot.framework.jdbc;

import cn.ubibi.jettyboot.framework.commons.CastBasicTypeUtils;
import cn.ubibi.jettyboot.framework.jdbc.model.UpdateResult;
import cn.ubibi.jettyboot.framework.jdbc.utils.ResultSetParser;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public abstract class DataAccessAbstract<T> implements ResultSetParser<T> {
    private DataAccess dataAccess;

    public DataAccessAbstract(ConnectionFactory connectionFactory) {
        this.dataAccess = new DataAccess(connectionFactory);
        this.dataAccess.setResultSetParser(this);
    }

    public UpdateResult update(String sql, Object... args) throws Exception {
        return dataAccess.update(sql, args);
    }

    public T queryObject(String sql, Object... args) throws Exception {
        return dataAccess.queryObject(null, sql, args);
    }

    public List<T> queryObjects(String sql, Object... args) throws Exception {
        return dataAccess.query(null, sql, args);
    }

    public long queryValue(String sql, Object... args) throws Exception {
        Object x = dataAccess.queryValue(sql, args);
        return CastBasicTypeUtils.toLong(x);
    }

    @Override
    public List<T> parseResultSet(ResultSet resultSet) throws Exception {
        List<T> result = new ArrayList<>();
        while (resultSet.next()) {
            T obj = this.parse(resultSet);
            result.add(obj);
        }
        return result;
    }

    abstract public T parse(ResultSet resultSet);
}
