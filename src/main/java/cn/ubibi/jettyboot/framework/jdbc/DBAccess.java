package cn.ubibi.jettyboot.framework.jdbc;

import cn.ubibi.jettyboot.framework.commons.BeanUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class DBAccess {

    private DataSource dataSource;
    private Connection connection;
    private boolean autoClose;

    public DBAccess(DataSource dataSource) {
        this.dataSource = dataSource;
        this.autoClose = true;
    }

    public DBAccess(Connection connection) {
        this.connection = connection;
        this.autoClose = false;
    }


    public static DBAccess use(Connection connection){
        return new DBAccess(connection);
    }


    // INSERT, UPDATE, DELETE 操作都可以包含在其中
    public void update(String sql, Object... args) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);

            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i + 1, args[i]);
            }

            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            release(null, preparedStatement, connection);
        }
    }




    /**
     * 返回某条记录的某一个字段的值 或 一个统计的值(一共有多少条记录等.)
     * @param sql
     * @param args
     * @param <E>
     * @return
     */
    public <E> E queryValue(String sql, Object... args) {

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
            ex.printStackTrace();
        } finally {
            release(resultSet, preparedStatement, connection);
        }

        //2. 取得结果
        return null;
    }


    // 查询一条记录, 返回对应的对象
    public <T> T queryObject(Class<T> clazz, String sql, Object... args) throws Exception {
        List<T> result = query(clazz, sql, args);
        if (result.size() > 0) {
            return result.get(0);
        }

        return null;
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
        List<Map<String, Object>> mapList = this.query(sql, args);
        return BeanUtils.mapListToBeanList(clazz,mapList);
    }


    /**
     * 传入 SQL 语句， 返回 SQL 语句查询到的记录对应的 Map对象的集合
     * @param sql
     * @param args
     * @return
     * @throws Exception
     */
    public List<Map<String,Object>> query(String sql, Object... args) throws Exception {
        List<Map<String,Object>> list;
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
    private List<Map<String, Object>> resultSetToMapList(
            ResultSet resultSet) throws SQLException {
        // 5. 准备一个 List<Map<String, Object>>:
        // 键: 存放列的别名, 值: 存放列的值. 其中一个 Map 对象对应着一条记录
        List<Map<String, Object>> values = new ArrayList<>();

        List<String> columnLabels = getColumnLabels(resultSet);
        Map<String, Object> map = null;

        // 7. 处理 ResultSet, 使用 while 循环
        while (resultSet.next()) {
            map = new HashMap<>();

            for (String columnLabel : columnLabels) {
                Object value = resultSet.getObject(columnLabel);
                map.put(columnLabel, value);
            }

            // 11. 把一条记录的一个 Map 对象放入 5 准备的 List 中
            values.add(map);
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

        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(true);
        return connection;
    }


    private void release(ResultSet resultSet, Statement statement, Connection connection) {

        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        //使用Connection创建的实例不能自动关闭
        //只能自动关闭dataSource创建的connection
        if (this.autoClose) {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}