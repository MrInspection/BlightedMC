package fr.moussax.blightedMod.commands.impl;

import fr.moussax.blightedMod.commands.ModerationCommand;
import fr.moussax.blightedMod.moderator.menus.ReportsCenterMenu;
import fr.moussax.blightedMod.moderator.reports.ReportManager;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static fr.moussax.bedrock.text.Messenger.inform;
import static fr.moussax.bedrock.text.Messenger.warn;

public final class ReportsCommand extends ModerationCommand {

    @Override
    protected boolean executeModeration(Player moderator, Command command, String label, String[] arguments) {
        if (arguments.length >= 2 && arguments[0].equalsIgnoreCase("dismiss")) {
            try {
                int reportId = Integer.parseInt(arguments[1]);
                boolean dismissed = ReportManager.getInstance().dismissReport(reportId);
                if (dismissed) {
                    inform(moderator, "§eDismissed report §d#" + reportId +"§e.");
                } else {
                    warn(moderator, "Report #" + reportId + " not found or already dismissed.");
                }
            } catch (NumberFormatException _) {
                warn(moderator, "Invalid report ID.");
            }
            return true;
        }

        new ReportsCenterMenu().open(moderator);
        return true;
    }
}
