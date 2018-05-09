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
        return Short.parseShort(ignoreDotAfter(value));
    }


    public Integer toInteger() {
        if (isEmpty()) {
            return 0;
        }
        return Integer.parseInt(ignoreDotAfter(value));
    }


    public Long toLong() {
        if (isEmpty()) {
            return 0L;
        }
        return Long.parseLong(ignoreDotAfter(value));
    }


    /**
     * 忽略掉小数点及以后的字符串
     * @param value 类似于：123.3223  .00
     * @return  无小数点的字符串
     */
    private String ignoreDotAfter(String value){
        String v = value;
        int indexOfDot = v.indexOf(".");
        if (indexOfDot == 0) {
            return "0";
        }
        if (indexOfDot > 0) {
            v = v.substring(0, indexOfDot);
        }
        return v;
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
        if (isEmpty()) {
            return null;
        }
        return JSON.parseObject(value);
    }

    public <T> T toJSONObject(Class<? extends T> clazz) {
        if (isEmpty()) {
            return null;
        }
        return JSON.parseObject(value, clazz);
    }

    public JSONArray toJSONArray() {
        if (isEmpty()) {
            return null;
        }
        return JSON.parseArray(value);
    }

    public <T> List<T> toJSONArray(Class<T> clazz) {
        if (isEmpty()) {
            return null;
        }
        return JSON.parseArray(value,clazz);
    }



    public boolean isEmpty() {
        return (value == null || value.isEmpty());
    }

}
