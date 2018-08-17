package cn.ubibi.jettyboot.framework.rest.handlers.proxyfiles;

import java.util.HashMap;
import java.util.Map;

public enum FileContentTypeEnum {
    IMG(1),
    VIDEO(2),
    AUDIO(3),
    JavaScript(4),
    CSS(5),
    HTML(6),
    TXT(7),
    JSON(8),
    ZIP_RAR(10),
    XML(11),
    PDF(14),
    SVG(15),
    FONT(16),

    OTHER(999);

    private int value;


    private static final Map<String, FileContentTypeEnum> suffixMap = new HashMap<>();
    private static final Map<String, String> suffixContentTypeMap = new HashMap<>();

    static {

        addSuffixInfo("jpg", IMG, "image/jpeg");
        addSuffixInfo("png", IMG, "image/png");
        addSuffixInfo("gif", IMG, "image/gif");
        addSuffixInfo("ico", IMG, "image/x-icon");
        addSuffixInfo("tif", IMG, "image/tiff");
        addSuffixInfo("bmp", IMG, "application/x-bmp");
        addSuffixInfo("pdf", PDF, "application/pdf");

        addSuffixInfo("woff", FONT, "application/font-woff");

        addSuffixInfo("zip", ZIP_RAR, "application/zip");
        addSuffixInfo("rar", ZIP_RAR, "application/octet-stream");


        addSuffixInfo("js", JavaScript, "application/javascript");
        addSuffixInfo("css", CSS, "text/css");
        addSuffixInfo("html", HTML, "text/html");
        addSuffixInfo("htm", HTML, "text/html");
        addSuffixInfo("shtml", HTML, "text/html");
        addSuffixInfo("xhtml", HTML, "text/html");

        addSuffixInfo("md", TXT, "text/plain");
        addSuffixInfo("jsp", TXT, "text/plain");
        addSuffixInfo("asp", TXT, "text/plain");
        addSuffixInfo("properties", TXT, "text/plain");
        addSuffixInfo("txt", TXT, "text/plain");
        addSuffixInfo("csv", TXT, "text/plain");
        addSuffixInfo("ini", TXT, "text/plain");
        addSuffixInfo("323", TXT, "text/plain");


        addSuffixInfo("json", JSON, "application/json");
        addSuffixInfo("svg", SVG, "text/xml");
        addSuffixInfo("xml", XML, "text/xml");
        addSuffixInfo("vml", XML, "text/xml");
        addSuffixInfo("tsd", XML, "text/xml");


        addSuffixInfo("mp4", VIDEO, "video/mpeg4");
        addSuffixInfo("rmvb", VIDEO, "application/vnd.rn-realmedia-vbr");
        addSuffixInfo("mpg", VIDEO, "video/mpg");
        addSuffixInfo("mpeg", VIDEO, "video/mpg");
        addSuffixInfo("3gp", VIDEO, "video/3gp");
        addSuffixInfo("mov", VIDEO, "video/mov");
        addSuffixInfo("wmv", VIDEO, "video/wmv");


        addSuffixInfo("mp3", AUDIO, "audio/mp3");
        addSuffixInfo("wav", AUDIO, "audio/wav");
        addSuffixInfo("wma", AUDIO, "audio/x-ms-wma");
        addSuffixInfo("m4a", AUDIO, "application/octet-stream");
        addSuffixInfo("mp2", AUDIO, "application/octet-stream");
        addSuffixInfo("m1a", AUDIO, "application/octet-stream");
        addSuffixInfo("m2a", AUDIO, "application/octet-stream");


    }



    public static boolean isTextContentTypeBySuffix(String suffix){
        FileContentTypeEnum x = getOssResEnumBySuffix(suffix);
        if (x == FileContentTypeEnum.TXT ||
                x == FileContentTypeEnum.JSON ||
                x == FileContentTypeEnum.HTML ||
                x == FileContentTypeEnum.CSS ||
                x == FileContentTypeEnum.JavaScript ||
                x == FileContentTypeEnum.XML) {
            return true;
        }
        return false;
    }


    public static String getContentTypeBySuffix(String suffix) {
        String x = suffixContentTypeMap.get(suffix);
        if (x == null) {
            return "application/octet-stream";
        }
        return x;
    }


    private static void addSuffixInfo(String suffix, FileContentTypeEnum ossFileEnum, String contentType) {
        suffixMap.put(suffix, ossFileEnum);
        suffixContentTypeMap.put(suffix, contentType);
    }


    public static FileContentTypeEnum getOssResEnumByFileName(String fileName) {
        String fileNameLower = fileName.toLowerCase();
        String suffix = getFileNameSuffix(fileNameLower);
        return getOssResEnumBySuffix(suffix);
    }


    public static FileContentTypeEnum getOssResEnumBySuffix(String suffix) {
        FileContentTypeEnum ossResEnum = suffixMap.get(suffix);
        if (ossResEnum != null) {
            return ossResEnum;
        }
        return OTHER;
    }


    public static String getFileNameSuffix(String originalname) {
        int dot_index = originalname.lastIndexOf('.');
        String file_suffix = originalname.substring(dot_index + 1);
        return file_suffix;
    }

    FileContentTypeEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
