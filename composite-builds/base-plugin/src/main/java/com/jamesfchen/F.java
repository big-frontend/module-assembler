package com.jamesfchen;

import java.io.File;
import java.util.jar.JarEntry;

/**
 * Copyright ® $ 2017
 * All right reserved.
 *
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

    public static String canonicalName(JarEntry jarEntry) {
        String classPath = jarEntry.getName();
        if (classPath.endsWith(".class")) {
            return classPath.split("\\.")[0].replace(File.separator, ".");
        } else {
            return null;
        }
    }
}
