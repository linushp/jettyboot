package cn.ubibi.jettyboot.framework.jdbc.model;

import java.util.List;

public class SqlNdArgs {
    private String sql;
    private List<Object> args;

    public SqlNdArgs(String sql, List<Object> args) {
        this.sql = sql;
        this.args = args;
    }


    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<Object> getArgs() {
        return args;
    }

    public void setArgs(List<Object> args) {
        this.args = args;
    }
}
