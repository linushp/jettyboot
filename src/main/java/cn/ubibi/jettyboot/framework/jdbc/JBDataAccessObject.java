package cn.ubibi.jettyboot.framework.jdbc;

import cn.ubibi.jettyboot.framework.commons.*;
import cn.ubibi.jettyboot.framework.jdbc.model.UpdateResult;

import java.sql.Connection;
import java.util.*;


public class JBDataAccessObject<T> {

    private Class<T> clazz;
    private String tableName;
    private String schemaName;
    private JBDataAccess JBDataAccess;


    public JBDataAccessObject(Class<T> clazz, String tableName, JBConnectionFactory connectionFactory) {
        this.clazz = clazz;
        this.tableName = tableName;
        this.schemaName = "";
        this.JBDataAccess = new JBDataAccess(connectionFactory);
    }

    public JBDataAccessObject(Class<T> clazz, String tableName, Connection connection) {
        this.clazz = clazz;
        this.tableName = tableName;
        this.schemaName = "";
        this.JBDataAccess = new JBDataAccess(connection);
    }

    private String schemaTableName() {
        if (schemaName == null || schemaName.isEmpty()) {
            return tableName;
        }
        return schemaName + "." + tableName;
    }

    private JBDataAccessObject() {
    }

    public Object clone() {

        try {
            //获取的是子类的Class
            Object o = this.getClass().newInstance();
            BeanUtils.copyField(o, this);
            return o;

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }


    public JBDataAccessObject<T> use(String tableName) {
        JBDataAccessObject dao = (JBDataAccessObject) this.clone();
        dao.tableName = tableName;
        return dao;
    }

    public JBDataAccessObject<T> useSchema(String schemaName) {
        JBDataAccessObject dao = (JBDataAccessObject) this.clone();
        dao.schemaName = schemaName;
        return dao;
    }

    public JBDataAccessObject<T> use(Connection connection) {
        JBDataAccessObject dao = (JBDataAccessObject) this.clone();
        dao.JBDataAccess = new JBDataAccess(connection);
        return dao;
    }

    public JBDataAccessObject<T> use(JBConnectionFactory connSource) {
        JBDataAccessObject dao = (JBDataAccessObject) this.clone();
        dao.JBDataAccess = new JBDataAccess(connSource);
        return dao;
    }


    public T findById(Object id) throws Exception {
        String sql = "select * from " + schemaTableName() + " where id = ?";
        return JBDataAccess.queryObject(clazz, sql, id);
    }

    public List<T> findAll() throws Exception {
        return findByWhere("");
    }

    public List<T> findByWhere(WhereSqlAndArgs whereSqlAndArgs) throws Exception {
        return findByWhere(whereSqlAndArgs.whereSql, whereSqlAndArgs.whereArgs);
    }


    public List<T> findByWhere(String whereSql, Object... args) throws Exception {
        String sql = "select * from " + schemaTableName() + " " + whereSql;
        return JBDataAccess.query(clazz, sql, args);
    }


    public JBPage<T> findPage(int pageNo, int pageSize) throws Exception {
        return findPage(pageNo, pageSize, "", "");
    }

    /**
     * 分页查询
     *
     * @param pageNo    页号从零开始
     * @param pageSize  每夜多少条数据
     * @param whereSql  条件
     * @param orderBy   排序条件
     * @param whereArgs 条件参数
     * @return 返回Page对象
     * @throws Exception 可能的异常
     */
    public JBPage<T> findPage(int pageNo, int pageSize, String whereSql, String orderBy, Object... whereArgs) throws Exception {

        if (pageNo < 0) {
            pageNo = 0;
        }

        if (pageSize < 0) {
            pageSize = 1;
        }

        if (whereSql == null) {
            whereSql = "";
        }
        if (orderBy == null) {
            orderBy = "";
        }

        int beginIndex = pageNo * pageSize;

        long totalCount = this.countByWhereSql(whereSql, whereArgs);

        //totalCount 为0的时候可以不查询
        List<T> dataList;
        if (totalCount > 0) {
            String sqlList = "select * from " + schemaTableName() + " " + whereSql + " " + orderBy + " limit  " + beginIndex + "," + pageSize;
            dataList = JBDataAccess.query(clazz, sqlList, whereArgs);
        } else {
            dataList = new ArrayList<>();
        }


        JBPage<T> result = new JBPage(dataList, totalCount, pageNo, pageSize);
        return result;
    }


    /**
     * 统计整个表的大小
     *
     * @return 数量
     */
    public Long countAll() {
        return countByWhereSql("");
    }


    /**
     * 统计数量多少
     *
     * @param whereSql  条件
     * @param whereArgs 条件参数
     * @return 数量
     */
    public Long countByWhereSql(String whereSql, Object... whereArgs) {
        String sqlCount = "select count(0) from " + schemaTableName() + " " + whereSql;
        Object totalCount = JBDataAccess.queryValue(sqlCount, whereArgs);
        return (Long) CastTypeUtils.castValueType(totalCount,Long.class);
    }


    /**
     * 根据Id删除
     *
     * @param id bean id
     */
    public UpdateResult deleteById(Object id) {
        return deleteByWhereSql("where id=?", id);
    }

    /**
     * 删除
     *
     * @param whereSql  条件
     * @param whereArgs 参数
     */
    public UpdateResult deleteByWhereSql(String whereSql, Object... whereArgs) {
        String sql = "delete from " + schemaTableName() + " " + whereSql;
        return JBDataAccess.update(sql, whereArgs);
    }


    public UpdateResult updateById(Map<String, Object> newValues, Object id) {
        return updateByWhereSql(newValues, "where id = ? ", id);
    }


    public UpdateResult updateByWhereSql(Map<String, Object> newValues, String whereSql, Object... whereArgs) {
        if (newValues != null && !newValues.isEmpty()) {

            List[] keysValues = CollectionUtils.listKeyValues(newValues);
            List<String> keys = keysValues[0];
            List<Object> values = keysValues[1];
            List<String> keys2 = CollectionUtils.eachWrap(keys, "`", "`=?");
            String setSql = StringUtils.join(keys2, ",");


            String sql = "update " + schemaTableName() + " set " + setSql + " " + whereSql;
            if (whereArgs != null && whereArgs.length > 0) {
                List<Object> whereArgsList = Arrays.asList(whereArgs);
                values.addAll(whereArgsList);
            }

            return JBDataAccess.update(sql, values);
        }
        return new UpdateResult("params is empty");
    }


    /**
     * 在调用此方法时,把它放在同一个事务里，效率会更好
     *
     * @param objectList
     */
    public List<UpdateResult> insertObjectList(List<Map<String, Object>> objectList) {
        List<UpdateResult> results = new ArrayList<>();
        if (objectList != null && !objectList.isEmpty()) {
            for (Map<String, Object> obj : objectList) {
                UpdateResult result = insertObject(obj);
                results.add(result);
            }
        }
        return results;
    }


    public UpdateResult insertObject(Map<String, Object> newValues) {
        if (newValues != null && !newValues.isEmpty()) {

            List[] keysValues = CollectionUtils.listKeyValues(newValues);
            List<String> keys = keysValues[0];
            List<Object> values = keysValues[1];

            List<String> keys2 = CollectionUtils.eachWrap(keys, "`", "`");
            List<String> valuesQuota = CollectionUtils.repeatList("?", values.size());

            String filedSql = StringUtils.join(keys2, ",");
            String valuesSql = StringUtils.join(valuesQuota, ",");

            String sql = "insert into " + schemaTableName() + "(" + filedSql + ") values (" + valuesSql + ")";
            return JBDataAccess.update(sql, values);
        }

        return new UpdateResult("params is empty");
    }


    public UpdateResult largeSqlBatchInsert(List<Map<String, Object>> objectList) {

        objectList = CollectionUtils.removeEmptyMap(objectList);

        if (objectList != null && !objectList.isEmpty()) {

            Set<String> fieldKeys = CollectionUtils.getAllMapKeys(objectList);

            List<String> fieldKeysList = new ArrayList<>(fieldKeys);
            List<String> fieldKeysWList = CollectionUtils.eachWrap(fieldKeysList, "`", "`");
            String filedSql = StringUtils.join(fieldKeysWList, ",");


            List<String> valuesQuota = CollectionUtils.repeatList("?", fieldKeysList.size());
            String valuesSql = "(" + StringUtils.join(valuesQuota, ",") + ")"; // (?,?,?)


            List<String> allValuesSqlList = new ArrayList<>();
            List<Object> allValues = new ArrayList<>();
            for (Map<String, Object> object : objectList) {
                allValuesSqlList.add(valuesSql);
                for (String key : fieldKeysList) {
                    Object value = object.get(key);
                    allValues.add(value);
                }
            }

            String allValuesSql = StringUtils.join(allValuesSqlList, ",");
            String sql = "insert into " + schemaTableName() + " (" + filedSql + ") values " + allValuesSql;

            return JBDataAccess.update(sql, allValues);
        }


        return new UpdateResult("params is empty");
    }




    public UpdateResult saveOrUpdateById(Map<String, Object> newValues, Object id) throws Exception {
        return saveOrUpdate(newValues, "where id = ?", id);
    }


    public UpdateResult saveOrUpdate(Map<String, Object> newValues, String whereSql, Object... whereArgs) throws Exception {
        List<T> findResult = findByWhere(whereSql, whereArgs);
        if (findResult.isEmpty()) {
            return insertObject(newValues);
        } else {
            return updateByWhereSql(newValues, whereSql, whereArgs);
        }
    }


    protected WhereSqlAndArgs toWhereSqlAndArgs(Map<String, Object> condition) {
        List[] keysValues = CollectionUtils.listKeyValues(condition);
        List<String> keys = keysValues[0];
        List<Object> values = keysValues[1];
        List<String> whereFields = CollectionUtils.eachWrap(keys, "`", "` = ?");
        String whereSql = "where " + StringUtils.join(whereFields, " and ");
        return new WhereSqlAndArgs(whereSql, values);
    }


    protected static class WhereSqlAndArgs {
        public String whereSql;
        public List<Object> whereArgs;

        public WhereSqlAndArgs(String whereSql, List<Object> whereArgs) {
            this.whereSql = whereSql;
            this.whereArgs = whereArgs;
        }
    }
}
