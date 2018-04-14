package cn.ubibi.jettyboot.framework.commons.scan;

import cn.ubibi.jettyboot.framework.commons.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class ClasspathPackageScanner implements PackageScanner {
    private Logger logger = LoggerFactory.getLogger(ClasspathPackageScanner.class);
    private String basePackage;
    private ClassLoader classLoader;

    /**
     * 初始化
     *
     * @param basePackage
     */
    public ClasspathPackageScanner(String basePackage) {
        this.basePackage = basePackage;
        this.classLoader = getClass().getClassLoader();
    }

    public ClasspathPackageScanner(String basePackage, ClassLoader classLoader) {
        this.basePackage = basePackage;
        this.classLoader = classLoader;
    }

    /**
     * 获取指定包下的所有字节码文件的全类名
     */
    public List<String> getFullyQualifiedClassNameList() throws IOException {
        logger.info("开始扫描包{}下的所有类", basePackage);
        return doScan(basePackage, new ArrayList<String>());
    }

    /**
     * doScan函数
     *
     * @param basePackage
     * @param nameList
     * @return
     * @throws IOException
     */
    private List<String> doScan(String basePackage, List<String> nameList) throws IOException {
        String splashPath = StringUtils.dotToSplash(basePackage);
        URL url = classLoader.getResource(splashPath);   //file:/D:/WorkSpace/java/ScanTest/target/classes/com/scan
        String filePath = StringUtils.getRootPath(url);
        List<String> names = null; // contains the name of the class file. e.g., Apple.class will be stored as "Apple"



        if (isJarFile(filePath)) {// 先判断是否是jar包，如果是jar包，通过JarInputStream产生的JarEntity去递归查询所有类
            if (logger.isDebugEnabled()) {
                logger.debug("{} 是一个JAR包", filePath);
            }

            names = readFromJarFile(filePath, splashPath);
            names = toFullyQualifiedName(names,null);
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("{} 是一个目录", filePath);
            }
            names = readFromDirectory(filePath);
            names = toFullyQualifiedName(names,basePackage);
        }


        nameList.addAll(names);

        if (logger.isDebugEnabled()) {
            for (String n : nameList) {
                logger.debug("找到{}", n);
            }
        }
        return nameList;
    }

    private List<String> toFullyQualifiedName(List<String> shortName, String basePackage) {

        List<String> result = new ArrayList<>();

        for (String s:shortName){

            String name;
            if (!StringUtils.isEmpty(basePackage)){
                name = basePackage + "." + s;
            }else {
                name = s;
            }
            name = StringUtils.trimExtension(name);
            name = StringUtils.splashToDot(name);

            result.add(name);
        }

        return result;

    }

    private List<String> readFromJarFile(String jarPath, String splashedPackageName) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("从JAR包中读取类: {}", jarPath);
        }
        JarInputStream jarIn = new JarInputStream(new FileInputStream(jarPath));
        JarEntry entry = jarIn.getNextJarEntry();
        List<String> nameList = new ArrayList<String>();
        while (null != entry) {
            String name = entry.getName();
            if (name.startsWith(splashedPackageName) && isClassFile(name)) {
                nameList.add(name);
            }
            entry = jarIn.getNextJarEntry();
        }

        return nameList;
    }

    private List<String> readFromDirectory(String path) {

        int prefixLength = path.length();

        File file = new File(path);

        List<File> fileList = listFileOfDirectory(file);


        List<String> fileNames = new ArrayList<>();
        for (File file1 : fileList){
            String path1 = file1.getAbsolutePath();
            String pathShort = path1.substring(prefixLength+1);
            fileNames.add(pathShort);
        }

        return fileNames;
    }

    private List<File> listFileOfDirectory(File file) {

        if (file.isDirectory()){
            List<File> result = new ArrayList<>();
            File[] files = file.listFiles();
            for (File file1 : files){

                if (file1.isDirectory()){

                    List<File> file1List = listFileOfDirectory(file1);
                    if (file1List!=null && !file1List.isEmpty()){
                        result.addAll(file1List);
                    }

                }else {
                    result.add(file1);
                }

            }

            return result;
        }

        return new ArrayList<>();
    }





    private boolean isClassFile(String name) {
        return name.endsWith(".class");
    }

    private boolean isJarFile(String name) {
        return name.endsWith(".jar");
    }

//    /**
//     * For test purpose.
//     */
//    public static void main(String[] args) throws Exception {
//        PackageScanner scan = new ClasspathPackageScanner("com.scan");
//        scan.getFullyQualifiedClassNameList();
//    }
}