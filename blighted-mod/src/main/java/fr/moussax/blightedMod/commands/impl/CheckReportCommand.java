package fr.moussax.blightedMod.commands.impl;

import fr.moussax.blightedMod.commands.ModerationCommand;
import fr.moussax.blightedMod.moderator.menus.ReportsCenterMenu;
import fr.moussax.blightedMod.moderator.reports.ReportData;
import fr.moussax.blightedMod.moderator.reports.ReportManager;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static fr.moussax.bedrock.text.Messenger.warn;

public final class CheckReportCommand extends ModerationCommand {

    @Override
    protected boolean executeModeration(Player moderator, Command command, String label, String[] arguments) {
        if (arguments.length == 0) {
            warn(moderator, "Usage: /checkreport <id>");
            return false;
        }

        int reportId;
        try {
            reportId = Integer.parseInt(arguments[0]);
        } catch (NumberFormatException _) {
            warn(moderator, "Invalid report ID format.");
            return false;
        }

        ReportData report = ReportManager.getInstance().getReportById(reportId);
        if (report == null) {
            warn(moderator, "Report #" + reportId + " not found or has been resolved.");
            return true;
        }

        ReportsCenterMenu.openReportBook(moderator, report);
        return true;
    }
}
