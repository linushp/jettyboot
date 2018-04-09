package cn.ubibi.jettyboot.framework.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

public interface JBConnectionFactory {
    Connection getConnection() throws SQLException;
}
