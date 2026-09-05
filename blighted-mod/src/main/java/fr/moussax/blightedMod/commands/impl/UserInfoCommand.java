package fr.moussax.blightedMod.commands.impl;

import fr.moussax.bedrock.commands.CommandArgument;
import fr.moussax.blightedMod.commands.ModerationCommand;
import fr.moussax.blightedMod.moderator.punishments.PunishmentData;
import fr.moussax.blightedMod.moderator.punishments.PunishmentManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import static fr.moussax.bedrock.text.Messenger.warn;

@CommandArgument(position = 0, suggestions = {"$players"})
public final class UserInfoCommand extends ModerationCommand {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);

    @Override
    protected boolean executeModeration(Player moderator, Command command, String label, String[] arguments) {
        if (arguments.length == 0) {
            warn(moderator, "Usage: /userinfo <player>");
            return false;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(arguments[0]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            warn(moderator, "Unable to find player §4" + arguments[0]);
            return false;
        }

        String targetName = target.getName() != null ? target.getName() : arguments[0];
        boolean isOnline = target.isOnline();
        String statusDot = isOnline ? "§a●" : "§c●";

        Player targetPlayer = target.getPlayer();
        List<PunishmentData> punishments = getPunishmentManager().getAllPunishments(targetName);
        String ipAddress = targetPlayer != null ? PunishmentManager.getPlayerIp(targetPlayer) : resolveOfflineIp(punishments);

        long mutesCount = punishments.stream().filter(punishment -> punishment.type() == PunishmentData.PunishmentType.MUTE).count();
        long bansCount = punishments.stream().filter(punishment -> punishment.type() == PunishmentData.PunishmentType.BAN || punishment.type() == PunishmentData.PunishmentType.IP_BAN).count();
        long kicksCount = punishments.stream().filter(punishment -> punishment.type() == PunishmentData.PunishmentType.KICK).count();

        String firstConnection = formatFirstPlayed(target.getFirstPlayed());
        String totalPlaytime = formatPlaytime(target.getStatistic(Statistic.PLAY_ONE_MINUTE));

        moderator.sendMessage(" ");
        moderator.sendMessage(" ");
        moderator.sendMessage("    §e§lINFO§f | §f" + targetName + " " + statusDot);
        moderator.sendMessage(" ");
        moderator.sendMessage("§7  • §fIP Address: §e" + ipAddress);
        moderator.sendMessage("§7  • §fFirst Connection: §e" + firstConnection);
        moderator.sendMessage("§7  • §fTotal Playtime: §d" + totalPlaytime);
        moderator.sendMessage("§7  • §fSanctions: §c" + mutesCount + "§f Mutes §7| §c" + bansCount + "§f Bans §7| §c" + kicksCount + "§f Kicks");
        moderator.sendMessage(" ");

        return true;
    }

    private String resolveOfflineIp(List<PunishmentData> punishments) {
        return punishments.stream()
                .map(PunishmentData::ipAddress)
                .filter(ipAddress -> ipAddress != null && !ipAddress.isBlank() && !ipAddress.equals("0.0.0.0"))
                .findFirst()
                .orElse("Unknown");
    }

    private String formatFirstPlayed(long firstPlayedTimestamp) {
        if (firstPlayedTimestamp <= 0) {
            return "Unknown";
        }
        Instant instant = Instant.ofEpochMilli(firstPlayedTimestamp);
        return DATE_FORMATTER.format(instant.atZone(ZoneId.systemDefault()));
    }

    private String formatPlaytime(int totalTicks) {
        long totalSeconds = totalTicks / 20L;
        long hours = totalSeconds / 3600L;
        long minutes = (totalSeconds % 3600L) / 60L;
        return hours + "h and " + minutes + "m";
    }
}
