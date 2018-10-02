package cn.ubibi.jettyboot.framework.commons;

import java.math.BigInteger;
import java.util.Base64;

public class BytesToString {


    public static String byte2Base58(byte[] bytes) {
        return Base58.encode(bytes);
    }


    public static String byte2Base64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }


    public static String byte2Hex(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer();
        String temp;
        for (int i = 0; i < bytes.length; i++) {
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length() == 1) {
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }



}
