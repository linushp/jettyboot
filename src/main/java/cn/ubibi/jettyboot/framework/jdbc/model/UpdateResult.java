package cn.ubibi.jettyboot.framework.jdbc.model;

public class UpdateResult {

    private Long generatedKey;

    private int affectedRows;

    public Long getGeneratedKey() {
        return generatedKey;
    }

    public void setGeneratedKey(Long generatedKey) {
        this.generatedKey = generatedKey;
    }

    public int getAffectedRows() {
        return affectedRows;
    }

    public void setAffectedRows(int affectedRows) {
        this.affectedRows = affectedRows;
    }

}
