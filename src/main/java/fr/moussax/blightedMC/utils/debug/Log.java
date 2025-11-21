package fr.moussax.blightedMC.utils.debug;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for formatted console logging with colored output.
 * Provides methods for logging messages at different levels:
 * INFO, WARN, ERROR, and DEBUG, with optional custom prefixes.
 */
public final class Log {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private static final String RESET = "\u001B[0m";
    private static final String LOG_INFO_COLOR = "\u001B[36m";
    private static final String LOG_WARNING_COLOR = "\u001B[33m";
    private static final String LOG_SUCCESS_COLOR = "\u001B[32m";
    private static final String LOG_ERROR_COLOR = "\u001B[31m";
    private static final String LOG_DEBUG_COLOR = "\u001B[1;35m";

    private static boolean includeTimestamp = true;

    private Log() {
    }

    public static void setIncludeTimestamp(boolean include) {
        includeTimestamp = include;
    }

    private static void print(String level, String message, String color) {
        if (includeTimestamp) {
            String timestamp = LocalDateTime.now().format(FORMATTER);
            System.out.println(color + "[" + timestamp + "] [" + level + "]: " + message + RESET);
        } else {
            System.out.println(color + "[" + level + "]: " + message + RESET);
        }
    }

    private static void print(String prefix, String level, String message, String color) {
        if (includeTimestamp) {
            String timestamp = LocalDateTime.now().format(FORMATTER);
            System.out.println(color + "[" + timestamp + "] [" + prefix + "/" + level + "]: " + message + RESET);
        } else {
            System.out.println(color + "[" + prefix + "/" + level + "]: " + message + RESET);
        }
    }

    public static void info(String message) {
        print("INFO", message, LOG_INFO_COLOR);
    }

    public static void warn(String message) {
        print("WARN", message, LOG_WARNING_COLOR);
    }

    public static void error(String message) {
        print("ERROR", message, LOG_ERROR_COLOR);
    }

    public static void success(String message) {
        print("SUCCESS", message, LOG_SUCCESS_COLOR);
    }

    public static void debug(String message) {
        print("DEBUG", message, LOG_DEBUG_COLOR);
    }

    public static void info(String prefix, String message) {
        print(prefix, "INFO", message, LOG_INFO_COLOR);
    }

    public static void warn(String prefix, String message) {
        print(prefix, "WARN", message, LOG_WARNING_COLOR);
    }

    public static void success(String prefix, String message) {
        print(prefix, "SUCCESS", message, LOG_SUCCESS_COLOR);
    }

    public static void error(String prefix, String message) {
        print(prefix, "ERROR", message, LOG_ERROR_COLOR);
    }

    public static void debug(String prefix, String message) {
        print(prefix, "DEBUG", message, LOG_DEBUG_COLOR);
    }
}