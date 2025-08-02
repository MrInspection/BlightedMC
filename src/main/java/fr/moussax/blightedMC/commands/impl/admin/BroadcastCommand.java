package fr.moussax.blightedMC.commands.impl.admin;

import fr.moussax.blightedMC.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class BroadcastCommand implements CommandExecutor {
  @Override
  public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args) {
    if (!label.equalsIgnoreCase("broadcast") || !(sender instanceof Player player)) return false;
    MessageUtils.enforceAdminPermission(player);

    if (args.length == 0) {
      MessageUtils.informSender(player,
        " ",
        "§8 ■ §7Usage: §6/§rbroadcast §6<§fmessage§6>",
        "§8 ■ §7Description: §eBroadcast a message to the server.",
        " "
      );
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
