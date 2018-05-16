package cn.ubibi.jettyboot.framework.jdbc.model;

import cn.ubibi.jettyboot.framework.jdbc.ConnectionFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class SingleConnectionFactory implements ConnectionFactory {

    private Connection connection;

    public SingleConnectionFactory(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.connection;
    }
}
