package com.electrolytej.assembler.util;

public class StringUtil {
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static void eachAfterSplit(String src, String reg, Callback cb) {
        if (isEmpty(src)) return;
        String[] split = src.split(reg);
        for (String name : split) {
            cb.on(name);
        }
    }

    interface Callback {
        void on(String str);
    }

}
