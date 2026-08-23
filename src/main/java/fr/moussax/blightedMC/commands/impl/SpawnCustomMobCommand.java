package fr.moussax.blightedMC.commands.impl;

import fr.moussax.blightedMC.commands.AdminCommand;
import fr.moussax.blightedMC.commands.utils.CommandFormatter;
import fr.moussax.blightedMC.engine.entities.BlightedEntity;
import fr.moussax.blightedMC.engine.entities.registry.EntitiesRegistry;
import fr.moussax.blightedMC.commands.utils.CommandArgument;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static fr.moussax.blightedMC.utils.Formatter.*;

@CommandArgument(position = 0, suggestions = {"$entities"})
public final class SpawnCustomMobCommand extends AdminCommand {
    @Override
    protected boolean executeAdmin(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            CommandFormatter.sendUsage(
                    player,
                    CommandFormatter.CommandInfo.of(
                            "spawncustommob <entity>",
                            "Summon a custom mob"
                    )
            );
            return false;
        }

        BlightedEntity entity = EntitiesRegistry.get(args[0].toUpperCase());

        if (entity == null) {
            warn(player, "Unable to find §4" + args[0].toUpperCase() + " §cinto the registry.");
            return false;
        }

        try {
            entity.spawn(player.getLocation());
            inform(player, " §eSummoned §d" + entity.getName() + "§7.");
            return true;
        } catch (Exception e) {
            warn(player, "Unable to spawn the entity §4" + entity.getName() + "§c.");
            return false;
        }
    }
}
