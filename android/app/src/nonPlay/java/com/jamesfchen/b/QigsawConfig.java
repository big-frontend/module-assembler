package com.jamesfchen.b;

public final class QigsawConfig {
    public static final boolean QIGSAW_MODE = true;
    public static final String VERSION_NAME = BuildConfig.VERSION_NAME;
    public static final String[] DYNAMIC_FEATURES = {
            "bundle",
            "bundle4",
    };
    public static final String DEFAULT_SPLIT_INFO_VERSION = BuildConfig.VERSION_NAME;
    public static final String QIGSAW_ID = BuildConfig.APPLICATION_ID + "_" + BuildConfig.VERSION_NAME;

    private QigsawConfig() {
    }
}
