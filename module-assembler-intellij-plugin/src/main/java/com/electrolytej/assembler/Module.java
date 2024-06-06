package com.electrolytej.assembler;


public class Module {

    /**
     * simpleName : app
     * canonicalName : :app
     * format : app
     * group : father
     * binary_artifact : com.jamesfchen.b:app:1.0
     * deps : [":hotel-module:bundle1",":hotel-module:bundle2"]
     */
    public String simpleName;
    public String format;
    public String group;
    public String dynamic;
//    public String binary_artifact;
    public Type type = Type.NONE;
}