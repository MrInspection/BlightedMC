package fr.moussax.blightedMC.smp.core.player.mod.punishments;

import java.util.UUID;

public class PunishmentData {
    private final int id;
    private final UUID playerUuid;
    private final String playerName;
    private final PunishmentType type;
    private final String reason;
    private final UUID moderatorUuid;
    private final String moderatorName;
    private final long createdAt;
    private final Long expiresAt;
    private final boolean active;
    private final String ipAddress;

    public PunishmentData(int id, UUID playerUuid, String playerName, PunishmentType type,
                          String reason, UUID moderatorUuid, String moderatorName,
                          long createdAt, Long expiresAt, boolean active, String ipAddress) {
        this.id = id;
        this.playerUuid = playerUuid;
        this.playerName = playerName;
        this.type = type;
        this.reason = reason;
        this.moderatorUuid = moderatorUuid;
        this.moderatorName = moderatorName;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.active = active;
        this.ipAddress = ipAddress;
    }

    public int getId() {
        return id;
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public String getPlayerName() {
        return playerName;
    }

    public PunishmentType getType() {
        return type;
    }

    public String getReason() {
        return reason;
    }

    public UUID getModeratorUuid() {
        return moderatorUuid;
    }

    public String getModeratorName() {
        return moderatorName;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public Long getExpiresAt() {
        return expiresAt;
    }

    public boolean isActive() {
        return active;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public boolean isPermanent() {
        return expiresAt == null;
    }

    public boolean isExpired() {
        return expiresAt != null && System.currentTimeMillis() > expiresAt;
    }

    public enum PunishmentType {
        MUTE,
        KICK,
        BAN,
        IP_BAN
    }
}
