package cn.ubibi.jettyboot.framework.jdbc.utils;

import cn.ubibi.jettyboot.framework.jdbc.ConnectionFactory;
import cn.ubibi.jettyboot.framework.jdbc.DataAccessObject;
import cn.ubibi.jettyboot.framework.jdbc.model.SqlSession;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionUtil {

    private static ThreadLocal<SqlSession> localSqlSession = new ThreadLocal<>();


    public static void beginTransaction(ConnectionFactory connectionFactory) throws SQLException {
        Connection connection = connectionFactory.getConnection();
        connection.setAutoCommit(false);
        localSqlSession.set(new SqlSession(connection, false));
    }


    public static void rollbackTransaction() throws SQLException {
        SqlSession sqlConnection = localSqlSession.get();
        Connection connection = sqlConnection.getConnection();
        connection.rollback();
    }

    public static void commitTransaction() throws SQLException {
        SqlSession sqlConnection = localSqlSession.get();
        Connection connection = sqlConnection.getConnection();
        connection.commit();
    }

    public static void endTransaction() throws SQLException {
        SqlSession sqlConnection = localSqlSession.get();
        Connection connection = sqlConnection.getConnection();
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        localSqlSession.set(null);
    }


    public static SqlSession getSqlSession() {
        return localSqlSession.get();
    }

}
