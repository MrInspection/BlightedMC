package fr.moussax.blightedMC.smp.core.player.mod.punishments;

import fr.moussax.blightedMC.BlightedMC;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PunishmentManager {
    private final Connection connection;

    public PunishmentManager() {
        this.connection = BlightedMC.getInstance().getDatabase().getConnection();
    }

    public void addPunishment(UUID playerUuid, String playerName, PunishmentData.PunishmentType type,
                              String reason, UUID moderatorUuid, String moderatorName,
                              Long expiresAt, String ipAddress) {
        String query = """
            INSERT INTO punishments (player_uuid, player_name, punishment_type, reason,
                                     moderator_uuid, moderator_name, created_at, expires_at, ip_address)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, playerUuid.toString());
            statement.setString(2, playerName);
            statement.setString(3, type.name());
            statement.setString(4, reason);
            statement.setString(5, moderatorUuid.toString());
            statement.setString(6, moderatorName);
            statement.setLong(7, System.currentTimeMillis());
            if (expiresAt != null) {
                statement.setLong(8, expiresAt);
            } else {
                statement.setNull(8, java.sql.Types.INTEGER);
            }
            statement.setString(9, ipAddress);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add punishment", e);
        }
    }

    public void removePunishment(UUID playerUuid, PunishmentData.PunishmentType type) {
        String query = """
            UPDATE punishments
            SET is_active = 0
            WHERE player_uuid = ? AND punishment_type = ? AND is_active = 1
            """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, playerUuid.toString());
            statement.setString(2, type.name());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to remove punishment", e);
        }
    }

    public void removeIpPunishment(String ipAddress) {
        String query = """
            UPDATE punishments
            SET is_active = 0
            WHERE ip_address = ? AND punishment_type = 'IP_BAN' AND is_active = 1
            """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, ipAddress);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to remove IP ban", e);
        }
    }

    public PunishmentData getActivePunishment(UUID playerUuid, PunishmentData.PunishmentType type) {
        String query = """
            SELECT * FROM punishments
            WHERE player_uuid = ? AND punishment_type = ? AND is_active = 1
            ORDER BY created_at DESC LIMIT 1
            """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, playerUuid.toString());
            statement.setString(2, type.name());

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    PunishmentData punishment = mapResultSet(rs);
                    if (punishment.isExpired()) {
                        removePunishment(playerUuid, type);
                        return null;
                    }
                    return punishment;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get punishment", e);
        }
        return null;
    }

    public PunishmentData getActiveIpBan(String ipAddress) {
        String query = """
            SELECT * FROM punishments
            WHERE ip_address = ? AND punishment_type = 'IP_BAN' AND is_active = 1
            ORDER BY created_at DESC LIMIT 1
            """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, ipAddress);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    PunishmentData punishment = mapResultSet(rs);
                    if (punishment.isExpired()) {
                        removeIpPunishment(ipAddress);
                        return null;
                    }
                    return punishment;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get IP ban", e);
        }
        return null;
    }

    public boolean isMuted(UUID playerUuid) {
        return getActivePunishment(playerUuid, PunishmentData.PunishmentType.MUTE) != null;
    }

    public boolean isBanned(UUID playerUuid) {
        return getActivePunishment(playerUuid, PunishmentData.PunishmentType.BAN) != null;
    }

    public boolean isIpBanned(String ipAddress) {
        return getActiveIpBan(ipAddress) != null;
    }

    private PunishmentData mapResultSet(ResultSet rs) throws SQLException {
        Long expiresAt = rs.getLong("expires_at");
        if (rs.wasNull()) {
            expiresAt = null;
        }

        return new PunishmentData(
            rs.getInt("id"),
            UUID.fromString(rs.getString("player_uuid")),
            rs.getString("player_name"),
            PunishmentData.PunishmentType.valueOf(rs.getString("punishment_type")),
            rs.getString("reason"),
            UUID.fromString(rs.getString("moderator_uuid")),
            rs.getString("moderator_name"),
            rs.getLong("created_at"),
            expiresAt,
            rs.getInt("is_active") == 1,
            rs.getString("ip_address")
        );
    }

    public static String getPlayerIp(Player player) {
        String address = player.getAddress().getAddress().getHostAddress();
        return address != null ? address : "0.0.0.0";
    }
}
