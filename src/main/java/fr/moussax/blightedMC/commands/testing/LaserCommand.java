package fr.moussax.blightedMC.commands.testing;

import fr.moussax.blightedMC.core.entities.abilities.LaserBeam;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class LaserCommand implements CommandExecutor {
  @Override
  public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args) {
    if(label.equalsIgnoreCase("laser") && sender instanceof Player player) {
      player.sendMessage("§8 ■ §7You summoned a laser beam. §a(Magic Test)");
      new LaserBeam(player.getLocation(), player.getLocation().add(0,0,5)).rotateAroundStartY(45);
      return true;
    }
    return false;
  }
}
