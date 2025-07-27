package fr.moussax.blightedMC.commands;

import fr.moussax.blightedMC.gameplay.mobs.ExperimentalMob;
import fr.moussax.blightedMC.gameplay.mobs.GaiaConstruct;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class SpawnMobCommand implements CommandExecutor {
  @Override
  public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args) {

    if(label.equalsIgnoreCase("spawnmob") && sender instanceof Player player) {

      if(!player.hasPermission("blightedmc.spawnmob")) {
        player.sendMessage(" §cYou must be §4ADMIN §cto use this command.");
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100f, 0.5f);
        return false;
      }

      switch (args[0].toUpperCase()) {
        case "EXPERIMENT":
          ExperimentalMob experimental = new ExperimentalMob();
          experimental.spawn(player.getLocation());
          player.sendMessage("§6§lSUCCESS! §7You spawned §6EXPERIMENTAL_MOB§7.");
          break;
        case "GAIA":
          GaiaConstruct gaiaConstruct = new GaiaConstruct(true);
          gaiaConstruct.spawn(player.getLocation());
          player.sendMessage(" §7You summoned §6Gaia Construct");
          break;
        default:
          break;
      }
      return true;
    }
    return false;
  }
}
