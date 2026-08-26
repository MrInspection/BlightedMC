package fr.moussax.blightedMC.shared.scheduling;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NonNull;

/**
 * Utility for accessing the main plugin instance and scheduling delayed tasks.
 */
public final class PluginContext {

    private static Plugin plugin;

    private PluginContext() {
    }

    /**
     * Binds the main plugin instance to this context.
     *
     * @param pluginInstance the plugin instance
     */
    public static void bind(@NonNull Plugin pluginInstance) {
        plugin = pluginInstance;
    }

    /**
     * Retrieves the bound plugin instance.
     *
     * @return the bound plugin instance
     * @throws IllegalStateException if {@link #bind(Plugin)} was never called
     */
    public static Plugin get() {
        if (plugin == null) {
            throw new IllegalStateException("PluginContext.bind(Plugin) was never called.");
        }
        return plugin;
    }

    /**
     * Schedules a task to run after a specified number of server ticks.
     *
     * @param runnable the task to execute
     * @param ticks    number of server ticks to wait before execution
     */
    public static void delay(@NonNull Runnable runnable, long ticks) {
        Bukkit.getScheduler().runTaskLater(get(), runnable, ticks);
    }
}
