package cn.ubibi.jettyboot.demotest.dao;

import cn.ubibi.jettyboot.demotest.entity.UserEntity;
import cn.ubibi.jettyboot.framework.jdbc.DBManager;

public class MyDBManager {

//    //这样做的目的是为了可以使用多个数据源
    public static final DBManager UDB = new DBManager("c3p0.properties");

    public static final DBManager INSTANCE_DB = new DBManager("c3p0.properties");
}
