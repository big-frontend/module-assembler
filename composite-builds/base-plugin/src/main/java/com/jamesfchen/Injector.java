package com.jamesfchen;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class Injector {
    public interface Callback {
        byte[] call(int where, InputStream inputStream);
    }

    public static void injectCode(ClassInfo info, Callback closure) {
        if (info.classStream != null) {
            File optZipFile = new File(info.mather.getParent(), info.mather.getName() + ".opt");
            if (optZipFile.exists()) {
                optZipFile.delete();
            }
            try (ZipOutputStream optZipFos = new ZipOutputStream(new FileOutputStream(optZipFile)); ZipFile theZip = new ZipFile(info.mather)) {
                for (ZipEntry zipEntry : Collections.list(theZip.entries())) {
                    try (InputStream inputStream = theZip.getInputStream(zipEntry)) {
                        String canonicalName = F.canonicalName(zipEntry);
                        String fileName = zipEntry.getName().substring(zipEntry.getName().lastIndexOf("/") + 1);
                        //创建一个新的zip entry
                        optZipFos.putNextEntry(new ZipEntry(zipEntry.getName()));
                        if (canonicalName == null || canonicalName.isEmpty()
                                || "R.class".equals(fileName)
                                || "BuildConfig.class".equals(fileName)
                                || fileName.startsWith("R$")
                                //                            || canonicalName.startsWith("androidx")
                                //                            || canonicalName.startsWith("android")
                                //                            || canonicalName.startsWith("kotlin")
                                || canonicalName.startsWith("org.bouncycastle")
                        ) {
                            int len;
                            byte[] buffer = new byte[1024];
                            while ((len = (inputStream.read(buffer))) != -1) {
                                optZipFos.write(len);
                            }
                        } else {
                            if (info.canonicalName.equals(canonicalName)) {
                                byte[] codes = closure.call(Constants.JAR, inputStream);
                                optZipFos.write(codes);
                            } else {
                                int len;
                                byte[] buffer = new byte[1024];
                                while ((len = (inputStream.read(buffer))) != -1) {
                                    optZipFos.write(len);
                                }
                            }
                        }
                        optZipFos.closeEntry();
                    } catch (Exception e) {
                        P.error("foreach zip file\n"+e.getLocalizedMessage());
                        return;
                    }
                }

            } catch (Exception e) {
                P.error(e.getLocalizedMessage());
                return;
            }
            if (info.mather.exists()) {
                boolean delete = info.mather.delete();
                P.info(delete ?"删除成功":"删除失败");
            }
            optZipFile.renameTo(info.mather);
        } else {
            injectCode(info.classFile, closure);
        }

    }

    public static void injectCode(File classFile, Callback closure) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(classFile);
            byte[] codes = closure.call(Constants.DIR, inputStream);
            if (codes !=null) FileUtils.writeByteArrayToFile(classFile, codes);
        } catch (Exception e) {
            e.printStackTrace();
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