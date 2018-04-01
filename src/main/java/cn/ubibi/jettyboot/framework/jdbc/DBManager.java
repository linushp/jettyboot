package cn.ubibi.jettyboot.framework.jdbc;

import cn.ubibi.jettyboot.framework.commons.PropertiesUtils;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.util.Properties;


public class DBManager {


    private DataSource dataSource;


    public DBManager(String propertiesPath) {
        this.dataSource = this.createDataSource(propertiesPath);
    }


    private DataSource createDataSource(String propertiesPath){
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        Properties p = null;
        try {
            p = PropertiesUtils.getProperties(propertiesPath);
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            dataSource.setDriverClass(p.getProperty("driverClass"));
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }

        dataSource.setJdbcUrl(p.getProperty("jdbcUrl"));
        dataSource.setUser(p.getProperty("user"));
        dataSource.setPassword(p.getProperty("password"));
        dataSource.setInitialPoolSize(Integer.parseInt(p.getProperty("initialPoolSize")));
        dataSource.setMaxPoolSize(Integer.parseInt(p.getProperty("maxPoolSize")));
        dataSource.setMinPoolSize(Integer.parseInt(p.getProperty("minPoolSize")));
        dataSource.setMaxStatements(Integer.parseInt(p.getProperty("maxStatements")));

        return dataSource;
    }


    public Connection getDataSourceConnection() throws Exception {
        return dataSource.getConnection();
    }

    public DataSource getDataSource(){
        return this.dataSource;
    }

    public DBAccess getDBAccess(){
        return new DBAccess(this.dataSource);
    }


}
