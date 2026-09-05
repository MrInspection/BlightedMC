package fr.moussax.blightedMod.commands.impl;

import fr.moussax.bedrock.commands.CommandArgument;
import fr.moussax.blightedMod.commands.ModerationCommand;
import fr.moussax.blightedMod.moderator.punishments.PunishmentData;
import fr.moussax.blightedMod.moderator.punishments.PunishmentManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import org.bukkit.attribute.Attribute;
import java.util.Objects;
import java.util.List;

import static fr.moussax.bedrock.text.Messenger.inform;
import static fr.moussax.bedrock.text.Messenger.warn;

@CommandArgument(position = 0, suggestions = {"$players"})
public final class UserInfoCommand extends ModerationCommand {

    @Override
    protected boolean executeModeration(Player moderator, Command command, String label, String[] arguments) {
        if (arguments.length == 0) {
            warn(moderator, "Usage: /userinfo <player>");
            return false;
        }

        Player target = requireTarget(moderator, arguments[0]);
        if (target == null) {
            return false;
        }

        String ipAddress = PunishmentManager.getPlayerIp(target);
        Location location = target.getLocation();
        double maxHealth = Objects.requireNonNull(target.getAttribute(Attribute.MAX_HEALTH)).getValue();
        int healthPercent = (int) Math.round((target.getHealth() / maxHealth) * 100.0);

        List<PunishmentData> punishments = getPunishmentManager().getAllPunishments(target.getName());
        long mutesCount = punishments.stream().filter(punishment -> punishment.type() == PunishmentData.PunishmentType.MUTE).count();
        long bansCount = punishments.stream().filter(punishment -> punishment.type() == PunishmentData.PunishmentType.BAN || punishment.type() == PunishmentData.PunishmentType.IP_BAN).count();
        long kicksCount = punishments.stream().filter(punishment -> punishment.type() == PunishmentData.PunishmentType.KICK).count();

        boolean isFrozen = getModerationManager().isFrozen(target);
        boolean isMuted = getPunishmentManager().isMuted(target.getUniqueId());
        boolean isVanished = getModerationManager().getModerator(target).isVanished();

        inform(moderator, " §eUser info for §d" + target.getName() + "§7:");
        moderator.sendMessage(" §f- §7UUID: §f" + target.getUniqueId());
        moderator.sendMessage(" §f- §7IP Address: §f" + ipAddress);
        moderator.sendMessage(" §f- §7State: §a" + healthPercent + "% HP §f| §e" + target.getFoodLevel() + " Food §f| §b" + target.getGameMode().name() + " §f| §dLvl " + target.getLevel());
        moderator.sendMessage(" §f- §7Location: §f" + location.getWorld().getName() + " (" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ")");
        moderator.sendMessage(" §f- §7Status: §fFrozen: " + (isFrozen ? "§aYES" : "§cNO") + " §f| Muted: " + (isMuted ? "§aYES" : "§cNO") + " §f| Vanished: " + (isVanished ? "§aYES" : "§cNO"));
        moderator.sendMessage(" §f- §7History: §f" + mutesCount + " Mutes | " + bansCount + " Bans | " + kicksCount + " Kicks");

        return true;
    }
}
