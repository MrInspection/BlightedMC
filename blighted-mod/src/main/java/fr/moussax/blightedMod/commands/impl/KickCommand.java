package fr.moussax.blightedMod.commands.impl;

import fr.moussax.bedrock.commands.CommandArgument;
import fr.moussax.blightedMod.commands.ModerationCommand;
import fr.moussax.blightedMod.moderator.punishments.PunishmentData;
import fr.moussax.blightedMod.moderator.punishments.PunishmentManager;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static fr.moussax.bedrock.text.Messenger.warn;

@CommandArgument(position = 0, suggestions = {"$players"})
public final class KickCommand extends ModerationCommand {
    private static final String PREFIX = " §9§lMOD §f| §7";

    @Override
    protected boolean executeModeration(Player moderator, Command command, String label, String[] arguments) {
        if (arguments.length < 1) {
            warn(moderator, "Usage: /kick <player> [reason]");
            return false;
        }

        Player target = requireTarget(moderator, arguments[0]);
        if (target == null) {
            return false;
        }

        if (target.equals(moderator)) {
            warn(moderator, "You cannot kick yourself.");
            return false;
        }

        if (getModerationManager().isModerator(target)) {
            warn(moderator, "You cannot kick another moderator.");
            return false;
        }

        String reason = arguments.length > 1
                ? String.join(" ", Arrays.copyOfRange(arguments, 1, arguments.length))
                : "No reason specified";
        String ipAddress = PunishmentManager.getPlayerIp(target);

        getPunishmentManager().addPunishment(
                target.getUniqueId(),
                target.getName(),
                PunishmentData.PunishmentType.KICK,
                reason,
                moderator.getUniqueId(),
                moderator.getName(),
                null,
                ipAddress
        );

        String kickMessage = """
                §cYou are kicked from this server!

                §7Reason: §f%s

                §7If you believe this was a mistake, please appeal on our Discord.""".formatted(reason);

        target.kickPlayer(kickMessage);

        String notification = PREFIX + "§9" + moderator.getName() + "§7 kicked §9" + target.getName() + "§7 for §c" + reason;
        getModerationManager().broadcastToModerators(notification);

        return true;
    }
}
