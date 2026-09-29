package fr.moussax.blightedMod.moderator.punishments;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

/**
 * Domain request payload for issuing a player punishment.
 *
 * @param playerUuid    unique identifier of the target player
 * @param playerName    name of the target player
 * @param type          type of sanction applied
 * @param reason        justification for the sanction
 * @param moderatorUuid unique identifier of the issuer
 * @param moderatorName name of the issuer
 * @param expiresAt     expiration timestamp in milliseconds, or {@code null} if permanent
 * @param ipAddress     associated IP address of the target, or {@code null}
 */
public record PunishmentRequest(
        @NonNull UUID playerUuid,
        @NonNull String playerName,
        PunishmentData.@NonNull PunishmentType type,
        @NonNull String reason,
        @NonNull UUID moderatorUuid,
        @NonNull String moderatorName,
        @Nullable Long expiresAt,
        @Nullable String ipAddress
) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private UUID playerUuid;
        private String playerName;
        private PunishmentData.PunishmentType type;
        private String reason = "No reason specified";
        private UUID moderatorUuid = PunishmentManager.CONSOLE_UUID;
        private String moderatorName = "CONSOLE";
        private Long expiresAt;
        private String ipAddress;

        public Builder target(Player player) {
            this.playerUuid = player.getUniqueId();
            this.playerName = player.getName();
            this.ipAddress = PunishmentManager.getPlayerIp(player);
            return this;
        }

        public Builder target(OfflineTargetIdentity identity) {
            this.playerUuid = identity.uniqueId();
            this.playerName = identity.name();
            this.ipAddress = identity.ipAddress();
            return this;
        }

        public Builder type(PunishmentData.PunishmentType type) {
            this.type = type;
            return this;
        }

        public Builder reason(String reason) {
            this.reason = reason != null && !reason.isBlank() ? reason : "No reason specified";
            return this;
        }

        public Builder moderator(@Nullable CommandSender moderator) {
            if (moderator != null) {
                this.moderatorUuid = PunishmentManager.getModeratorUuid(moderator);
                this.moderatorName = moderator.getName();
            } else {
                this.moderatorUuid = PunishmentManager.CONSOLE_UUID;
                this.moderatorName = "CONSOLE";
            }
            return this;
        }

        public Builder expiresAt(Long expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public Builder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public PunishmentRequest build() {
            Objects.requireNonNull(playerUuid, "Target player UUID cannot be null");
            Objects.requireNonNull(playerName, "Target player name cannot be null");
            Objects.requireNonNull(type, "Punishment type cannot be null");
            return new PunishmentRequest(playerUuid, playerName, type, reason, moderatorUuid, moderatorName, expiresAt, ipAddress);
        }
    }
}
