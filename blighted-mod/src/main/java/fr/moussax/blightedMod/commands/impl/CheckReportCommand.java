package fr.moussax.blightedMod.commands.impl;

import fr.moussax.bedrock.ui.book.BookMenu;
import fr.moussax.blightedMod.commands.ModerationCommand;
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

        BookMenu bookMenu = BookMenu.builder()
                .addPage(page -> page
                        .append("  §1§lREPORT DETAILS\n")
                        .append("§0Report ID: §8#" + report.id() + "\n")
                        .append("§0Target: §c" + report.targetName() + "\n")
                        .append("§0Reporter: §9" + report.reporterName() + "\n\n")
                        .append("§0Reason: §8" + report.reason() + "\n\n")
                        .append("§0Message:\n")
                        .append("§8\"§7" + report.message() + "§8\"\n\n")
                        .hoverAndExecute("§c▶ Dismiss Report", "§eClick to dismiss this report", "/reports dismiss " + report.id())
                );

        bookMenu.open(moderator);
        return true;
    }
}
