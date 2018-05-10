package cn.ubibi.jettyboot.framework.commons;



public class CastTypeUtils {

    //"1221.00" ,Interget.class
    public static Object castValueType(Object value, Class targetType) {
        return new BasicConverter(value).toTypeOf(targetType);
    }

}
