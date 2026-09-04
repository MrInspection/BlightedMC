package fr.moussax.blightedMod.commands.impl;

import fr.moussax.bedrock.commands.CommandArgument;
import fr.moussax.blightedMod.commands.ModerationCommand;
import fr.moussax.blightedMod.moderator.menus.SanctionsMenu;
import fr.moussax.blightedMod.moderator.punishments.PunishmentData;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.List;

import static fr.moussax.bedrock.text.Messenger.warn;

/**
 * Moderation command to view all sanctions for a player (/sanctions <player>).
 */
@CommandArgument(position = 0, suggestions = {"$players"})
public final class SanctionsCommand extends ModerationCommand {

    @Override
    protected boolean executeModeration(Player moderator, Command command, String label, String[] arguments) {
        if (arguments.length == 0) {
            warn(moderator, "Usage: /sanctions <player>");
            return false;
        }

        String targetName = arguments[0];
        List<PunishmentData> punishments = getPunishmentManager().getAllPunishments(targetName);

        if (punishments.isEmpty()) {
            warn(moderator, "No recorded sanctions found for player §4" + targetName + "§c.");
            return true;
        }

        new SanctionsMenu(targetName, punishments).open(moderator);
        return true;
    }
}
