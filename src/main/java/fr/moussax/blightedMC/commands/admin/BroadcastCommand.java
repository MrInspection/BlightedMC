package fr.moussax.blightedMC.commands.admin;

import fr.moussax.blightedMC.server.PluginPermissions;
import fr.moussax.blightedMC.utils.commands.CommandInfo;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import static fr.moussax.blightedMC.utils.formatting.Formatter.hasRequiredPermission;

public class BroadcastCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command cmd, @NonNull String label, String @NonNull [] args) {
        if (!(sender instanceof Player player)) return false;
        if (!hasRequiredPermission(player, PluginPermissions.ADMIN)) return false;

        if (args.length == 0) {
            CommandInfo.sendUsage(player, "Broadcast a message to the server", "broadcast", "<message>");
            return false;
        }

        StringBuilder messageBuilder = new StringBuilder();
        for (String argument : args) {
            messageBuilder.append(argument).append(" ");
        }

        String message = messageBuilder.toString().trim();
        Bukkit.broadcastMessage(" ");
        Bukkit.broadcastMessage(" §6§lBROADCAST! §f" + player.getName() + " §8» §e" + message);
        Bukkit.broadcastMessage(" ");

        return true;
    }
}
