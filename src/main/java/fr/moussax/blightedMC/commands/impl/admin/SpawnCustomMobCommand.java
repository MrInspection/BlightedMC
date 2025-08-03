package fr.moussax.blightedMC.commands.impl.admin;

import fr.moussax.blightedMC.commands.CommandArgument;
import fr.moussax.blightedMC.core.entities.BlightedEntity;
import fr.moussax.blightedMC.core.entities.EntitiesRegistry;
import fr.moussax.blightedMC.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

@CommandArgument(suggestions = {"$entities"})
public class SpawnCustomMobCommand implements CommandExecutor {
  public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args) {
    if (!label.equalsIgnoreCase("spawncustommob") || !(sender instanceof Player player)) return false;
    MessageUtils.enforceAdminPermission(player);

    if (args.length == 0) {
      MessageUtils.informSender(sender,
        "",
        "§8 ■ §7Usage: §6/§rspawncustommob §6<§rentity§6>",
        "§8 ■ §7Description: §eSummon BlightedMC custom mobs.",
        ""
      );
      return false;
    }

    BlightedEntity entity = EntitiesRegistry.getEntity(args[0].toUpperCase());

    if(entity == null) {
      MessageUtils.warnSender(player, "Unable to find §4" + args[0].toUpperCase() + " §cin the registry.");
      return false;
    }

    try {
      entity.spawn(player.getLocation());
      MessageUtils.informSender(player, "§8 ■ §7You summoned §d" + entity.getName() + "§7.");
      return true;
    } catch (Exception e) {
      MessageUtils.warnSender(player, "Unable to spawn to");
      return false;
    }
  }
}
