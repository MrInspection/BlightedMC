package fr.moussax.blightedMod.commands.impl;

import fr.moussax.bedrock.commands.PlayerCommand;
import fr.moussax.bedrock.text.InteractiveMessage;
import fr.moussax.bedrock.ui.book.BookMenu;
import fr.moussax.blightedMod.moderator.ModerationManager;
import fr.moussax.blightedMod.moderator.reports.ReportData;
import fr.moussax.blightedMod.moderator.reports.ReportManager;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static fr.moussax.bedrock.text.Messenger.inform;
import static fr.moussax.bedrock.text.Messenger.warn;

/**
 * Handles /report command execution for general reports and chat reports with BookMenu interface.
 */
public final class ReportCommand extends PlayerCommand {

    @Override
    protected boolean execute(Player player, Command command, String label, String[] arguments) {
        if (arguments.length == 0) {
            warn(player, "Usage: /report <player> [reason] or click a chat flag ⚑.");
            return false;
        }

        String subAction = arguments[0];

        if (subAction.equalsIgnoreCase("chat") && arguments.length >= 2) {
            String targetName = arguments[1];
            String chatMessage = arguments.length > 2
                    ? String.join(" ", Arrays.copyOfRange(arguments, 2, arguments.length))
                    : "No message content";

            openChatReportBook(player, targetName, chatMessage);
            return true;
        }

        if (subAction.equalsIgnoreCase("submit") && arguments.length >= 3) {
            String targetName = arguments[1];
            String reason = arguments[2];
            String chatMessage = arguments.length > 3
                    ? String.join(" ", Arrays.copyOfRange(arguments, 3, arguments.length))
                    : "No message content";

            submitChatReport(player, targetName, reason, chatMessage);
            return true;
        }

        Player target = requireTarget(player, subAction);
        if (target == null) {
            return false;
        }

        if (target.equals(player)) {
            warn(player, "You cannot report yourself.");
            return false;
        }

        if (arguments.length > 1) {
            String reason = String.join(" ", Arrays.copyOfRange(arguments, 1, arguments.length));
            submitGeneralReport(player, target.getName(), reason);
        } else {
            openGeneralReportBook(player, target.getName());
        }

        return true;
    }

    private void openChatReportBook(Player reporter, String targetName, String chatMessage) {
        if (reporter.getName().equalsIgnoreCase(targetName)) {
            warn(reporter, "You cannot report yourself.");
            return;
        }

        BookMenu bookMenu = BookMenu.builder()
                .addPage(page -> page
                        .append("  §1§lCHAT REPORT\n")
                        .append("§0Reporting: §c" + targetName + "\n\n")
                        .append("§0Message:\n")
                        .append("§8\"§7" + chatMessage + "§8\"\n\n")
                        .append("§0Select Reason:\n")
                        .hoverAndExecute("§c▶ Abuse / Harassment\n", "§eSubmit report for Abuse / Harassment", "/report submit " + targetName + " Abuse/Harassment " + chatMessage)
                        .hoverAndExecute("§c▶ Spam / Flood\n", "§eSubmit report for Spam / Flood", "/report submit " + targetName + " Spam/Flood " + chatMessage)
                        .hoverAndExecute("§c▶ Inappropriate Content\n", "§eSubmit report for Inappropriate Content", "/report submit " + targetName + " Inappropriate " + chatMessage)
                        .hoverAndExecute("§c▶ Advertising / Scam", "§eSubmit report for Advertising / Scam", "/report submit " + targetName + " Advertising/Scam " + chatMessage)
                );

        bookMenu.open(reporter);
    }

    private void openGeneralReportBook(Player reporter, String targetName) {
        BookMenu bookMenu = BookMenu.builder()
                .addPage(page -> page
                        .append("  §1§lPLAYER REPORT\n")
                        .append("§0Reporting: §c" + targetName + "\n\n")
                        .append("§0Select Reason:\n")
                        .hoverAndExecute("§c▶ Cheating / Hacks\n", "§eSubmit report for Cheating / Hacks", "/report submit " + targetName + " Cheating None")
                        .hoverAndExecute("§c▶ Toxicity / Abuse\n", "§eSubmit report for Toxicity / Abuse", "/report submit " + targetName + " Toxicity None")
                        .hoverAndExecute("§c▶ Griefing / Scamming\n", "§eSubmit report for Griefing / Scamming", "/report submit " + targetName + " Griefing None")
                        .hoverAndExecute("§c▶ Cross-Teaming", "§eSubmit report for Cross-Teaming", "/report submit " + targetName + " Cross-Teaming None")
                );

        bookMenu.open(reporter);
    }

    private void submitChatReport(Player reporter, String targetName, String reason, String chatMessage) {
        ReportData report = ReportManager.getInstance().submitReport(reporter.getName(), targetName, reason, chatMessage);

        InteractiveMessage notificationMessage = InteractiveMessage.text(" §6§lALERT! §f" + reporter.getName() + " §ereported §d" + targetName + "§e's chat message: §7\"" + chatMessage + "\"§e. ")
                .hoverAndExecute("§6[DETAILS]", "§fClick to view §dreport §fdetails.", "/checkreport " + report.id())
                .append(" ")
                .hoverAndExecute("§b[MTP]", "§fClick to teleport to §d" + targetName + "§f.", "/mtp " + targetName)
                .append(" ")
                .hoverAndExecute("§3[INFO]", "§fClick to view information about §d" + targetName + "§f.", "/userinfo " + targetName);

        ModerationManager.getInstance().broadcastToModerators(notificationMessage);
        inform(reporter, " §a§lREPORT SENT! §7Your report for §d" + targetName + " §7has been submitted to online staff.");
    }

    private void submitGeneralReport(Player reporter, String targetName, String reason) {
        ReportData report = ReportManager.getInstance().submitReport(reporter.getName(), targetName, reason, "General player report");

        InteractiveMessage notificationMessage = InteractiveMessage.text(" §6§lALERT! §d" + targetName + " §ewas reported. ")
                .hoverAndExecute("§6[DETAILS]", "§fClick to view §dreport §fdetails.", "/checkreport " + report.id())
                .append(" ")
                .hoverAndExecute("§b[MTP]", "§fClick to teleport to §d" + targetName + "§f.", "/mtp " + targetName)
                .append(" ")
                .hoverAndExecute("§3[INFO]", "§fClick to view information about §d" + targetName + "§f.", "/userinfo " + targetName);

        ModerationManager.getInstance().broadcastToModerators(notificationMessage);
        inform(reporter, " §a§lREPORT SENT! §7Your report for §d" + targetName + " §7has been submitted to online staff.");
    }
}
