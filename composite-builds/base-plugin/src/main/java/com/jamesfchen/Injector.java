package com.jamesfchen;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

public class Injector {
    public interface Callback {
        byte[] call(String where, InputStream inputStream);
    }

    public static void injectCode(ClassInfo info, Callback closure) {
        if (info.classStream != null) {
            JarFile jarFile = null;
            try {
                jarFile = new JarFile(info.mather);
                File optJarFile = new File(info.mather.getParent(), info.mather.getName() + ".opt");
                if (optJarFile.exists()) {
                    optJarFile.delete();
                }
                JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(optJarFile));
                while (jarFile.entries().hasMoreElements()) {
                    JarEntry jarEntry = jarFile.entries().nextElement();
                    String jarName = jarEntry.getName();
                    ZipEntry zipEntry = new ZipEntry(jarName);
                    InputStream inputStream = jarFile.getInputStream(jarEntry);
                    jarOutputStream.putNextEntry(zipEntry);
                    if (jarName.equals(info.canonicalName.replace(".", "/") + ".class")) {
                        byte[] codes = closure.call("jar", inputStream);
                        jarOutputStream.write(codes);
                    } else {
                        int len;
                        byte[] buffer = new byte[1024];
                        while ((len = (inputStream.read(buffer))) != -1) {
                            jarOutputStream.write(len);
                        }
                    }
                    inputStream.close();
                    jarOutputStream.closeEntry();
                }
                jarOutputStream.close();
                if (info.mather.exists()) {
                    info.mather.delete();
                }
                optJarFile.renameTo(info.mather);
            } catch (Exception ignore) {

            } finally {
                if (jarFile != null) {
                    try {
                        jarFile.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        } else {
            injectCode(info.classFile, closure);
        }

    }

    public static void injectCode(File classFile, Callback closure) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(classFile);
            byte[] codes = closure.call("dir", inputStream);
            FileUtils.writeByteArrayToFile(classFile, codes);
            inputStream.close();
        } catch (Exception e) {

        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}