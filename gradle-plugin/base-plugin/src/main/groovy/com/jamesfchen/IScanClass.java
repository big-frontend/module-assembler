package com.jamesfchen;

/**
 * Copyright ® $ 2017
 * All right reserved.
 *
 * @author: jamesfchen
 * @since: Nov/28/2021  Sun
 */
public interface IScanClass {
    void onScanBegin();

    void onScanClassInDir(ClassInfo info);

    void onScanClassInJar(ClassInfo info);

    void onScanEnd();
}