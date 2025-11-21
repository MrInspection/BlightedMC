package fr.moussax.blightedMC.utils;

import fr.moussax.blightedMC.BlightedMC;
import org.bukkit.scheduler.BukkitRunnable;

public class Utilities {

    public static void delay(Runnable runnable, int ticks) {
        delay(runnable, (long) ticks);
    }

    public static void delay(Runnable runnable, long ticks) {
        new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskLater(BlightedMC.getInstance(), ticks);
    }
}
