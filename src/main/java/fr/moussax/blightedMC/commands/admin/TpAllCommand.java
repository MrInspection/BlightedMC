package fr.moussax.blightedMC.commands.admin;

import fr.moussax.blightedMC.server.PluginPermissions;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import static fr.moussax.blightedMC.utils.formatting.Formatter.*;

public class TpAllCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command cmd, @NonNull String label, String @NonNull [] args) {
        if (!(sender instanceof Player player)) return false;
        if (!hasRequiredPermission(player, PluginPermissions.ADMIN)) return false;

        int count = 0;
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (target.getUniqueId().equals(player.getUniqueId())) continue;
            if (target.teleport(player.getLocation())) {
                count++;
            }
        }

        inform(player, "Teleported §d" + count + " §7players to your location.");
        return true;
    }
}
