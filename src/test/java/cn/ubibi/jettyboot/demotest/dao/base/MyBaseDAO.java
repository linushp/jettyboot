package cn.ubibi.jettyboot.demotest.dao.base;

import cn.ubibi.jettyboot.framework.jdbc.DAO;

public class MyBaseDAO<T> extends DAO<T>{

    public MyBaseDAO(Class<T> clazz, String tableName) {
        super(clazz, tableName, MyConnectionFactory.getInstance());
    }

}
