package fr.moussax.blightedMod.commands.impl;

import fr.moussax.blightedMod.commands.ModerationCommand;
import fr.moussax.blightedMod.moderator.BlightedModerator;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public final class VanishCommand extends ModerationCommand {

    @Override
    protected boolean executeModeration(Player moderator, Command command, String label, String[] arguments) {
        BlightedModerator currentModerator = getModerationManager().getModerator(moderator);
        boolean newVanishState = !currentModerator.isVanished();
        currentModerator.setVanished(newVanishState);
        return true;
    }
}
