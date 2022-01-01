package com.jamesfchen;

import java.io.File;
import java.io.InputStream;

/**
 * Copyright ® $ 2017
 * All right reserved.
 *
 */
public class ClassInfo{
    public File mather;//jar or dir
    public File classFile;
    public InputStream classStream;
    public String canonicalName;

    ClassInfo(File mather, InputStream classStream, String canonicalName) {
        this.mather = mather;
        this.classStream = classStream;
        this.canonicalName = canonicalName;
    }

    ClassInfo(File mather, File classFile, String canonicalName) {
        this.mather = mather;
        this.classFile = classFile;
        this.canonicalName = canonicalName;
    }

    @Override
    public  String toString() {
        if (classFile !=null){
            String info = "ClassInfo{" +
                    "mather=" + mather +
                    ", classFile=" + classFile +
                    ", canonicalName='" + canonicalName + '\'' +
                    '}';
            return  "Class In Dir "+info;
        }else {
            String info = "ClassInfo{" +
                    "mather=" + mather +
                    ", classStream=" + classStream +
                    ", canonicalName='" + canonicalName + '\'' +
                    '}';
            return  "Class In Jar "+info;
        }

    }
}