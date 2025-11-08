package fr.moussax.blightedMC.commands.admin;

import fr.moussax.blightedMC.utils.formatting.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class FlyCommand implements CommandExecutor {
  @Override
  public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args) {
    if (!label.equalsIgnoreCase("fly") || !(sender instanceof Player player)) return false;
    toggleFlightMode(player);
    return true;
  }

  private void toggleFlightMode(Player p) {
    if (p.getAllowFlight()) {
      p.setAllowFlight(false);
      p.setFlying(false);
      MessageUtils.informSender(p, "Flight mode disabled.");
    } else {
      p.setAllowFlight(true);
      p.setFlying(true);
      MessageUtils.informSender(p, "Flight mode enabled.");
    }
  }
}
