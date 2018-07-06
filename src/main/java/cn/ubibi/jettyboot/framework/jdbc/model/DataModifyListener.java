package cn.ubibi.jettyboot.framework.jdbc.model;

public interface DataModifyListener {
    void onBeforeDataModify(String sql, Object[] args) throws Exception;

    void onAfterDataModify(String sql, Object[] args, UpdateResult updateResult) throws Exception;
}
