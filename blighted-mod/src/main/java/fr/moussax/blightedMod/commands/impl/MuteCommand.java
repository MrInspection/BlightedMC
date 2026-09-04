package fr.moussax.blightedMod.commands.impl;

import fr.moussax.bedrock.commands.CommandArgument;
import fr.moussax.blightedMod.commands.ModerationCommand;
import fr.moussax.blightedMod.moderator.punishments.DurationParser;
import fr.moussax.blightedMod.moderator.punishments.PunishmentData;
import fr.moussax.blightedMod.moderator.punishments.PunishmentManager;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static fr.moussax.bedrock.text.Messenger.warn;

@CommandArgument(position = 0, suggestions = {"$players"})
public final class MuteCommand extends ModerationCommand {
    private static final String PREFIX = " §9§lMOD §f| §7";

    @Override
    protected boolean executeModeration(Player moderator, Command command, String label, String[] arguments) {
        if (label.equalsIgnoreCase("unmute")) {
            return handleUnmute(moderator, arguments);
        }

        return handleMute(moderator, arguments);
    }

    private boolean handleMute(Player moderator, String[] arguments) {
        if (arguments.length < 1) {
            warn(moderator, "Usage: /mute <player> [duration] [reason]");
            moderator.sendMessage("§7Duration format: 1d, 3w, 1m, 1y (omit for permanent)");
            return false;
        }

        Player target = requireTarget(moderator, arguments[0]);
        if (target == null) {
            return false;
        }

        if (target.equals(moderator)) {
            warn(moderator, "You cannot mute yourself.");
            return false;
        }

        if (getModerationManager().isModerator(target)) {
            warn(moderator, "You cannot mute another moderator.");
            return false;
        }

        Long expiresAt = arguments.length > 1 ? DurationParser.parseDuration(arguments[1]) : null;
        int reasonStartIndex = expiresAt != null ? 2 : 1;

        String reason = arguments.length > reasonStartIndex
                ? String.join(" ", Arrays.copyOfRange(arguments, reasonStartIndex, arguments.length))
                : "No reason specified";
        String ipAddress = PunishmentManager.getPlayerIp(target);

        getPunishmentManager().addPunishment(
                target.getUniqueId(),
                target.getName(),
                PunishmentData.PunishmentType.MUTE,
                reason,
                moderator.getUniqueId(),
                moderator.getName(),
                expiresAt,
                ipAddress
        );

        String durationText = expiresAt != null ? " (" + DurationParser.formatDuration(arguments[1]) + ")" : " (Permanent)";
        String notification = PREFIX + "§9" + moderator.getName() + "§e muted §d" + target.getName()
                + "§e for §f" + reason + "§6" + durationText;

        getModerationManager().broadcastToModerators(notification);
        target.sendMessage(" §f§lSANCTION §f| §7You have been muted for: §f" + reason + durationText);

        return true;
    }

    private boolean handleUnmute(Player moderator, String[] arguments) {
        if (arguments.length < 1) {
            warn(moderator, "Usage: /unmute <player>");
            return false;
        }

        Player target = requireTarget(moderator, arguments[0]);
        if (target == null) {
            return false;
        }

        if (!getPunishmentManager().isMuted(target.getUniqueId())) {
            warn(moderator, target.getName() + " is not muted.");
            return false;
        }

        getPunishmentManager().removePunishment(target.getUniqueId(), PunishmentData.PunishmentType.MUTE);

        String notification = PREFIX + "§9" + moderator.getName() + "§7 unmuted §9" + target.getName();
        getModerationManager().broadcastToModerators(notification);
        target.sendMessage("§aYou have been unmuted.");
        return true;
    }
}
