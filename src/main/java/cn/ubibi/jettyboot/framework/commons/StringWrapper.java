package cn.ubibi.jettyboot.framework.commons;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class StringWrapper {

    private String value = null;

    public StringWrapper(String stringData) {
        this.value = stringData;
    }

    public Short toShort() {
        if (isEmpty()) {
            return 0;
        }
        return Short.parseShort(value);
    }

    public Integer toInteger() {
        if (isEmpty()) {
            return 0;
        }
        return Integer.parseInt(value);
    }

    public Long toLong() {
        if (isEmpty()) {
            return 0L;
        }
        return Long.parseLong(value);
    }

    public Float toFloat() {
        if (isEmpty()) {
            return 0F;
        }
        return Float.parseFloat(value);
    }

    public Double toDouble() {
        if (isEmpty()) {
            return 0D;
        }
        return Double.parseDouble(value);
    }

    public Boolean toBoolean() {
        if (isEmpty() || "0".equals(value)) {
            return false;
        }
        return true;
    }

    public BigInteger toBigInteger() {
        if (isEmpty()) {
            return new BigInteger("0");
        }
        return new BigInteger(value);
    }

    public BigDecimal toBigDecimal() {
        if (isEmpty()) {
            return new BigDecimal(0);
        }
        return new BigDecimal(value);
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }


    public JSONObject toJSONObject() {
        String s = this.value;
        if (isEmpty()) {
            return null;
        }
        return JSON.parseObject(s);
    }

    public <T> T toJSONObject(Class<? extends T> clazz) {
        String s = this.value;
        if (isEmpty()) {
            return null;
        }
        return JSON.parseObject(s, clazz);
    }

    public JSONArray toJSONArray() {
        String s = this.value;
        if (isEmpty()) {
            return null;
        }
        return JSON.parseArray(s);
    }

    public <T> List<T> toJSONArray(Class<T> clazz) {
        String s = this.value;
        if (isEmpty()) {
            return null;
        }
        return JSON.parseArray(s,clazz);
    }



    public boolean isEmpty() {
        return (value == null || value.isEmpty());
    }


}
