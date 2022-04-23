package com.jamesfchen;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;

/**
 * Copyright ® $ 2017
 * All right reserved.
 *
 */
class JarDiffer {
    File srcJar;
    File destJar;
    Map<String, JarEntry> unchangedMap = new LinkedHashMap<>();
    Map<String, JarEntry> removedMap = new LinkedHashMap<>();
    Map<String, JarEntry> addedMap = new LinkedHashMap<>();

    public List<JarEntry> getRemovedList() {
        return  new ArrayList<>(removedMap.values());
    }
    public List<JarEntry> getAddedList() {
        return  new ArrayList<>(addedMap.values());
    }
    public JarDiffer(File srcJar, File destJar) {
        this.srcJar = srcJar;
        this.destJar = destJar;
    }

    JarDiffer computeDiff(){
        addedMap.putAll(Jarer.getClassMapOnJar(srcJar));
        Jarer.getClassMapOnJar(destJar).forEach((entryName, jarEntry) -> {
            if (addedMap.remove(entryName) !=null){
                unchangedMap.put(entryName,jarEntry);
            } else {
                removedMap.put(entryName,jarEntry);
            }
        });
        return this;
    }
}
