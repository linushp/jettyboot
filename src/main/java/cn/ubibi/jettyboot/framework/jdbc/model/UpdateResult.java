package cn.ubibi.jettyboot.framework.jdbc.model;

public class UpdateResult {

    private Long generatedKey;

    private int affectedRows;

    private String errMsg;

    public UpdateResult() {
    }

    public UpdateResult(String errMsg) {
        this.errMsg = errMsg;
    }


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

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
