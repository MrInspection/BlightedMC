package fr.moussax.blightedMC.commands.moderator;

import fr.moussax.blightedMC.server.PluginPermissions;
import fr.moussax.blightedMC.smp.core.player.BlightedPlayer;
import fr.moussax.blightedMC.smp.core.player.mod.ModerationManager;
import fr.moussax.blightedMC.utils.commands.CommandInfo;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

import static fr.moussax.blightedMC.utils.formatting.Formatter.*;

public class ModTPCommands implements CommandExecutor {
    private static final String PREFIX = " §9§lMOD §f| §7";
    private final ModerationManager moderationManager = ModerationManager.getInstance();

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args) {
        switch (sender) {
            case Player player when label.equalsIgnoreCase("mtp") -> {
                if (!hasRequiredPermission(player, PluginPermissions.MODERATOR)) return false;

                if (args.length == 0) {
                    CommandInfo.sendUsage(player, "Teleport to a player.", "mtp", "<player>");
                    return false;
                }

                Player target = Bukkit.getPlayerExact(args[0]);
                if (target == null) {
                    warn(player, "Unable to find the player §4" + args[0] + "§c.");
                    return false;
                }

                if (target.equals(player)) {
                    warn(player, "You cannot teleport to yourself.");
                    return false;
                }

                player.teleport(target.getLocation());
                player.sendMessage(PREFIX + "Teleported to §9" + target.getName() + "§7.");

                String notification = PREFIX + "§9" + player.getName() + "§7 teleported to §9" + target.getName();
                moderationManager.broadcastToModerators(notification);

                BlightedPlayer bp = BlightedPlayer.getBlightedPlayer(player);
                if (bp != null) {
                    bp.getActionBarManager().setModTarget(target);
                }

                return true;
            }
            case Player player when label.equalsIgnoreCase("mtphere") -> {
                if (!hasRequiredPermission(player, PluginPermissions.MODERATOR)) return false;

                if (args.length == 0) {
                    CommandInfo.sendUsage(player, "Teleport a player to you.", "mtphere", "<player>");
                    return false;
                }

                Player target = Bukkit.getPlayerExact(args[0]);
                if (target == null) {
                    warn(player, "Unable to find the player §4" + args[0] + "§c.");
                    return false;
                }

                if (target.equals(player)) {
                    warn(player, "You cannot teleport yourself to yourself.");
                    return false;
                }

                target.teleport(player.getLocation());
                player.sendMessage(PREFIX + "Teleported §9" + target.getName() + "§7 to you.");

                String notification = PREFIX + "§9" + player.getName() + "§7 teleported §9" + target.getName() + "§7 to them";
                moderationManager.broadcastToModerators(notification);

                BlightedPlayer bp = BlightedPlayer.getBlightedPlayer(player);
                if (bp != null) {
                    bp.getActionBarManager().setModTarget(target);
                }

                return true;
            }
            default -> {
                warn(sender, "Only players can execute teleport commands.");
                return false;
            }
        }
    }
}
