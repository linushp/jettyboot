package cn.ubibi.jettyboot.framework.jdbc;

import cn.ubibi.jettyboot.framework.commons.BeanUtils;
import cn.ubibi.jettyboot.framework.commons.CollectionUtils;
import cn.ubibi.jettyboot.framework.jdbc.model.SqlNdArgs;
import cn.ubibi.jettyboot.framework.jdbc.model.UpdateResult;
import cn.ubibi.jettyboot.framework.jdbc.utils.SQLFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ConnectException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataAccess {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataAccess.class);


    private ConnectionFactory connectionFactory;
    private Connection connection;
    private boolean autoClose;

    public DataAccess(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        this.autoClose = true;
    }

    public DataAccess(Connection connection) {
        this.connection = connection;
        this.autoClose = false;
    }


    public static DataAccess use(Connection connection) {
        return new DataAccess(connection);
    }


    public UpdateResult update(String sql, List<Object> args) throws ConnectException {
        Object[] objects = args.toArray(new Object[args.size()]);
        return update(sql, objects);
    }


    // INSERT, UPDATE, DELETE 操作都可以包含在其中
    public UpdateResult update(String sql, Object... args) throws ConnectException {

        //允许第一个参数传递过来一个Map，如果第一个参数是一个map，后面其他参数均忽略
        if (args.length > 0 && args[0] instanceof Map) {
            SqlNdArgs sqlNdArgs = SQLFormatUtils.formatSQLAndArgs(sql, (Map<String, Object>) args[0]);
            sql = sqlNdArgs.getSql();
            args = sqlNdArgs.getArgs().toArray();
        }


        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet generatedKeyResultSet = null;
        UpdateResult updateResult = new UpdateResult();

        try {
            LOGGER.info("update sql : " + sql);
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);

            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i + 1, args[i]);
            }

            int affectedRows = preparedStatement.executeUpdate();
            updateResult.setAffectedRows(affectedRows);


            generatedKeyResultSet = preparedStatement.getGeneratedKeys();
            List<Map<String, ?>> mapList = resultSetToMapList(generatedKeyResultSet);
            if (mapList != null && !mapList.isEmpty()) {
                for (Map<String, ?> map : mapList) {
                    Object generatedKey = map.get("GENERATED_KEY");
                    if (generatedKey != null) {
                        updateResult.getGeneratedKeys().add(generatedKey);
                        updateResult.setGeneratedKey(generatedKey);
                    }
                }
            }
            return updateResult;

        } catch (java.net.ConnectException ee) {
            throw ee;
        } catch (Exception e) {
            LOGGER.debug("update error ", e);
            updateResult.setErrMsg(e.getMessage());
        } finally {
            release(generatedKeyResultSet, preparedStatement, connection);
        }
        return updateResult;
    }


    public <E> E queryValue(String sql, List<Object> args) throws Exception {
        Object[] objects = args.toArray(new Object[args.size()]);
        return queryValue(sql, objects);
    }


    /**
     * 返回某条记录的某一个字段的值 或 一个统计的值(一共有多少条记录等.)
     *
     * @param sql
     * @param args
     * @param <E>
     * @return
     */
    public <E> E queryValue(String sql, Object... args) throws Exception {

        //允许第一个参数传递过来一个Map，如果第一个参数是一个map，后面其他参数均忽略
        if (args.length > 0 && args[0] instanceof Map) {
            SqlNdArgs sqlNdArgs = SQLFormatUtils.formatSQLAndArgs(sql, (Map<String, Object>) args[0]);
            sql = sqlNdArgs.getSql();
            args = sqlNdArgs.getArgs().toArray();
        }


        LOGGER.info("query sql : " + sql);

        //1. 得到结果集: 该结果集应该只有一行, 且只有一列
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            //1. 得到结果集
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);

            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i + 1, args[i]);
            }

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return (E) resultSet.getObject(1);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            release(resultSet, preparedStatement, connection);
        }

        //2. 取得结果
        return null;
    }


    public <T> T queryObject(Class<T> clazz, String sql, List<Object> args) throws Exception {
        Object[] objects = args.toArray(new Object[args.size()]);
        return queryObject(clazz, sql, objects);
    }


    // 查询一条记录, 返回对应的对象
    public <T> T queryObject(Class<T> clazz, String sql, Object... args) throws Exception {
        List<T> result = query(clazz, sql, args);
        return CollectionUtils.getFirstElement(result);
    }


    public <T> List<T> query(Class<T> clazz, String sql, List<Object> args) throws Exception {
        Object[] objects = args.toArray(new Object[args.size()]);
        return query(clazz, sql, objects);
    }


    /**
     * 传入 SQL 语句和 Class 对象, 返回 SQL 语句查询到的记录对应的 Class 类的对象的集合
     *
     * @param clazz: 对象的类型
     * @param sql:   SQL 语句
     * @param args:  填充 SQL 语句的占位符的可变参数.
     * @return
     */
    public <T> List<T> query(Class<T> clazz, String sql, Object... args) throws Exception {
        List<Map<String, ?>> mapList = this.query(sql, args);
        return BeanUtils.mapListToBeanList(clazz, mapList);
    }


    public List<Map<String, ?>> query(String sql, List<?> args) throws Exception {
        Object[] objects = args.toArray(new Object[args.size()]);
        return query(sql, objects);
    }

    /**
     * 传入 SQL 语句， 返回 SQL 语句查询到的记录对应的 Map对象的集合
     *
     * @param sql
     * @param args
     * @return
     * @throws Exception
     */
    public List<Map<String, ?>> query(String sql, Object... args) throws Exception {


        //允许第一个参数传递过来一个Map，如果第一个参数是一个map，后面其他参数均忽略
        if (args.length > 0 && args[0] instanceof Map) {
            SqlNdArgs sqlNdArgs = SQLFormatUtils.formatSQLAndArgs(sql, (Map<String, Object>) args[0]);
            sql = sqlNdArgs.getSql();
            args = sqlNdArgs.getArgs().toArray();
        }


        List<Map<String, ?>> list;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        LOGGER.info("query sql : " + sql);


        try {
            //1. 得到结果集
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);

            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i + 1, args[i]);
            }

            resultSet = preparedStatement.executeQuery();

            //2. 转换成List<Map>
            list = resultSetToMapList(resultSet);

        } catch (Exception e) {
            throw e;
        } finally {
            release(resultSet, preparedStatement, connection);
        }

        return list;
    }


    /**
     * 传入 SQL 语句， 返回 SQL 语句查询到的记录对应的 Map对象的集合
     *
     * @param sql
     * @return
     * @throws Exception
     */
    public List<Map<String, ?>> queryTemp(String sql) throws Exception {
        List<Map<String, ?>> list;
        Connection connection = null;
        Statement preparedStatement = null;
        ResultSet resultSet = null;

        LOGGER.info("query sql : " + sql);

        try {
            //1. 得到结果集
            connection = getConnection();
            preparedStatement = connection.createStatement();
            resultSet = preparedStatement.executeQuery(sql);

            //2. 转换成List<Map>
            list = resultSetToMapList(resultSet);

        } catch (Exception e) {
            throw e;
        } finally {
            release(resultSet, preparedStatement, connection);
        }

        return list;
    }


    /**
     * 处理结果集, 得到 Map 的一个 List, 其中一个 Map 对象对应一条记录
     *
     * @param resultSet
     * @return
     * @throws SQLException
     */
    private List<Map<String, ?>> resultSetToMapList(ResultSet resultSet) throws SQLException {
        List<Map<String, ?>> values = new ArrayList<>();
        if (resultSet != null) {
            List<String> columnLabels = getColumnLabels(resultSet);
            // 7. 处理 ResultSet, 使用 while 循环
            while (resultSet.next()) {

                Map<String, Object> map = new HashMap<>();

                for (String columnLabel : columnLabels) {
                    Object value = resultSet.getObject(columnLabel);
                    map.put(columnLabel, value);
                }

                // 11. 把一条记录的一个 Map 对象放入 5 准备的 List 中
                values.add(map);
            }
        }

        return values;
    }

    /**
     * 获取结果集的 ColumnLabel 对应的 List
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    private List<String> getColumnLabels(ResultSet rs) throws SQLException {
        List<String> labels = new ArrayList<>();

        ResultSetMetaData rsmd = rs.getMetaData();
        for (int i = 0; i < rsmd.getColumnCount(); i++) {
            labels.add(rsmd.getColumnLabel(i + 1));
        }

        return labels;
    }


    private Connection getConnection() throws Exception {
        if (this.connection != null) {
            return this.connection;
        }


        Connection connection = connectionFactory.getConnection();
        connection.setAutoCommit(true);
        return connection;
    }


    private void release(ResultSet resultSet, Statement statement, Connection connection) {

        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                LOGGER.info("ResultSet close error");
            }
        }

        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                LOGGER.info("Statement close error");
            }
        }


        //使用Connection创建的实例不能自动关闭
        //只能自动关闭dataSource创建的connection
        if (this.autoClose) {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    LOGGER.info("Connection close error");
                }
            }
        }

    }


}