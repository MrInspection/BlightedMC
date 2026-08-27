package fr.moussax.blightedMC.utils.debug;

import org.bukkit.Bukkit;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for standardized console logging through Spigot's native logger.
 *
 * <p>Delegates log output directly to {@link Bukkit#getLogger()} using standard
 * {@link Level} severity thresholds, with support for optional subsystem tag
 * prefixes.</p>
 */
public final class Log {

    private Log() {
    }

    private static Logger getLogger() {
        return Bukkit.getLogger();
    }

    private static void log(Level level, String message) {
        getLogger().log(level, message);
    }

    private static void log(Level level, String prefix, String message) {
        getLogger().log(level, "[" + prefix + "] " + message);
    }

    /**
     * Logs an informational message.
     *
     * @param message content to log
     */
    public static void info(String message) {
        log(Level.INFO, message);
    }

    /**
     * Logs a warning message.
     *
     * @param message content to log
     */
    public static void warn(String message) {
        log(Level.WARNING, message);
    }

    /**
     * Logs an error message at severe level.
     *
     * @param message content to log
     */
    public static void error(String message) {
        log(Level.SEVERE, message);
    }

    /**
     * Logs a success message at informational level.
     *
     * @param message content to log
     */
    public static void success(String message) {
        log(Level.INFO, message);
    }

    /**
     * Logs a debug message at fine level.
     *
     * @param message content to log
     */
    public static void debug(String message) {
        log(Level.FINE, message);
    }

    /**
     * Logs an informational message with a subsystem prefix tag.
     *
     * @param prefix subsystem tag prefix
     * @param message content to log
     */
    public static void info(String prefix, String message) {
        log(Level.INFO, prefix, message);
    }

    /**
     * Logs a warning message with a subsystem prefix tag.
     *
     * @param prefix subsystem tag prefix
     * @param message content to log
     */
    public static void warn(String prefix, String message) {
        log(Level.WARNING, prefix, message);
    }

    /**
     * Logs a success message at informational level with a subsystem prefix tag.
     *
     * @param prefix subsystem tag prefix
     * @param message content to log
     */
    public static void success(String prefix, String message) {
        log(Level.INFO, prefix, message);
    }

    /**
     * Logs an error message at severe level with a subsystem prefix tag.
     *
     * @param prefix subsystem tag prefix
     * @param message content to log
     */
    public static void error(String prefix, String message) {
        log(Level.SEVERE, prefix, message);
    }

    /**
     * Logs a debug message at fine level with a subsystem prefix tag.
     *
     * @param prefix subsystem tag prefix
     * @param message content to log
     */
    public static void debug(String prefix, String message) {
        log(Level.FINE, prefix, message);
    }
}
