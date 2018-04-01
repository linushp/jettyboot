package cn.ubibi.jettyboot.framework.commons;

import com.alibaba.fastjson.JSON;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.internal.runtime.JSONFunctions;

import javax.script.ScriptException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

public class StringWrapper {

    private String stringData = null;

    public StringWrapper(String stringData) {
        this.stringData = stringData;
    }

    public Short toShort() {
        if (isEmpty()) {
            return 0;
        }
        return Short.parseShort(stringData);
    }

    public Integer toInteger() {
        if (isEmpty()) {
            return 0;
        }
        return Integer.parseInt(stringData);
    }

    public Long toLong() {
        if (isEmpty()) {
            return 0L;
        }
        return Long.parseLong(stringData);
    }

    public Float toFloat() {
        if (isEmpty()) {
            return 0F;
        }
        return Float.parseFloat(stringData);
    }

    public Double toDouble() {
        if (isEmpty()) {
            return 0D;
        }
        return Double.parseDouble(stringData);
    }

    public Boolean toBoolean() {
        if (isEmpty() || "0".equals(stringData)) {
            return false;
        }
        return true;
    }


    public BigDecimal toBigDecimal() {
        if (isEmpty()) {
            return new BigDecimal(0);
        }
        return new BigDecimal(stringData);
    }


    public Date toDate(){
        return toTimestamp();
    }


    public Timestamp toTimestamp(){
        if(isEmpty()){
            return null;
        }
        long longDate = toLong();
        Timestamp timestamp = new Timestamp(longDate);
        return timestamp;
    }

    public String toString() {
        return stringData;
    }

    public <T> T toJSONOBject(Class<? extends T> clazz) {
        String s = this.stringData;
        if (isEmpty()) {
            return null;
        }
        return JSON.parseObject(s, clazz);
    }


    public boolean isEmpty() {
        return (stringData == null || stringData.isEmpty());
    }


}
