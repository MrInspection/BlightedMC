package fr.moussax.blightedMod.commands.impl;

import fr.moussax.bedrock.commands.CommandArgument;
import fr.moussax.blightedMod.commands.ModerationCommand;
import fr.moussax.blightedMod.moderator.punishments.DurationParser;
import fr.moussax.blightedMod.moderator.punishments.OfflineTargetIdentity;
import fr.moussax.blightedMod.moderator.punishments.PunishmentArguments;
import fr.moussax.blightedMod.moderator.punishments.PunishmentData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static fr.moussax.bedrock.text.Messenger.warn;

@CommandArgument(position = 0, suggestions = {"$players"})
public final class MuteCommand extends ModerationCommand {

    @Override
    protected boolean isConsoleAllowed() {
        return true;
    }

    @Override
    protected boolean executeModeration(CommandSender moderator, Command command, String label, String[] arguments) {
        if (label.equalsIgnoreCase("unmute")) {
            return handleUnmute(moderator, arguments);
        }

        return handleMute(moderator, arguments);
    }

    private boolean handleMute(CommandSender moderator, String[] arguments) {
        if (arguments.length < 1) {
            warn(moderator, "Usage: /mute <player> [duration] [reason]");
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
            warn(moderator, "You cannot mute yourself.");
            return false;
        }

        Player onlineTarget = Bukkit.getPlayerExact(targetName);
        if (moderator instanceof Player && onlineTarget != null && getModerationManager().isModerator(onlineTarget)) {
            warn(moderator, "You cannot mute another moderator.");
            return false;
        }

        PunishmentArguments punishmentArguments = PunishmentArguments.parse(arguments, 1);
        Long expiresAt = punishmentArguments.expiresAt();
        String reason = punishmentArguments.reason();

        getPunishmentManager().addMute(targetIdentity, moderator, reason, expiresAt);

        getModerationManager().handleSanctionNotification(moderator, targetIdentity.name(), "muted");
        if (onlineTarget != null && onlineTarget.isOnline()) {
            if (expiresAt != null) {
                onlineTarget.sendMessage(" §c⌚ §cYou are muted for §d" + arguments[1] + " §cfor §b" + reason + "§c.");
            } else {
                onlineTarget.sendMessage(" §c⌚ §cYou are muted §cfor §b" + reason + "§c.");
            }
        }

        return true;
    }

    private boolean handleUnmute(CommandSender moderator, String[] arguments) {
        if (arguments.length < 1) {
            warn(moderator, "Usage: /unmute <player>");
            return false;
        }

        String targetName = arguments[0];
        OfflineTargetIdentity targetIdentity = getPunishmentManager().resolvePlayerIdentity(targetName);
        if (targetIdentity == null) {
            warn(moderator, "Unable to find player §4" + targetName);
            return false;
        }

        if (!getPunishmentManager().isMuted(targetIdentity.uniqueId())) {
            warn(moderator, targetIdentity.name() + " is not muted.");
            return false;
        }

        getPunishmentManager().removePunishment(targetIdentity.uniqueId(), PunishmentData.PunishmentType.MUTE);

        String notification = " §d§lSTAFF! §9" + moderator.getName() + "§e unmuted §d" + targetIdentity.name() + "§e.";
        getModerationManager().broadcastToModerators(notification);

        Player onlineTarget = Bukkit.getPlayerExact(targetName);
        if (onlineTarget != null && onlineTarget.isOnline()) {
            onlineTarget.sendMessage(" §a⚑ §7You are no longer muted.");
        }
        return true;
    }
}
