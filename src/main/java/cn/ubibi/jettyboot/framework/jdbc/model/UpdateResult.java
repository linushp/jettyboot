package cn.ubibi.jettyboot.framework.jdbc.model;

import java.util.ArrayList;
import java.util.List;

public class UpdateResult {

    private List<Long> generatedKeys = new ArrayList<>();

    private int affectedRows;

    private String errMsg;

    public UpdateResult() {
    }

    public UpdateResult(String errMsg) {
        this.errMsg = errMsg;
    }


    public List<Long> getGeneratedKeys() {
        return generatedKeys;
    }

    public void setGeneratedKeys(List<Long> generatedKeys) {
        this.generatedKeys = generatedKeys;
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
