package com.jamesfchen;

import java.io.File;
import java.io.InputStream;

/**
 * Copyright ® $ 2017
 * All right reserved.
 *ClassInfo{
 * mather=/Users/hawks.jamesf/tech/Spacecraft/bundles-assembler/b/app/build/intermediates/transforms/IbcTransform/release/335.jar,
 * classStream=java.util.zip.ZipFile$ZipFileInflaterInputStream@ce006b7,
 * canonicalName='com.jamesfchen.ibc.IBCInitializer'
 * }
 */
public class ClassInfo{
    public File mather;//jar or dir
    public File classFile;
    public InputStream classStream;
    public String canonicalName;
    public int status=-1;
    public static final int BIRTH_JAR=0;
    public static final int BIRTH_DIR=1;
    public static final int DEATH_JAR=2;
    public static final int DEATH_DIR=3;

    ClassInfo(int status, File mather, InputStream classStream, String canonicalName) {
        this.status = status;
        this.mather = mather;
        this.classStream = classStream;
        this.canonicalName = canonicalName;
    }

    ClassInfo(int status,File mather, File classFile, String canonicalName) {
        this.status = status;
        this.mather = mather;
        this.classFile = classFile;
        this.canonicalName = canonicalName;
    }

    @Override
    public  String toString() {
        if (classFile !=null){
            String info = "ClassInfo{" +
                    "mather=" + mather +
                    ", status=" + status +
                    ", classFile=" + classFile +
                    ", canonicalName='" + canonicalName + '\'' +
                    '}';
            return  "Class In Dir "+info;
        }else{
            String info = "ClassInfo{" +
                    "mather=" + mather +
                    ", status=" + status +
                    ", classStream=" + classStream +
                    ", canonicalName='" + canonicalName + '\'' +
                    '}';
            return  "Class In Jar "+info;
        }

    }
}