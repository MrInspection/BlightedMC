package fr.moussax.blightedMC.commands.moderator;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.server.PluginPermissions;
import fr.moussax.blightedMC.smp.core.player.mod.BlightedModerator;
import fr.moussax.blightedMC.smp.core.player.mod.ModerationManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

import static fr.moussax.blightedMC.utils.formatting.Formatter.hasRequiredPermission;

public class VanishCommand implements CommandExecutor {
    private static final String PREFIX = " §d§lMOD §f| §e";

    private final ModerationManager moderationManager = ModerationManager.getInstance();

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command cmd, @NonNull String label, String @NonNull [] args) {
        if (!(sender instanceof Player player)) return false;
        if (!hasRequiredPermission(player, PluginPermissions.MODERATOR))

            if (moderationManager.isInModerationMode(player)) {
                toggleVanishInModerationMode(player);
                return true;
            }

        toggleVanishStandalone(player);
        return true;
    }

    private void toggleVanishInModerationMode(Player player) {
        BlightedModerator moderator = Objects.requireNonNull(moderationManager.getModerator(player));
        boolean newVanishState = !moderator.isVanished();
        moderator.setVanished(newVanishState);
    }

    private void toggleVanishStandalone(Player player) {
        boolean currentlyVanished = moderationManager.isVanished(player);
        boolean newVanishState = !currentlyVanished;

        moderationManager.setVanished(player, newVanishState);
        BlightedMC pluginInstance = BlightedMC.getInstance();

        if (newVanishState) {
            hideFromNonModerators(player, pluginInstance);
            player.sendMessage(PREFIX + "You are now §avanished§7.");
        } else {
            showToAllPlayers(player, pluginInstance);
            player.sendMessage(PREFIX + "You are now §cvisible§7.");
        }
    }

    private void hideFromNonModerators(Player player, BlightedMC pluginInstance) {
        Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
            if (!hasRequiredPermission(onlinePlayer, PluginPermissions.MODERATOR)) {
                onlinePlayer.hidePlayer(pluginInstance, player);
            }
        });
    }

    private void showToAllPlayers(Player player, BlightedMC pluginInstance) {
        Bukkit.getOnlinePlayers().forEach(onlinePlayer ->
            onlinePlayer.showPlayer(pluginInstance, player)
        );
    }
}
