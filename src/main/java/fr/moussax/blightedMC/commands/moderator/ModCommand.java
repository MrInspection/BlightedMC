package fr.moussax.blightedMC.commands.moderator;

import fr.moussax.blightedMC.server.PluginPermissions;
import fr.moussax.blightedMC.smp.core.player.mod.ModerationManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

import static fr.moussax.blightedMC.utils.formatting.Formatter.hasRequiredPermission;

public class ModCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args) {
        if (!(sender instanceof Player player)) return false;
        if (!hasRequiredPermission(player, PluginPermissions.MODERATOR)) return false;

        if (args.length > 0) {
            String message = String.join(" ", args);
            String formatted = " §9§lMOD §9" + player.getName() + " §f§l» §e§l" + message;
            Bukkit.broadcastMessage(" ");
            Bukkit.broadcastMessage(formatted);
            Bukkit.broadcastMessage(" ");
            return true;
        }

        ModerationManager.getInstance().toggleModerationMode(player);
        return true;
    }
}
