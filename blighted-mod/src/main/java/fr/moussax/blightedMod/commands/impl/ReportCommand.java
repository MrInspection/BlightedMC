package fr.moussax.blightedMod.commands.impl;

import fr.moussax.bedrock.commands.PlayerCommand;
import fr.moussax.blightedMod.moderator.menus.ReportMenu;
import fr.moussax.blightedMod.moderator.reports.ReportManager;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static fr.moussax.bedrock.text.Messenger.warn;

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

            if (player.getName().equalsIgnoreCase(targetName)) {
                warn(player, "You cannot report yourself.");
                return false;
            }

            new ReportMenu(targetName, chatMessage).open(player);
            return true;
        }

        if (subAction.equalsIgnoreCase("submit") && arguments.length >= 3) {
            String targetName = arguments[1];
            String reason = arguments[2];
            String chatMessage = arguments.length > 3
                    ? String.join(" ", Arrays.copyOfRange(arguments, 3, arguments.length))
                    : "No message content";

            submitDirectReport(player, targetName, reason, chatMessage);
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
            submitDirectReport(player, target.getName(), reason, "General player report");
        } else {
            new ReportMenu(target.getName()).open(player);
        }

        return true;
    }

    private void submitDirectReport(Player reporter, String targetName, String reason, String chatMessage) {
        ReportManager.getInstance().submitAndNotify(reporter, targetName, reason, chatMessage);
    }
}
