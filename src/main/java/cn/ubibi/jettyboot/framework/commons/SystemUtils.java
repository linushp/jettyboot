package cn.ubibi.jettyboot.framework.commons;

import java.lang.management.ManagementFactory;
import java.util.Map;

public class SystemUtils {

    public static boolean isMavenDevMode(){
        Map<String, String> map = ManagementFactory.getRuntimeMXBean().getSystemProperties();
        String commandLine = map.get("sun.java.command");
        if (commandLine!=null && commandLine.indexOf("is_maven_dev_mode")>0){
            return true;
        }
        return false;
    }


}
