package fr.moussax.blightedMC.commands.see;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.smp.core.player.menus.EnderSeeMenu;
import fr.moussax.blightedMC.utils.formatting.Formatter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import static fr.moussax.blightedMC.utils.formatting.Formatter.hasRequiredPermission;

public class EnderSeeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command cmd, @NonNull String label, String @NonNull [] args) {
        if (!(sender instanceof Player player)) return false;
        if (!hasRequiredPermission(player)) return false;

        if (args.length == 0) {
            Formatter.warn(player, "Usage: /endersee <player>");
            return false;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            Formatter.warn(player, "Unable to find player §4" + args[0]);
            return false;
        }

        BlightedMC.menuManager().openMenu(new EnderSeeMenu(target), player);
        return true;
    }
}
