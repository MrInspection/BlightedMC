package fr.moussax.blightedMC.commands.moderator;

import fr.moussax.blightedMC.server.PluginPermissions;
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

import static fr.moussax.blightedMC.utils.formatting.Formatter.hasRequiredPermission;
import static fr.moussax.blightedMC.utils.formatting.Formatter.warn;

public class KickCommand implements CommandExecutor {
    private static final String PREFIX = " §9§lMOD §f| §7";
    private final ModerationManager moderationManager = ModerationManager.getInstance();
    private final PunishmentManager punishmentManager = moderationManager.getPunishmentManager();

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args) {
        if (!(sender instanceof Player moderator)) return false;
        if (!hasRequiredPermission(moderator, PluginPermissions.MODERATOR)) return false;

        if (!hasRequiredPermission(moderator, PluginPermissions.MODERATOR)) return false;

        if (args.length < 2) {
            CommandInfo.sendUsage(moderator, "Kick a player.", "kick", "<player> <reason>");
            return false;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            warn(moderator, "Unable to find the player §4" + args[0] + "§c.");
            return false;
        }

        if (target.equals(moderator)) {
            warn(moderator, "You cannot kick yourself.");
            return false;
        }

        String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        String ipAddress = PunishmentManager.getPlayerIp(target);

        punishmentManager.addPunishment(
            target.getUniqueId(),
            target.getName(),
            PunishmentData.PunishmentType.KICK,
            reason,
            moderator.getUniqueId(),
            moderator.getName(),
            null,
            ipAddress
        );

        String kickMessage = "§cYou are kicked from this server!\n\n§7Reason: §f" + reason +
            "\n\n§7If you believe this was a mistake, please appeal on our Discord.";

        target.kickPlayer(kickMessage);

        String notification = PREFIX + "§9" + moderator.getName() + "§7 kicked §9" + target.getName() +
            "§7 for §c" + reason;
        moderationManager.broadcastToModerators(notification);

        return true;
    }
}
