package fr.moussax.blightedMC.utils;

import fr.moussax.blightedMC.BlightedMC;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Utility class providing general-purpose helper methods for the BlightedMC plugin.
 *
 * <p>Includes methods for delayed task execution using the Bukkit scheduler.</p>
 */
public class Utilities {

    /**
     * Schedules a task to run after a specified number of server ticks.
     *
     * @param runnable the task to execute
     * @param ticks    number of server ticks to wait before execution
     */
    public static void delay(Runnable runnable, int ticks) {
        delay(runnable, (long) ticks);
    }

    /**
     * Schedules a task to run after a specified number of server ticks.
     *
     * @param runnable the task to execute
     * @param ticks    number of server ticks to wait before execution
     */
    public static void delay(Runnable runnable, long ticks) {
        new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskLater(BlightedMC.getInstance(), ticks);
    }
}
