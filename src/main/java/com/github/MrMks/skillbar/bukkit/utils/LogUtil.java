package com.github.MrMks.skillbar.bukkit.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LogUtil {
    private static Logger logger;
    public static void setLogger(Logger logger) {
        LogUtil.logger = logger;
    }

    public static void info(String msg) {
        logger.info(msg);
    }

    public static void info(String msg, Throwable throwable) {
        logger.log(Level.INFO, msg, throwable);
    }

    public static void warn(String msg) {
        logger.warning(msg);
    }

    public static void warn(String msg, Throwable throwable) {
        logger.log(Level.WARNING, msg, throwable);
    }

    public static void server(String msg) {
        logger.severe(msg);
    }

    public static void severe(String msg, Throwable throwable) {
        logger.log(Level.SEVERE, msg, throwable);
    }
}
