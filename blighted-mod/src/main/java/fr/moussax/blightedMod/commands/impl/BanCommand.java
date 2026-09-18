package fr.moussax.blightedMod.commands.impl;

import fr.moussax.bedrock.commands.CommandArgument;
import fr.moussax.blightedMod.commands.ModerationCommand;
import fr.moussax.blightedMod.moderator.punishments.DurationParser;
import fr.moussax.blightedMod.moderator.punishments.PunishmentArguments;
import fr.moussax.blightedMod.moderator.punishments.PunishmentData;
import fr.moussax.blightedMod.moderator.punishments.OfflineTargetIdentity;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.UUID;

import static fr.moussax.bedrock.text.Messenger.warn;

@CommandArgument(position = 0, suggestions = {"$players"})
public final class BanCommand extends ModerationCommand {

    @Override
    protected boolean isConsoleAllowed() {
        return true;
    }

    @Override
    protected boolean executeModeration(CommandSender moderator, Command command, String label, String[] arguments) {
        return switch (label.toLowerCase(Locale.ROOT)) {
            case "unban" -> handleUnban(moderator, arguments);
            case "banip" -> handleBanIp(moderator, arguments);
            case "unbanip" -> handleUnbanIp(moderator, arguments);
            default -> handleBan(moderator, arguments);
        };
    }

    private boolean handleBan(CommandSender moderator, String[] arguments) {
        return executeBanOrIpBan(moderator, arguments, false);
    }

    private boolean handleBanIp(CommandSender moderator, String[] arguments) {
        return executeBanOrIpBan(moderator, arguments, true);
    }

    private boolean executeBanOrIpBan(CommandSender moderator, String[] arguments, boolean isIpBan) {
        String actionLabel = isIpBan ? "banip" : "ban";
        if (arguments.length < 1) {
            warn(moderator, "Usage: /" + actionLabel + " <player> [duration] [reason]");
            moderator.sendMessage("§7Duration format: 1d, 3w, 1m, 1y (omit for permanent)");
            return false;
        }

        String targetName = arguments[0];
        OfflineTargetIdentity targetIdentity = getPunishmentManager().resolvePlayerIdentity(targetName);
        if (targetIdentity == null) {
            warn(moderator, "Unable to find player §4" + targetName);
            return false;
        }

        if (moderator instanceof Player executingPlayer && targetIdentity.uniqueId().equals(executingPlayer.getUniqueId())) {
            warn(moderator, "You cannot " + (isIpBan ? "IP ban" : "ban") + " yourself.");
            return false;
        }

        Player onlineTarget = Bukkit.getPlayerExact(targetName);
        if (moderator instanceof Player && onlineTarget != null && getModerationManager().isModerator(onlineTarget)) {
            warn(moderator, "You cannot " + (isIpBan ? "IP ban" : "ban") + " another moderator.");
            return false;
        }

        PunishmentArguments punishmentArguments = PunishmentArguments.parse(arguments, 1);
        Long expiresAt = punishmentArguments.expiresAt();
        String reason = punishmentArguments.reason();

        if (isIpBan) {
            getPunishmentManager().addIpBan(targetIdentity, moderator, reason, expiresAt);
        } else {
            getPunishmentManager().addBan(targetIdentity, moderator, reason, expiresAt);
        }

        if (onlineTarget != null && onlineTarget.isOnline()) {
            String durationText = expiresAt != null ? DurationParser.formatDuration(arguments[1]) : "Permanent";
            String header = isIpBan ? "§cYour IP address is banned from this server!" : "§cYou are banned from this server!";
            String banMessage = """
                    %s

                    §7Reason: §f%s
                    §7Duration: §f%s

                    §7Appeal on our Discord if you believe this was a mistake.""".formatted(header, reason, durationText);
            onlineTarget.kickPlayer(banMessage);
        }

        getModerationManager().handleSanctionNotification(moderator, targetIdentity.name(), isIpBan ? "IP banned" : "banned");
        return true;
    }

    private boolean handleUnban(CommandSender moderator, String[] arguments) {
        if (arguments.length < 1) {
            warn(moderator, "Usage: /unban <player>");
            return false;
        }

        String targetName = arguments[0];
        UUID targetId = getPunishmentManager().resolveBannedUuidByName(targetName);

        if (targetId == null) {
            warn(moderator, targetName + " is not banned.");
            return false;
        }

        getPunishmentManager().removePunishment(targetId, PunishmentData.PunishmentType.BAN);
        getModerationManager().handleSanctionNotification(moderator, targetName, "unbanned");

        return true;
    }

    private boolean handleUnbanIp(CommandSender moderator, String[] arguments) {
        if (arguments.length < 1) {
            warn(moderator, "Usage: /unbanip <player>");
            return false;
        }

        String input = arguments[0];
        String ipAddress = null;
        String targetName = input;

        if (getPunishmentManager().isIpBanned(input)) {
            ipAddress = input;
        } else {
            for (PunishmentData punishment : getPunishmentManager().getAllPunishments(targetName)) {
                if (punishment.ipAddress() != null && !punishment.ipAddress().isEmpty() && !"0.0.0.0".equals(punishment.ipAddress())) {
                    ipAddress = punishment.ipAddress();
                    break;
                }
            }
        }

        if (ipAddress == null || !getPunishmentManager().isIpBanned(ipAddress)) {
            warn(moderator, "This IP is not banned.");
            return false;
        }

        getPunishmentManager().removeIpPunishment(ipAddress);
        getModerationManager().handleSanctionNotification(moderator, targetName, "unbanned IP");

        return true;
    }
}
