package fr.moussax.blightedMC.smp.core.managers;

import fr.moussax.blightedMC.BlightedMC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.text.DecimalFormat;

public class TabManager {
    private final BlightedMC plugin;
    private BukkitTask task;
    private final DecimalFormat tpsFormat = new DecimalFormat("0.00");

    public TabManager(BlightedMC plugin) {
        this.plugin = plugin;
    }

    public void start() {
        if (task != null && !task.isCancelled()) return;
        task = Bukkit.getScheduler().runTaskTimer(plugin, this::updateAll, 0L, 20L);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    public void updateAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            update(player);
        }
    }

    private void update(Player player) {
        if (player == null) return;
        int ping = player.getPing();

        String pingColor = (ping < 100) ? "§a" : (ping < 200 ? "§e" : "§c");
        String pingDisplay = pingColor + ping;

        String header = """
           \s
             §f§lBLIGHTED SMP\s
             §7Ultra Hard Survival\s
           """;
        String footer = "\n§7Ping: %sms\n".formatted(pingDisplay);

        player.setPlayerListHeaderFooter(header, footer);
    }
}
