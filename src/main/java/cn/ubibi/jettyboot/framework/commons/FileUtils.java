package cn.ubibi.jettyboot.framework.commons;

import java.io.*;

public class FileUtils {

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 8;

    public static void saveToFile(String file_name_path, InputStream inputStream) throws IOException {
        File file = new File(file_name_path);

        FileOutputStream fileOut = new FileOutputStream(file);

        inputStream2OutputStream(inputStream,fileOut);

        fileOut.flush();
        fileOut.close();
    }


    public static void inputStream2OutputStream(InputStream inputStream,OutputStream outputStream) throws IOException {
        inputStream2OutputStream(inputStream,outputStream,DEFAULT_BUFFER_SIZE);
    }


    public static void inputStream2OutputStream(InputStream inputStream,OutputStream outputStream,int buffer_zise) throws IOException {
        byte[] buf = new byte[buffer_zise];
        while (true) {
            int read = 0;
            if (inputStream != null) {
                read = inputStream.read(buf);
            }
            if (read == -1) {
                break;
            }
            outputStream.write(buf, 0, read);
        }
    }


    public static File forceMkdirs(String pathName) {
        return forceMkdirs(new File(pathName));
    }


    public static File forceMkdirs(File file) {
        if (!file.exists()) {
            file.mkdirs();
        } else if (!file.isDirectory()) {
            file.delete();
            file.mkdirs();
        }
        return file;
    }
}
