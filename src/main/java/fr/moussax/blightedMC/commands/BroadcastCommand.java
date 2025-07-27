package fr.moussax.blightedMC.commands;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class BroadcastCommand implements CommandExecutor {
  @Override
  public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label,@Nonnull String[] args) {
    if(label.equalsIgnoreCase("broadcast") && sender instanceof Player player) {

      if(!player.isOp()) {
        player.sendMessage(" §4§lDENIED! §cYou must be ADMIN to use this command.");
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100f, 0.5f);
        return false;
      }

      if(args.length == 0) {
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 100f, 0.85f);
        player.sendMessage(" §c§lINVALID! §fUsage: §6/§fbroadcast §6<message>§f.");
        return false;
      }

      StringBuilder messageBuilder = new StringBuilder();
      for(String argument : args) {
        messageBuilder.append(argument).append(" ");
      }

      String message = messageBuilder.toString().trim();
      Bukkit.broadcastMessage("\n §3§lBROADCAST! §f" + player.getName() + " §f» §b§l" + message + "\n");
      return true;
    }
    return false;
  }
}
