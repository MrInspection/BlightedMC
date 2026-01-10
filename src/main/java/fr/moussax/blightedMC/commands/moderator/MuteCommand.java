package fr.moussax.blightedMC.commands.moderator;

import fr.moussax.blightedMC.server.PluginPermissions;
import fr.moussax.blightedMC.smp.core.player.mod.punishments.DurationParser;
import fr.moussax.blightedMC.smp.core.player.mod.ModerationManager;
import fr.moussax.blightedMC.smp.core.player.mod.punishments.PunishmentData;
import fr.moussax.blightedMC.smp.core.player.mod.punishments.PunishmentManager;
import fr.moussax.blightedMC.utils.commands.CommandInfo;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

import java.util.Arrays;

import static fr.moussax.blightedMC.utils.formatting.Formatter.*;

public class MuteCommand implements CommandExecutor {
    private static final String PREFIX = " §9§lMOD §f| §7";
    private final ModerationManager moderationManager = ModerationManager.getInstance();
    private final PunishmentManager punishmentManager = moderationManager.getPunishmentManager();

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args) {
        if (!(sender instanceof Player moderator)) {
            warn(sender, "Only players can execute this command.");
            return false;
        }

        if (!hasRequiredPermission(moderator, PluginPermissions.MODERATOR)) return false;

        if (label.equalsIgnoreCase("unmute")) {
            return handleUnmute(moderator, args);
        }

        return handleMute(moderator, args);
    }

    private boolean handleMute(Player moderator, String[] args) {
        if (args.length < 2) {
            CommandInfo.sendUsage(moderator, "Mute a player.", "mute", "<player> [duration] <reason>");
            moderator.sendMessage("§7Duration format: 1d, 3w, 1m, 1y (omit for permanent)");
            return false;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            warn(moderator, "Unable to find the player §4" + args[0] + "§c.");
            return false;
        }

        if (target.equals(moderator)) {
            warn(moderator, "You cannot mute yourself.");
            return false;
        }

        Long expiresAt = DurationParser.parseDuration(args[1]);
        int reasonStartIndex = expiresAt != null ? 2 : 1;

        if (args.length <= reasonStartIndex) {
            warn(moderator, "You must provide a reason.");
            return false;
        }

        String reason = String.join(" ", Arrays.copyOfRange(args, reasonStartIndex, args.length));
        String ipAddress = PunishmentManager.getPlayerIp(target);

        punishmentManager.addPunishment(
            target.getUniqueId(),
            target.getName(),
            PunishmentData.PunishmentType.MUTE,
            reason,
            moderator.getUniqueId(),
            moderator.getName(),
            expiresAt,
            ipAddress
        );

        String durationText = expiresAt != null ? " (" + DurationParser.formatDuration(args[1]) + ")" : " (Permanent)";
        String notification = PREFIX + "§9" + moderator.getName() + "§e muted §d" + target.getName() +
            "§e for §f" + reason + "§6" + durationText;

        moderationManager.broadcastToModerators(notification);
        target.sendMessage(" §f§lSANCTION §f| §7You have been muted for: §f" + reason + durationText);

        return true;
    }

    private boolean handleUnmute(Player moderator, String[] args) {
        if (args.length < 1) {
            CommandInfo.sendUsage(moderator, "Unmute a player.", "unmute", "<player>");
            return false;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            warn(moderator, "Unable to find the player §4" + args[0] + "§c.");
            return false;
        }

        if (!punishmentManager.isMuted(target.getUniqueId())) {
            warn(moderator, target.getName() + " is not muted.");
            return false;
        }

        punishmentManager.removePunishment(target.getUniqueId(), PunishmentData.PunishmentType.MUTE);

        String notification = PREFIX + "§9" + moderator.getName() + "§7 unmuted §9" + target.getName();
        moderationManager.broadcastToModerators(notification);
        target.sendMessage("§aYou have been unmuted.");
        return true;
    }
}
