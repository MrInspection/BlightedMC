package fr.moussax.blightedMC.utils;

import fr.moussax.blightedMC.BlightedMC;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import static com.google.common.base.Strings.repeat;

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

  public static String createProgressBar(double percent, double max, double bar) {
    double filledBars = percent / bar;
    double emptyBars = max - filledBars;

    if(filledBars > max) filledBars = max;

    return ChatColor.DARK_GREEN + repeat("-", (int) filledBars) + ChatColor.WHITE + repeat("-", (int) emptyBars);
  }
}
