package com.igd.mimeka.io;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {
	
	/**
     * Fileתbyte[]����
     *
     * @param fileFullPath
     * @return
     */
    public static byte[] file2byte(String fileFullPath) {
        if (fileFullPath == null || "".equals(fileFullPath)) {
            return null;
        }
        return file2byte(new File(fileFullPath));
    }
 
    /**
     * Fileתbyte[]����
     *
     * @param file
     * @return
     */
    public static byte[] file2byte(File file) {
        if (file == null) {
            return null;
        }
        FileInputStream fileInputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fileInputStream.read(b)) != -1) {
                byteArrayOutputStream.write(b, 0 , n);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
 
    /**
     * byte[]����תFile
     *
     * @param bytes
     * @param fileFullPath
     * @return
     */
    public static File byte2file(byte[] bytes, String fileFullPath) {
        if (bytes == null) {
            return null;
        }
        FileOutputStream fileOutputStream = null;
        try {
            File file = new File(fileFullPath);
            //�ж��ļ��Ƿ����
            if (file.exists()) {
                file.mkdirs();
            }
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bytes);
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            }  catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return null;
    }

}
