package cn.ubibi.jettyboot.framework.commons;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesUtils {

    private static final Map<String, Properties> propertiesMap = new HashMap<>();

    public static Properties loadProperties(String fileName) throws IOException {
        Properties properties = new Properties();
        InputStream inStream = PropertiesUtils.class.getClassLoader().getResourceAsStream(fileName);
        properties.load(inStream);

        //放入缓存
        propertiesMap.put(fileName, properties);

        return properties;
    }


    public static Properties getProperties(String fileName) throws IOException {
        Properties p = propertiesMap.get(fileName);
        if (p == null) {
            p = loadProperties(fileName);
        }
        return p;
    }


}
