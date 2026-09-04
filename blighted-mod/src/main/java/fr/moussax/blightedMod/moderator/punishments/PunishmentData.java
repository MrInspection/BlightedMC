package fr.moussax.blightedMod.moderator.punishments;

import java.util.UUID;

/**
 * Record data carrier for player punishment records.
 */
public record PunishmentData(
        int id,
        UUID playerUuid,
        String playerName,
        PunishmentType type,
        String reason,
        UUID moderatorUuid,
        String moderatorName,
        long createdAt,
        Long expiresAt,
        boolean active,
        String ipAddress
) {
    public boolean isPermanent() {
        return expiresAt == null;
    }

    public boolean isExpired() {
        return expiresAt != null && System.currentTimeMillis() > expiresAt;
    }

    public enum PunishmentType {MUTE, KICK, BAN, IP_BAN}
}
