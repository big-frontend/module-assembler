package com.jamesfchen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.zip.ZipEntry;

/**
 * Copyright ® $ 2017
 * All right reserved.
 */
public class F {
    public static String classPath(File rootDir, File file) {
        String rootPath = rootDir.getAbsolutePath();
        if (!rootPath.endsWith(File.separator)) {
            rootPath += File.separator;
        }
        return file.getAbsolutePath().replace(rootPath, "");
    }

    public static String canonicalName(File rootDir, File file) {
        String rootPath = rootDir.getAbsolutePath();
        if (!rootPath.endsWith(File.separator)) {
            rootPath += File.separator;
        }
        String classPath = file.getAbsolutePath().replace(rootPath, "");
        if (classPath.endsWith(".class")) {
            return classPath.split("\\.")[0].replace(File.separator, ".");
        } else {
            return null;
        }
    }

    public static String canonicalName(ZipEntry entry) {
        String classPath = entry.getName();
        if (classPath.endsWith(".class")) {
            return classPath.split("\\.")[0].replace("/", ".");
        } else {
            return null;
        }
    }

    public static boolean copyFileNIO(File srcFile, File destFile) {
        try (FileChannel inChannel = new FileInputStream(srcFile).getChannel(); FileChannel outChannel = new FileOutputStream(destFile).getChannel();) {
            inChannel.transferTo(0, inChannel.size(), outChannel);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void copyFileNIO(FileInputStream fis, FileOutputStream fos) throws IOException {
        copyFileNIO(fis.getChannel(), fos.getChannel());
    }

    public static void copyFileNIO(FileChannel inChannel, FileChannel outChannel) throws IOException {
        inChannel.transferTo(0, inChannel.size(), outChannel);
    }

    public static void copyIs2Os(InputStream is, OutputStream os) throws IOException {
        int len;
        byte[] buffer = new byte[8192];
        while ((len = (is.read(buffer))) != -1) {
            os.write(buffer, 0, len);
        }
    }

}
