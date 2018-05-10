package cn.ubibi.jettyboot.framework.commons;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class BasicConverter {

    //一个缓存避免重复转换成字符串
    private String stringValueCached = null;
    private Object value;


    public BasicConverter(Object value) {
        this.value = value;

    }


    public <T> T toTypeOf(Class<T> targetType) throws Exception {

        if (isNull()) {
            return null;
        }

        Class valueType = value.getClass();
        if (targetType == valueType || targetType.equals(valueType)) {
            return (T) value;
        }


        Object result = null;
        if (targetType == String.class) {
            result = this.getStringValue();
        } else if (isTypeOf(targetType, Integer.class, Integer.TYPE)) {
            result = this.toInteger();
        } else if (isTypeOf(targetType, Float.class, Float.TYPE)) {
            result = this.toFloat();
        } else if (isTypeOf(targetType, Double.class, Double.TYPE)) {
            result = this.toDouble();
        } else if (isTypeOf(targetType, Long.class, Long.TYPE)) {
            result = this.toLong();
        } else if (isTypeOf(targetType, Boolean.class, Boolean.TYPE)) {
            result = this.toGeneralizedBoolean();
        } else if (isTypeOf(targetType, Short.class, Short.TYPE)) {
            result = this.toShort();
        } else if (targetType == Timestamp.class) {
            result = this.toTimestamp();
        } else if (targetType == Date.class) {
            result = this.toDate();
        } else if (targetType == BigDecimal.class) {
            result = this.toBigDecimal();
        } else if (targetType == BigInteger.class) {
            result = this.toBigInteger();
        } else if (targetType == JSONObject.class) {
            result = this.toJSONObject();
        } else if (targetType == JSONArray.class) {
            result = this.toJSONArray();
        } else {
            throw new Exception("NotSupportTheTypeOf:" + targetType);
        }

        return (T) result;
    }


    public Short toShort() {
        if (isNull()) {
            return 0;
        }

        if (value instanceof Short) {
            return (Short) value;
        }

        return Short.valueOf(ignoreDotAfter(getStringValue()));
    }


    public Integer toInteger() {
        if (isNull()) {
            return 0;
        }

        if (value instanceof Integer) {
            return (Integer) value;
        }

        return Integer.valueOf(ignoreDotAfter(getStringValue()));
    }


    public Long toLong() {
        if (isNull()) {
            return 0L;
        }

        if (value instanceof Long) {
            return (Long) value;
        }

        return Long.valueOf(ignoreDotAfter(getStringValue()));
    }


    //byte 实际上是一个表示范围比较小的int（-128，128）
    private Byte toByte() {

        if (isNull()) {
            return 0;
        }
        if (value instanceof Byte) {
            return (Byte) value;
        }

        if (value instanceof Integer) {
            int intValue = ((Integer) value).intValue();
            return (byte) intValue;
        }

        return Byte.valueOf(ignoreDotAfter(getStringValue()));
    }


    /**
     * 忽略掉小数点及以后的字符串
     *
     * @param value 类似于：123.3223  .00
     * @return 无小数点的字符串
     */
    private String ignoreDotAfter(String value) {
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
        if (isNull()) {
            return 0F;
        }
        if (value instanceof Float) {
            return (Float) value;
        }
        return Float.valueOf(getStringValue());
    }

    public Double toDouble() {
        if (isNull()) {
            return 0D;
        }

        if (value instanceof Double) {
            return (Double) value;
        }

        return Double.valueOf(getStringValue());
    }

    public Boolean toBoolean() {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }

        return "true".equalsIgnoreCase(getStringValue());
    }


    //广义的boolean类型
    public Boolean toGeneralizedBoolean() {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (isNull() || "0".equals(getStringValue()) || "false".equals(getStringValue())) {
            return false;
        }
        return true;
    }


    public BigInteger toBigInteger() {
        if (isNull()) {
            return new BigInteger("0");
        }

        if (value instanceof BigInteger) {
            return (BigInteger) value;
        }

        return new BigInteger(ignoreDotAfter(getStringValue()));
    }

    public BigDecimal toBigDecimal() {
        if (isNull()) {
            return new BigDecimal(0);
        }

        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }

        return new BigDecimal(getStringValue());
    }


    public Date toDate() {
        return toTimestamp();
    }


    public Timestamp toTimestamp() {
        if (isNull()) {
            return null;
        }


        if (value instanceof Timestamp) {
            return (Timestamp) value;
        }

        long longDate = toLong();
        Timestamp timestamp = new Timestamp(longDate);
        return timestamp;
    }


    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }


    public String toString() {
        return getStringValue();
    }


    public JSONObject toJSONObject() {
        if (isNull()) {
            return null;
        }

        if (value instanceof JSONObject) {
            return (JSONObject) value;
        }

        return JSON.parseObject(getStringValue());
    }


    public <T> T toJSONObject(Class<? extends T> clazz) {
        if (isNull()) {
            return null;
        }
        return JSON.parseObject(getStringValue(), clazz);
    }

    public JSONArray toJSONArray() {
        if (isNull()) {
            return null;
        }
        return JSON.parseArray(getStringValue());
    }


    public <T> List<T> toJSONArray(Class<T> clazz) {
        if (isNull()) {
            return null;
        }
        return JSON.parseArray(getStringValue(), clazz);
    }


    public String getStringValue() {
        if (isNull()) {
            return null;
        }
        if (stringValueCached == null) {
            stringValueCached = this.value.toString();
        }
        return stringValueCached;
    }


    public boolean isNull() {
        return this.value == null;
    }


    /**
     * 判断数据类型是否需要转换
     *
     * @param fieldType
     * @param targetClass1
     * @param targetClass2
     * @return 是否
     */
    private static boolean isTypeOf(Class fieldType, Class targetClass1, Class targetClass2) {
        return fieldType == targetClass1 || fieldType == targetClass2;
    }

    public boolean isEmptyString() {
        return getStringValue() == null || getStringValue().isEmpty();
    }

}
