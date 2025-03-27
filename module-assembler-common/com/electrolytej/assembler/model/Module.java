package com.electrolytej.assembler.model;


/**
 * simpleName : app
 * canonicalName : :app
 * format : app
 * group : father
 * binary_artifact : com.jamesfchen.b:app:1.0
 * deps : [":hotel-module:bundle1",":hotel-module:bundle2"]
 */
public class Module {
    public String group;
    public String simpleName;
    public String sourcePath;
    public String binaryPath;
    public String projectDir;
    public String format;
    public String versionName;
    public String versionCode;
    public String dynamic;
    public Type type = Type.NONE;

    //    public String binary_artifact;
    public enum Type {
        NONE,

        //    AAR,
        //    JAR,
        BINARY, SOURCE, EXCLUDE
    }
}