package fr.moussax.bedrock.scheduling;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jspecify.annotations.NonNull;

/**
 * Utility for accessing the main plugin instance and scheduling synchronous tasks.
 */
public final class PluginContext {

    private static volatile Plugin plugin;

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
     * Schedules a task to run synchronously on the next server tick.
     *
     * @param runnable the task to execute
     * @return the scheduled task handle
     */
    public static BukkitTask run(@NonNull Runnable runnable) {
        return Bukkit.getScheduler().runTask(get(), runnable);
    }

    /**
     * Schedules a task to run after a specified number of server ticks.
     *
     * @param runnable the task to execute
     * @param ticks    number of server ticks to wait before execution
     * @return the scheduled task handle
     */
    public static BukkitTask delay(@NonNull Runnable runnable, long ticks) {
        return Bukkit.getScheduler().runTaskLater(get(), runnable, ticks);
    }
}
