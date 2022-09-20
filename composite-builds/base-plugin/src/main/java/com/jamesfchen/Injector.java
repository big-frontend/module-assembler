package com.jamesfchen;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class Injector {
    public interface Callback {
        byte[] call(int where, InputStream inputStream);
    }

    public static void injectCode(ClassInfo info, Callback closure) {
        if (info.classStream != null) {
            File optZipFile = new File(info.mather2.getParent(), info.mather2.getName() + ".opt");
            if (optZipFile.exists()) {
                optZipFile.delete();
            }
            try (ZipOutputStream optZipFos = new ZipOutputStream(new FileOutputStream(optZipFile)); ZipFile theZip = new ZipFile(info.mather2)) {
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
                            F.copyIs2Os(inputStream, optZipFos);
                        } else if (info.canonicalName.equals(canonicalName)) {
                            byte[] codes = closure.call(Constants.JAR, inputStream);
                            if (codes.length != 0) {
                                optZipFos.write(codes);
                            } else {
                                F.copyIs2Os(inputStream, optZipFos);
                            }
                        } else {
                            F.copyIs2Os(inputStream, optZipFos);
                        }
                        optZipFos.closeEntry();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (info.mather2.exists()) {
                info.mather2.delete();
            }
            optZipFile.renameTo(info.mather2);
        } else {
            injectCode(info.classFile, closure);
        }

    }

    public static void injectCode(File classFile, Callback closure) {
        try (InputStream inputStream = new FileInputStream(classFile)) {
            byte[] codes = closure.call(Constants.DIR, inputStream);
            if (codes != null) FileUtils.writeByteArrayToFile(classFile, codes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}