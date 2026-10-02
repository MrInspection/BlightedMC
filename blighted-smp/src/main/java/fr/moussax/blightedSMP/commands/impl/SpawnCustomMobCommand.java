package fr.moussax.blightedSMP.commands.impl;

import fr.moussax.blightedSMP.commands.AdminCommand;
import fr.moussax.bedrock.commands.CommandArgument;
import fr.moussax.blightedSMP.engine.entities.BlightedEntity;
import fr.moussax.blightedSMP.engine.entities.registry.EntitiesRegistry;
import fr.moussax.blightedSMP.engine.entities.rituals.AncientCreature;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static fr.moussax.bedrock.text.Messenger.inform;
import static fr.moussax.bedrock.text.Messenger.warn;

@CommandArgument(position = 0, suggestions = {"$entities"})
public final class SpawnCustomMobCommand extends AdminCommand {
    @Override
    protected boolean executeAdmin(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            warn(player, "Usage: /spawncustommob <entity>");
            return false;
        }

        BlightedEntity entity = EntitiesRegistry.create(args[0].toUpperCase());

        if (entity == null) {
            warn(player, "Unable to find §4" + args[0].toUpperCase() + " §cinto the registry.");
            return false;
        }

        if (entity instanceof AncientCreature ancientCreature) {
            ancientCreature.summoner(player);
        }

        try {
            entity.spawn(player.getLocation());
            inform(player, " §eSummoned §d" + entity.getName() + "§e.");
            return true;
        } catch (Exception exception) {
            warn(player, "Unable to spawn the entity §4" + entity.getName() + "§c.");
            return false;
        }
    }
}
