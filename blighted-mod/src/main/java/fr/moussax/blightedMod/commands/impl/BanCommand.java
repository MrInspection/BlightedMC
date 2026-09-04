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

/**
 * Moderation command to ban (/ban, /banip) and unban (/unban, /unbanip) players.
 */
@CommandArgument(position = 0, suggestions = {"$players"})
public final class BanCommand extends ModerationCommand {

    @Override
    protected boolean executeModeration(Player moderator, Command command, String label, String[] arguments) {
        if (label.equalsIgnoreCase("unban")) {
            return handleUnban(moderator, arguments);
        }

        if (label.equalsIgnoreCase("banip")) {
            return handleBanIp(moderator, arguments);
        }

        if (label.equalsIgnoreCase("unbanip")) {
            return handleUnbanIp(moderator, arguments);
        }

        return handleBan(moderator, arguments);
    }

    private boolean handleBan(Player moderator, String[] arguments) {
        if (arguments.length < 1) {
            warn(moderator, "Usage: /ban <player> [duration] [reason]");
            moderator.sendMessage("§7Duration format: 1d, 3w, 1m, 1y (omit for permanent)");
            return false;
        }

        Player target = requireTarget(moderator, arguments[0]);
        if (target == null) {
            return false;
        }

        if (target.equals(moderator)) {
            warn(moderator, "You cannot ban yourself.");
            return false;
        }

        if (getModerationManager().isModerator(target)) {
            warn(moderator, "You cannot ban another moderator.");
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
                PunishmentData.PunishmentType.BAN,
                reason,
                moderator.getUniqueId(),
                moderator.getName(),
                expiresAt,
                ipAddress
        );

        String durationText = expiresAt != null ? DurationParser.formatDuration(arguments[1]) : "Permanent";
        String banMessage = """
                §cYou are banned from this server!

                §7Reason: §f%s
                §7Duration: §f%s

                §7Appeal on our Discord if you believe this was a mistake.""".formatted(reason, durationText);

        target.kickPlayer(banMessage);

        String durationString = expiresAt != null ? " for §6" + durationText + "§e" : " permanently";
        String notification = " §d§lSTAFF! §9" + moderator.getName() + "§e banned §9" + target.getName() + "§e" + durationString + " for §c" + reason + "§e.";
        getModerationManager().broadcastToModerators(notification);

        return true;
    }

    private boolean handleUnban(Player moderator, String[] arguments) {
        if (arguments.length < 1) {
            warn(moderator, "Usage: /unban <player>");
            return false;
        }

        Player target = requireTarget(moderator, arguments[0]);
        if (target == null) {
            return false;
        }

        if (!getPunishmentManager().isBanned(target.getUniqueId())) {
            warn(moderator, target.getName() + " is not banned.");
            return false;
        }

        getPunishmentManager().removePunishment(target.getUniqueId(), PunishmentData.PunishmentType.BAN);

        String notification = " §d§lSTAFF! §9" + moderator.getName() + "§e unbanned §9" + target.getName() + "§e.";
        getModerationManager().broadcastToModerators(notification);

        return true;
    }

    private boolean handleBanIp(Player moderator, String[] arguments) {
        if (arguments.length < 1) {
            warn(moderator, "Usage: /banip <player> [duration] [reason]");
            moderator.sendMessage("§7Duration format: 1d, 3w, 1m, 1y (omit for permanent)");
            return false;
        }

        Player target = requireTarget(moderator, arguments[0]);
        if (target == null) {
            return false;
        }

        if (target.equals(moderator)) {
            warn(moderator, "You cannot IP ban yourself.");
            return false;
        }

        if (getModerationManager().isModerator(target)) {
            warn(moderator, "You cannot IP ban another moderator.");
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
                PunishmentData.PunishmentType.IP_BAN,
                reason,
                moderator.getUniqueId(),
                moderator.getName(),
                expiresAt,
                ipAddress
        );

        String durationText = expiresAt != null ? DurationParser.formatDuration(arguments[1]) : "Permanent";
        String banMessage = """
                §cYour IP address is banned from this server!

                §7Reason: §f%s
                §7Duration: §f%s

                §7Appeal on our Discord if you believe this was a mistake.""".formatted(reason, durationText);

        target.kickPlayer(banMessage);

        String notification = " §d§lSTAFF! §9" + moderator.getName() + "§e IP banned §9" + target.getName() + "§e for §c" + reason + "§e.";
        getModerationManager().broadcastToModerators(notification);

        return true;
    }

    private boolean handleUnbanIp(Player moderator, String[] arguments) {
        if (arguments.length < 1) {
            warn(moderator, "Usage: /unbanip <player>");
            return false;
        }

        Player target = requireTarget(moderator, arguments[0]);
        if (target == null) {
            return false;
        }

        String ipAddress = PunishmentManager.getPlayerIp(target);

        if (!getPunishmentManager().isIpBanned(ipAddress)) {
            warn(moderator, "This IP is not banned.");
            return false;
        }

        getPunishmentManager().removeIpPunishment(ipAddress);

        String notification = " §d§lSTAFF! §9" + moderator.getName() + "§e unbanned IP for §d" + target.getName() + "§e.";
        getModerationManager().broadcastToModerators(notification);

        return true;
    }
}
