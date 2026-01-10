package fr.moussax.blightedMC.commands.moderator;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.server.PluginPermissions;
import fr.moussax.blightedMC.smp.core.player.mod.menus.InvSeeMenu;
import fr.moussax.blightedMC.utils.formatting.Formatter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import static fr.moussax.blightedMC.utils.formatting.Formatter.hasRequiredPermission;

public class InvSeeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command cmd, @NonNull String label, String @NonNull [] args) {
        if (!(sender instanceof Player player)) return false;
        if (!hasRequiredPermission(player, PluginPermissions.MODERATOR)) return false;

        if (args.length == 0) {
            Formatter.warn(player, "Usage: /invsee <player>");
            return false;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            Formatter.warn(player, "Unable to find player §4" + args[0]);
            return false;
        }

        BlightedMC.menuManager().openMenu(new InvSeeMenu(target), player);
        return true;
    }
}
