package cn.ubibi.jettyboot.demotest.dao.base;

import cn.ubibi.jettyboot.framework.jdbc.JBDataAccessObject;

public class MyDAO<T> extends JBDataAccessObject<T> {

    public MyDAO(Class<T> clazz, String tableName) {
        super(clazz, tableName, MyConnectionFactory.getInstance());
    }

}
