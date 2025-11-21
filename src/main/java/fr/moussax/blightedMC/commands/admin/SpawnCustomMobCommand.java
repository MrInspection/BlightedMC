package fr.moussax.blightedMC.commands.admin;

import fr.moussax.blightedMC.core.entities.BlightedEntity;
import fr.moussax.blightedMC.core.entities.registry.EntitiesRegistry;
import fr.moussax.blightedMC.utils.commands.CommandArgument;
import fr.moussax.blightedMC.utils.formatting.CommandInfo;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

import static fr.moussax.blightedMC.utils.formatting.Formatter.*;

@CommandArgument(suggestions = {"$entities"})
public class SpawnCustomMobCommand implements CommandExecutor {
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args) {
        if (!label.equalsIgnoreCase("spawncustommob") || !(sender instanceof Player player)) return false;
        if (!enforceAdminPermission(player)) return false;

        if (args.length == 0) {
            CommandInfo.sendUsage(player, "Summon a custom mob", "spawncustommob", "<entity>");
            return false;
        }

        BlightedEntity entity = EntitiesRegistry.getEntity(args[0].toUpperCase());

        if (entity == null) {
            warn(player, "Unable to find §4" + args[0].toUpperCase() + " §cin the registry.");
            return false;
        }

        try {
            entity.spawn(player.getLocation());
            inform(player, "You summoned §d" + entity.getName() + "§7.");
            return true;
        } catch (Exception e) {
            warn(player, "Unable to spawn the entity §4" + entity.getName() + "§c.");
            return false;
        }
    }
}
