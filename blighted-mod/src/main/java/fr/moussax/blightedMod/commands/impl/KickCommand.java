package fr.moussax.blightedMod.commands.impl;

import fr.moussax.bedrock.commands.CommandArgument;
import fr.moussax.blightedMod.commands.ModerationCommand;
import fr.moussax.blightedMod.moderator.punishments.PunishmentData;
import fr.moussax.blightedMod.moderator.punishments.PunishmentManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static fr.moussax.bedrock.text.Messenger.warn;

@CommandArgument(position = 0, suggestions = {"$players"})
public final class KickCommand extends ModerationCommand {

    @Override
    protected boolean isConsoleAllowed() {
        return true;
    }

    @Override
    protected boolean executeModeration(CommandSender moderator, Command command, String label, String[] arguments) {
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

        if (moderator instanceof Player && getModerationManager().isModerator(target)) {
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
                PunishmentManager.getModeratorUuid(moderator),
                moderator.getName(),
                null,
                ipAddress
        );

        String kickMessage = """
                §cYou are kicked from this server!
                
                §7Reason: §f%s
                """.formatted(reason);

        target.kickPlayer(kickMessage);

        String notification = " §d§lSTAFF! §9" + moderator.getName() + "§e kicked §d" + target.getName() + "§e for §c" + reason + "§e.";
        getModerationManager().broadcastToModerators(notification);

        return true;
    }
}
