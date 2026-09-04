package fr.moussax.blightedMod.commands.impl;

import fr.moussax.blightedMod.commands.ModerationCommand;
import fr.moussax.blightedMod.moderator.menus.ReportsCenterMenu;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

/**
 * Moderation command to view active player reports via a paginated menu (/reports).
 */
public final class ReportsCommand extends ModerationCommand {

    @Override
    protected boolean executeModeration(Player moderator, Command command, String label, String[] arguments) {
        new ReportsCenterMenu().open(moderator);
        return true;
    }
}
