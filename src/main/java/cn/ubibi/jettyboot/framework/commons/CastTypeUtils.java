package cn.ubibi.jettyboot.framework.commons;



public class CastTypeUtils {

    //"1221.00" ,Interget.class
    public static Object toTypeOf(Object value, Class targetType) throws Exception {
        return new BasicConverter(value).toTypeOf(targetType);
    }

}
