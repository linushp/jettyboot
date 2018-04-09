package cn.ubibi.jettyboot.framework.jdbc;

import cn.ubibi.jettyboot.framework.commons.BeanUtils;
import cn.ubibi.jettyboot.framework.commons.PropertiesUtils;
import javax.sql.DataSource;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;


public class JBDataUtils {





    public static DataSource createComboPooledDataSource(String propertiesPath){
        Properties p = null;
        try {
            p = PropertiesUtils.getProperties(propertiesPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String,String> m = PropertiesUtils.toMap(p);

        try {
           return createComboPooledDataSource(m);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }




    public static DataSource createComboPooledDataSource(Map<String,String> p) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {


        Class<?> dataSourceClass = Class.forName("com.mchange.v2.c3p0.ComboPooledDataSource");

        DataSource dataSource = (DataSource) dataSourceClass.newInstance();

        Method m_setDriverClass = dataSourceClass.getMethod("setDriverClass", String.class);
        m_setDriverClass.setAccessible(true);
        m_setDriverClass.invoke(dataSource,p.get("driverClass"));


        Method m_setJdbcUrl = dataSourceClass.getMethod("setJdbcUrl", String.class);
        m_setJdbcUrl.setAccessible(true);
        m_setJdbcUrl.invoke(dataSource,p.get("jdbcUrl"));


        Method m_setUser = dataSourceClass.getMethod("setUser", String.class);
        m_setUser.setAccessible(true);
        m_setUser.invoke(dataSource,p.get("user"));


        Method setPassword = dataSourceClass.getMethod("setPassword", String.class);
        setPassword.setAccessible(true);
        setPassword.invoke(dataSource,p.get("password"));


        Method setInitialPoolSize = dataSourceClass.getMethod("setInitialPoolSize", int.class);
        setInitialPoolSize.setAccessible(true);
        setInitialPoolSize.invoke(dataSource,Integer.parseInt(p.get("initialPoolSize")));

        Method setMaxPoolSize = dataSourceClass.getMethod("setMaxPoolSize", int.class);
        setMaxPoolSize.setAccessible(true);
        setMaxPoolSize.invoke(dataSource,Integer.parseInt(p.get("maxPoolSize")));


        Method setMinPoolSize = dataSourceClass.getMethod("setMinPoolSize", int.class);
        setMinPoolSize.setAccessible(true);
        setMinPoolSize.invoke(dataSource,Integer.parseInt(p.get("minPoolSize")));


        Method setMaxStatements = dataSourceClass.getMethod("setMaxStatements", int.class);
        setMaxStatements.setAccessible(true);
        setMaxStatements.invoke(dataSource,Integer.parseInt(p.get("maxStatements")));



        return dataSource;
    }





}
