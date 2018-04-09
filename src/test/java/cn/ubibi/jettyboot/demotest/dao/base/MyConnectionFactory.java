package cn.ubibi.jettyboot.demotest.dao.base;

import cn.ubibi.jettyboot.framework.jdbc.JBDataAccess;
import cn.ubibi.jettyboot.framework.jdbc.JBDataUtils;
import cn.ubibi.jettyboot.framework.jdbc.JBConnectionFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class MyConnectionFactory implements JBConnectionFactory {

    private static MyConnectionFactory ourInstance = new MyConnectionFactory();

    public static MyConnectionFactory getInstance() {
        return ourInstance;
    }

    private DataSource dataSource ;



    private MyConnectionFactory() {
    }


    public void init(){
        this.dataSource = JBDataUtils.createComboPooledDataSource("c3p0.properties");
        try {
            Connection conn = this.dataSource.getConnection();
            JBDataAccess jbDataAccess = JBDataAccess.use(conn);
            jbDataAccess.query("SELECT now()");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
