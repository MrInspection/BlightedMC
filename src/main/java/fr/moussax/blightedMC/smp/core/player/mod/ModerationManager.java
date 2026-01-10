package fr.moussax.blightedMC.smp.core.player.mod;

import fr.moussax.blightedMC.smp.core.player.BlightedPlayer;
import fr.moussax.blightedMC.smp.core.player.mod.punishments.PunishmentManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public final class ModerationManager {
    private static ModerationManager instance;
    private final Set<UUID> moderatorsInMode;
    private final Set<UUID> frozenPlayers;
    private final Set<UUID> vanishedPlayers;
    private final PunishmentManager punishmentManager;

    private ModerationManager() {
        this.moderatorsInMode = new HashSet<>();
        this.frozenPlayers = new HashSet<>();
        this.vanishedPlayers = new HashSet<>();
        this.punishmentManager = new PunishmentManager();
    }

    public PunishmentManager getPunishmentManager() {
        return punishmentManager;
    }

    public boolean isModerator(Player player) {
        BlightedPlayer blightedPlayer = BlightedPlayer.getBlightedPlayer(player);
        return blightedPlayer != null && blightedPlayer.isModerator();
    }

    public boolean isInModerationMode(Player player) {
        return moderatorsInMode.contains(player.getUniqueId());
    }

    public BlightedModerator getModerator(Player player) {
        BlightedPlayer blightedPlayer = BlightedPlayer.getBlightedPlayer(player);
        return blightedPlayer != null ? blightedPlayer.getModerator() : null;
    }

    public void enableModeration(Player player) {
        BlightedPlayer blightedPlayer = BlightedPlayer.getBlightedPlayer(player);
        if (blightedPlayer == null || !blightedPlayer.isModerator()) return;

        BlightedModerator moderator = blightedPlayer.getModerator();
        assert moderator != null;
        if (moderator.isModerationMode()) return;

        moderator.enable();
        moderatorsInMode.add(player.getUniqueId());
    }

    public void disableModeration(Player player) {
        BlightedPlayer blightedPlayer = BlightedPlayer.getBlightedPlayer(player);
        if (blightedPlayer == null || !blightedPlayer.isModerator()) return;

        BlightedModerator moderator = blightedPlayer.getModerator();
        assert moderator != null;
        if (!moderator.isModerationMode()) return;

        moderator.disable();
        moderatorsInMode.remove(player.getUniqueId());
        frozenPlayers.remove(player.getUniqueId());
        vanishedPlayers.remove(player.getUniqueId());
    }

    public void toggleModerationMode(Player player) {
        if (isInModerationMode(player)) {
            disableModeration(player);
            return;
        }
        enableModeration(player);
    }

    public void disableAll() {
        moderatorsInMode.clear();
        frozenPlayers.clear();
        vanishedPlayers.clear();
    }

    public boolean toggleFreeze(Player target) {
        UUID targetId = target.getUniqueId();
        if (frozenPlayers.remove(targetId)) {
            return false;
        }
        frozenPlayers.add(targetId);
        return true;
    }

    public boolean isFrozen(UUID playerId) {
        return frozenPlayers.contains(playerId);
    }

    public boolean isFrozen(Player player) {
        return isFrozen(player.getUniqueId());
    }

    public void setVanished(Player player, boolean vanished) {
        UUID playerId = player.getUniqueId();
        if (vanished) {
            vanishedPlayers.add(playerId);
        } else {
            vanishedPlayers.remove(playerId);
        }
    }

    public boolean isVanished(UUID playerId) {
        return vanishedPlayers.contains(playerId);
    }

    public boolean isVanished(Player player) {
        return isVanished(player.getUniqueId());
    }

    public int getVanishedCount() {
        return vanishedPlayers.size();
    }

    public void broadcastToModerators(String message) {
        Bukkit.getOnlinePlayers().stream()
            .filter(this::isModerator)
            .forEach(mod -> mod.sendMessage(message));
    }

    public static ModerationManager getInstance() {
        if (instance == null) {
            instance = new ModerationManager();
        }
        return instance;
    }
}
