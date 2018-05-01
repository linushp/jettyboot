package cn.ubibi.jettyboot.framework.commons;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class PropertiesUtils {

    private static final String CLASSPATH = "classpath:";

    private static final Map<String, Properties> propertiesMap = new HashMap<>();

    private static Properties loadProperties(String fileName) throws IOException {


        InputStream inStream = null;
        if (fileName.startsWith("./") || fileName.startsWith("../") || fileName.startsWith("/")) {
            File file = new File(fileName);
            inStream = new FileInputStream(file);
        } else if (fileName.length() > 2 && fileName.charAt(1) == ':') {
            //for windows : C:\\
            File file = new File(fileName);
            inStream = new FileInputStream(file);
        } else if (fileName.startsWith("~")) {
            String userHome = System.getProperty("user.home");
            String filePath = fileName.replaceFirst("~", userHome);
            File file = new File(filePath);
            inStream = new FileInputStream(file);
        } else if (fileName.startsWith(CLASSPATH)) {
            String filePath = fileName.substring(CLASSPATH.length());
            filePath = (filePath.charAt(0) == '/' ? filePath.substring(1) : filePath);
            inStream = PropertiesUtils.class.getClassLoader().getResourceAsStream(filePath);
        } else {
            String filePath = fileName;
            filePath = (filePath.charAt(0) == '/' ? filePath.substring(1) : filePath);
            inStream = PropertiesUtils.class.getClassLoader().getResourceAsStream(fileName);
        }


        Properties properties = new Properties();
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


    public static Map<String, Object> toMap(Properties p) {
        Map<String, Object> map = new HashMap<>();
        if (p == null) {
            return map;
        }

        Set<Map.Entry<Object, Object>> entrySet = p.entrySet();
        for (Map.Entry<Object, Object> entry : entrySet) {
            Object x = entry.getValue();
            Object k = entry.getKey();
            if (x != null && k != null) {
                String xs = x.toString();
                String keyString = k.toString();
                map.put(keyString, xs);
            }
        }

        return map;
    }


    /**
     * 从一个文件中过滤出属性以startWith开头的所有属性
     *
     * @param fileName          文件名
     * @param startWith         以什么开头
     * @param isRemoveStartWith 返回结果是否移除掉startWith
     * @return 一个map
     * @throws IOException IO异常
     */
    public static Map<String, Object> getPropertiesKeyStartWith(String fileName, String startWith, boolean isRemoveStartWith) throws IOException {
        Properties p = getProperties(fileName);
        Map<String, Object> map = new HashMap<>();
        if (p == null) {
            return map;
        }

        Set<Map.Entry<Object, Object>> entrySet = p.entrySet();
        for (Map.Entry<Object, Object> entry : entrySet) {
            Object value = entry.getValue();
            Object key = entry.getKey();
            if (value != null && key != null) {
                String keyString = key.toString();
                if (keyString.startsWith(startWith)) {
                    if (isRemoveStartWith) {
                        keyString = keyString.substring(startWith.length());
                    }
                    map.put(keyString, value);
                }
            }
        }
        return map;
    }


    public static <T> T getBeanByProperties(String fileName, Class<T> tClass) throws IOException, IllegalAccessException, InstantiationException {
        Properties p = getProperties(fileName);
        Map<String, Object> m = toMap(p);
        T b = BeanUtils.mapToBean(tClass, m);
        return b;
    }
}
