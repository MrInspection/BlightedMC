package fr.moussax.blightedMC.commands.admin;

import fr.moussax.blightedMC.utils.formatting.CommandInfo;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;

import static fr.moussax.blightedMC.utils.formatting.Formatter.*;

public class ForceCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command cmd, String label, String @NonNull [] args) {
        if (!(label.equalsIgnoreCase("forcecommand") && sender instanceof Player player)) return false;
        if (!enforceAdminPermission(player)) return false;

        if (args.length < 2) {
            CommandInfo.sendUsage(player, "Force a player to execute a command",
                "forcecommand", "<player>", "[command]");
            return false;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            warn(player, "Unable to find the player §4" + args[0] + "§c.");
            return false;
        }

        if (target.equals(player)) {
            warn(player, "You can't force yourself to execute a command.");
            return false;
        }

        String commandToExecute = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        if (commandToExecute.startsWith("forcecommand ")) {
            warn(player, "You can't force a player to use the §4forcecommand§c.");
            return false;
        }

        TextComponent message = new TextComponent("§8 ■ §7You forced §d" + target.getName() + "§7 to execute a ");
        TextComponent commandWord = createInteractiveText(
            "§f§lCOMMAND",
            "§7Click to fill §dcommand §7in chat",
            ClickEvent.Action.SUGGEST_COMMAND,
            "/" + commandToExecute
        );
        TextComponent afterCommand = new TextComponent("§7.");

        message.addExtra(commandWord);
        message.addExtra(afterCommand);
        player.spigot().sendMessage(message);

        Bukkit.dispatchCommand(target, commandToExecute);
        return true;
    }
}
