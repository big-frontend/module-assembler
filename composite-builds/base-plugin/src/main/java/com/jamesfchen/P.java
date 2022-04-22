package com.jamesfchen;

import org.gradle.api.Project;
import org.gradle.api.logging.Logger;

public final class P {

    public static final int L_VERBOSE = 0;
    public static final int L_DEBUG = 1;
    public static final int L_INFO = 2;
    public static final int L_WARN = 3;
    public static final int L_ERROR = 4;
    public static final int CURRENT_LEVEL = L_INFO;
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";
    private static Logger logger;

    public static void init(Project project) {
        logger = project.getLogger();
    }

    private static void checkLogger() {
        if (logger == null) {
            throw new IllegalArgumentException("没有初始化Logger");
        }
    }
//    public static void lifecycle(String message) {
//        checkLogger();
//        logger.lifecycle(ANSI_YELLOW + "" + message + "" + ANSI_RESET);
//    }
//    public static void error(String message) {
//        checkLogger();
//        logger.error(message);
//    }
//    public static void warn(String message) {
//        checkLogger();
//        logger.warn(message );
//    }
//
//    public static void info(String message) {
//        checkLogger();
//        logger.info(message);
//    }
//
//    public static void debug(String message) {
//        checkLogger();
//        logger.debug(message);
//    }

    private static void println(Object message) {
        System.out.println(message);
    }

    public static void error(Object message) {
        if (CURRENT_LEVEL > L_ERROR) return;
        println(ANSI_RED + "" + message + "" + ANSI_RESET);
    }

    public static void warn(Object message) {
        if (CURRENT_LEVEL > L_WARN) return;
        println(ANSI_YELLOW + "" + message + "" + ANSI_RESET);
    }

    public static void info(Object message) {
        if (CURRENT_LEVEL > L_INFO) return;
        println(ANSI_GREEN + "" + message + "" + ANSI_RESET);
    }

    public static void debug(Object message) {
        if (CURRENT_LEVEL > L_DEBUG) return;
        println(ANSI_BLUE + "" + message + "" + ANSI_RESET);
    }

    public static void verbose(Object message) {
        if (CURRENT_LEVEL > L_VERBOSE) return;
        println(message);
    }

    public static void child(Object msg) {
        println("👶[ gradle 开始 ] " + msg);
    }

    public static void teenager(Object msg) {
        println("👩‍🎓👨‍🎓[ initialzation ] " + msg);
    }

    public static void middleAge(Object msg) {
        println("👰🤵[ configuration ] " + msg);
    }

    public static void theElderly(Object msg) {
        println("👵👴[ gradle 结束 ] " + msg);
    }
}
