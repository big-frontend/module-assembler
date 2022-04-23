package com.jamesfchen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Copyright ® $ 2017
 * All right reserved.
 *
 */
public class Jarer {

    static Map<String, JarEntry> getClassMapOnJar(File jar) {
        Map<String, JarEntry> allJarEntries = new LinkedHashMap<>();
        JarFile file = null;
        try {
            file = new JarFile(jar);
            Enumeration<JarEntry> enumeration = file.entries();
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = enumeration.nextElement();
                String entryName = jarEntry.getName();
                if (entryName.endsWith(".class")) {
                    allJarEntries.put(entryName, jarEntry);
                }
            }
            return allJarEntries;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }finally {
            if (file !=null){
                try {
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    static List<JarEntry> getClassListOnJar(File jar) {
        List<JarEntry> allJarEntries = new ArrayList<>();
        JarFile file = null;
        try {
            file = new JarFile(jar);
            Enumeration<JarEntry> enumeration = file.entries();
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = enumeration.nextElement();
                String entryName = jarEntry.getName();
                if (entryName.endsWith(".class")) {
                    allJarEntries.add(jarEntry);
                }
            }
            return allJarEntries;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }finally {
            if (file !=null){
                try {
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
