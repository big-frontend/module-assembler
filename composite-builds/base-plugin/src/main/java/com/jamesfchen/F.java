package com.jamesfchen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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

    public boolean copyFileNIO(File srcFile, File destFile) {
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            inChannel = new FileInputStream(srcFile).getChannel();
            outChannel = new FileOutputStream(destFile).getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (inChannel != null) {
                try {
                    inChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outChannel != null) {
                try {
                    outChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void copyFileNIO(FileInputStream fis, FileOutputStream fos) {
        copyFileNIO(fis.getChannel(),fos.getChannel());
    }
    public void copyFileNIO(FileChannel inChannel, FileChannel outChannel) {
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                outChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
