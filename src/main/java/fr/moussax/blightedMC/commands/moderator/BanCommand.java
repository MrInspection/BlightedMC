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

import static fr.moussax.blightedMC.utils.formatting.Formatter.*;

public class BanCommand implements CommandExecutor {
    private static final String PREFIX = " §9§lMOD §f| §7";
    private final ModerationManager moderationManager = ModerationManager.getInstance();
    private final PunishmentManager punishmentManager = moderationManager.getPunishmentManager();

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args) {
        if (!(sender instanceof Player moderator)) return false;
        if (!hasRequiredPermission(moderator, PluginPermissions.MODERATOR)) return false;

        if (label.equalsIgnoreCase("unban")) {
            return handleUnban(moderator, args);
        }

        if (label.equalsIgnoreCase("banip")) {
            return handleBanIp(moderator, args);
        }

        if (label.equalsIgnoreCase("unbanip")) {
            return handleUnbanIp(moderator, args);
        }

        return handleBan(moderator, args);
    }

    private boolean handleBan(Player moderator, String[] args) {
        if (args.length < 2) {
            CommandInfo.sendUsage(moderator, "Ban a player.", "ban", "<player> [duration] <reason>");
            moderator.sendMessage("§7Duration format: 1d, 3w, 1m, 1y (omit for permanent)");
            return false;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            warn(moderator, "Unable to find the player §4" + args[0] + "§c.");
            return false;
        }

        if (target.equals(moderator)) {
            warn(moderator, "You cannot ban yourself.");
            return false;
        }

        Long expiresAt = DurationParser.parseDuration(args[1]);
        int reasonStartIndex = expiresAt != null ? 2 : 1;

        if (args.length <= reasonStartIndex) {
            warn(moderator, "You must provide a reason.");
            return false;
        }

        String reason = String.join(" ", java.util.Arrays.copyOfRange(args, reasonStartIndex, args.length));
        String ipAddress = PunishmentManager.getPlayerIp(target);

        punishmentManager.addPunishment(
            target.getUniqueId(),
            target.getName(),
            PunishmentData.PunishmentType.BAN,
            reason,
            moderator.getUniqueId(),
            moderator.getName(),
            expiresAt,
            ipAddress
        );

        String durationText = expiresAt != null ? DurationParser.formatDuration(args[1]) : "Permanent";
        String banMessage = "§cYou are temporarly banned from this server\n\n§7Reason: §f" + reason +
            "\n§7Duration: §f" + durationText +
            "\n\n§7Appeal on our Discord if you believe this was a mistake.";

        target.kickPlayer(banMessage);

        String notification = PREFIX + "§9" + moderator.getName() + "§7 banned §9" + target.getName() +
            "§7 for §c" + reason + "§7 (" + durationText + ")";
        moderationManager.broadcastToModerators(notification);

        return true;
    }

    private boolean handleUnban(Player moderator, String[] args) {
        if (args.length < 1) {
            CommandInfo.sendUsage(moderator, "Unban a player.", "unban", "<player>");
            return false;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            warn(moderator, "Unable to find the player §4" + args[0] + "§c.");
            return false;
        }

        if (!punishmentManager.isBanned(target.getUniqueId())) {
            warn(moderator, target.getName() + " is not banned.");
            return false;
        }

        punishmentManager.removePunishment(target.getUniqueId(), PunishmentData.PunishmentType.BAN);

        String notification = PREFIX + "§9" + moderator.getName() + "§7 unbanned §9" + target.getName();
        moderationManager.broadcastToModerators(notification);

        return true;
    }

    private boolean handleBanIp(Player moderator, String[] args) {
        if (args.length < 2) {
            CommandInfo.sendUsage(moderator, "IP ban a player.", "banip", "<player> [duration] <reason>");
            moderator.sendMessage("§7Duration format: 1d, 3w, 1m, 1y (omit for permanent)");
            return false;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            warn(moderator, "Unable to find the player §4" + args[0] + "§c.");
            return false;
        }

        if (target.equals(moderator)) {
            warn(moderator, "You cannot IP ban yourself.");
            return false;
        }

        Long expiresAt = DurationParser.parseDuration(args[1]);
        int reasonStartIndex = expiresAt != null ? 2 : 1;

        if (args.length <= reasonStartIndex) {
            warn(moderator, "You must provide a reason.");
            return false;
        }

        String reason = String.join(" ", java.util.Arrays.copyOfRange(args, reasonStartIndex, args.length));
        String ipAddress = PunishmentManager.getPlayerIp(target);

        punishmentManager.addPunishment(
            target.getUniqueId(),
            target.getName(),
            PunishmentData.PunishmentType.IP_BAN,
            reason,
            moderator.getUniqueId(),
            moderator.getName(),
            expiresAt,
            ipAddress
        );

        String durationText = expiresAt != null ? DurationParser.formatDuration(args[1]) : "Permanent";
        String banMessage = "§cYou ip address is temporarly banned from this server!\n\n§7Reason: §f" + reason +
            "\n§7Duration: §f" + durationText +
            "\n\n§7Appeal on our Discord if you believe this was a mistake.";

        target.kickPlayer(banMessage);

        String notification = PREFIX + "§9" + moderator.getName() + "§7 IP banned §9" + target.getName() +
            "§7 for §c" + reason + "§7 (" + durationText + ")";
        moderationManager.broadcastToModerators(notification);

        return true;
    }

    private boolean handleUnbanIp(Player moderator, String[] args) {
        if (args.length < 1) {
            CommandInfo.sendUsage(moderator, "Unban an IP address.", "unbanip", "<player>");
            return false;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            warn(moderator, "Unable to find the player §4" + args[0] + "§c.");
            return false;
        }

        String ipAddress = PunishmentManager.getPlayerIp(target);

        if (!punishmentManager.isIpBanned(ipAddress)) {
            warn(moderator, "This IP is not banned.");
            return false;
        }

        punishmentManager.removeIpPunishment(ipAddress);

        String notification = PREFIX + "§9" + moderator.getName() + "§7 unbanned IP for §9" + target.getName();
        moderationManager.broadcastToModerators(notification);

        return true;
    }
}
