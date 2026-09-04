package fr.moussax.blightedMod.moderator;

import fr.moussax.bedrock.text.InteractiveMessage;
import fr.moussax.blightedMod.moderator.punishments.PunishmentManager;
import fr.moussax.blightedMod.utils.PluginPermissions;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

import java.util.concurrent.ConcurrentHashMap;

public final class ModerationManager {
    private static ModerationManager instance;

    public enum ChatChannel {
        ALL,
        STAFF
    }

    private final PunishmentManager punishmentManager;
    private final Map<UUID, BlightedModerator> moderators = new HashMap<>();
    private final Set<UUID> frozenPlayers = new HashSet<>();
    private final Map<UUID, Long> lastChatTimestamps = new ConcurrentHashMap<>();
    private final Map<UUID, ChatChannel> playerChatChannels = new ConcurrentHashMap<>();
    private final Set<UUID> messageInspectEnabled = ConcurrentHashMap.newKeySet();
    private final Map<UUID, UUID> lastMessageTargets = new ConcurrentHashMap<>();
    private int slowmodeDelaySeconds = 0;

    public PunishmentManager getPunishmentManager() {
        return punishmentManager;
    }

    private ModerationManager(PunishmentManager punishmentManager) {
        this.punishmentManager = punishmentManager;
    }

    public static ModerationManager init(PunishmentManager punishmentManager) {
        if (instance != null) {
            throw new IllegalStateException("ModerationManager already initialized");
        }
        instance = new ModerationManager(punishmentManager);
        return instance;
    }

    public static ModerationManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ModerationManager not initialized yet");
        }
        return instance;
    }

    public BlightedModerator getModerator(Player player) {
        return moderators.computeIfAbsent(player.getUniqueId(), _ -> new BlightedModerator(player));
    }

    public boolean isModerator(Player player) {
        return player.hasPermission(PluginPermissions.MODERATOR.toString());
    }

    public boolean isInModerationMode(Player player) {
        BlightedModerator moderator = moderators.get(player.getUniqueId());
        return moderator != null && moderator.isInModerationMode();
    }

    public void toggleModerationMode(Player player) {
        BlightedModerator moderator = getModerator(player);
        if (moderator.isInModerationMode()) {
            moderator.onDisable();
        } else {
            moderator.onEnable();
        }
    }

    public void disableModeration(Player player) {
        BlightedModerator moderator = moderators.get(player.getUniqueId());
        if (moderator != null && moderator.isInModerationMode()) moderator.onDisable();
    }

    public boolean isFrozen(Player player) {
        return frozenPlayers.contains(player.getUniqueId());
    }

    public boolean toggleFreeze(Player player) {
        UUID playerId = player.getUniqueId();
        if (frozenPlayers.contains(playerId)) {
            frozenPlayers.remove(playerId);
            return false;
        }
        frozenPlayers.add(playerId);
        return true;
    }

    public void setSlowmodeDelaySeconds(int seconds) {
        this.slowmodeDelaySeconds = Math.max(0, seconds);
    }

    public int getSlowmodeDelaySeconds() {
        return slowmodeDelaySeconds;
    }

    public boolean isSlowmodeEnabled() {
        return slowmodeDelaySeconds > 0;
    }

    public int getRemainingSlowmodeCooldown(Player player) {
        if (slowmodeDelaySeconds <= 0 || isModerator(player)) {
            return 0;
        }

        Long lastChatTime = lastChatTimestamps.get(player.getUniqueId());
        if (lastChatTime == null) {
            return 0;
        }

        long elapsedSeconds = (System.currentTimeMillis() - lastChatTime) / 1000L;
        long remainingSeconds = slowmodeDelaySeconds - elapsedSeconds;
        return (int) Math.max(0, remainingSeconds);
    }

    public void recordPlayerChat(Player player) {
        lastChatTimestamps.put(player.getUniqueId(), System.currentTimeMillis());
    }

    public void broadcastToModerators(String message) {
        Bukkit.getOnlinePlayers().stream()
                .filter(this::isModerator)
                .forEach(moderator -> moderator.sendMessage(message));
    }

    public void broadcastToModerators(InteractiveMessage interactiveMessage) {
        Bukkit.getOnlinePlayers().stream()
                .filter(this::isModerator)
                .forEach(interactiveMessage::send);
    }

    public ChatChannel getChatChannel(Player player) {
        return playerChatChannels.getOrDefault(player.getUniqueId(), ChatChannel.ALL);
    }

    public void setChatChannel(Player player, ChatChannel channel) {
        if (channel == ChatChannel.ALL) {
            playerChatChannels.remove(player.getUniqueId());
        } else {
            playerChatChannels.put(player.getUniqueId(), channel);
        }
    }

    public void handlePlayerQuit(Player player) {
        UUID playerId = player.getUniqueId();
        BlightedModerator moderator = moderators.get(playerId);
        if (moderator != null && moderator.isInModerationMode()) {
            moderator.onDisable();
        }
        moderators.remove(playerId);
        frozenPlayers.remove(playerId);
        lastChatTimestamps.remove(playerId);
        playerChatChannels.remove(playerId);
        messageInspectEnabled.remove(playerId);
        lastMessageTargets.remove(playerId);
    }

    public boolean isMessageInspectEnabled(Player player) {
        return messageInspectEnabled.contains(player.getUniqueId());
    }

    public boolean toggleMessageInspect(Player player) {
        UUID playerId = player.getUniqueId();
        if (messageInspectEnabled.contains(playerId)) {
            messageInspectEnabled.remove(playerId);
            return false;
        }
        messageInspectEnabled.add(playerId);
        return true;
    }

    public void setLastMessageTarget(UUID senderId, UUID recipientId) {
        lastMessageTargets.put(senderId, recipientId);
        lastMessageTargets.put(recipientId, senderId);
    }

    public UUID getLastMessageTarget(UUID playerId) {
        return lastMessageTargets.get(playerId);
    }

    public void broadcastSpyMessage(String senderName, String recipientName, String content) {
        String formatted = " §9[SPY] §7" + senderName + " §8» §7" + recipientName + "§f: " + content;
        Bukkit.getOnlinePlayers().stream()
                .filter(this::isModerator)
                .filter(this::isMessageInspectEnabled)
                .forEach(moderator -> moderator.sendMessage(formatted));
    }

    public Map<UUID, BlightedModerator> getModeratorsView() {
        return Collections.unmodifiableMap(moderators);
    }
}
