package fr.moussax.blightedMC.commands.admin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

import static fr.moussax.blightedMC.utils.formatting.Formatter.inform;

public class FlyCommand implements CommandExecutor {
  @Override
  public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args) {
    if (!label.equalsIgnoreCase("fly") || !(sender instanceof Player player)) return false;
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
