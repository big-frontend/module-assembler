package com.jamesfchen;

/**
 * Copyright ® $ 2017
 * All right reserved.
 *
 */
public interface IScanClass {
    void onScanBegin();

    void onScanClassInDir(ClassInfo info);

    void onScanClassInJar(ClassInfo info);

    void onScanEnd();
}