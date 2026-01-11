package fr.moussax.blightedMC.commands.admin;

import fr.moussax.blightedMC.server.PluginPermissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import static fr.moussax.blightedMC.utils.formatting.Formatter.*;

public class FlyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command cmd, @NonNull String label, String @NonNull [] args) {
        if (!(sender instanceof Player player)) return false;
        if (!hasRequiredPermission(player, PluginPermissions.ADMIN)) return false;

        toggleFlightMode(player);
        return true;
    }

    private void toggleFlightMode(Player player) {
        if (player.getAllowFlight()) {
            player.setAllowFlight(false);
            player.setFlying(false);
            inform(player, "Flight mode disabled.");
        } else {
            player.setAllowFlight(true);
            player.setFlying(true);
            inform(player, "Flight mode enabled.");
        }
    }
}
