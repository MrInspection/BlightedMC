package fr.moussax.blightedMod.moderator.punishments;

import org.bukkit.entity.Player;

import java.net.InetSocketAddress;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class PunishmentManager {
    private final Connection connection;

    public PunishmentManager(Connection connection) {
        this.connection = connection;
    }

    public void addPunishment(UUID playerUuid, String playerName, PunishmentData.PunishmentType type,
                              String reason, UUID moderatorUuid, String moderatorName,
                              Long expiresAt, String ipAddress) {
        String query = """
                INSERT INTO punishments (player_uuid, player_name, punishment_type, reason,
                                         moderator_uuid, moderator_name, created_at, expires_at, ip_address)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        synchronized (connection) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, playerUuid.toString());
                statement.setString(2, playerName);
                statement.setString(3, type.name());
                statement.setString(4, reason);
                statement.setString(5, moderatorUuid.toString());
                statement.setString(6, moderatorName);
                statement.setLong(7, System.currentTimeMillis());
                if (expiresAt != null) statement.setLong(8, expiresAt);
                else statement.setNull(8, Types.INTEGER);
                statement.setString(9, ipAddress);
                statement.executeUpdate();
            } catch (SQLException exception) {
                throw new RuntimeException("Failed to add punishment", exception);
            }
        }
    }

    public void removePunishment(UUID playerUuid, PunishmentData.PunishmentType type) {
        String query = """
                UPDATE punishments SET is_active = 0
                WHERE player_uuid = ? AND punishment_type = ? AND is_active = 1
                """;

        synchronized (connection) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, playerUuid.toString());
                statement.setString(2, type.name());
                statement.executeUpdate();
            } catch (SQLException exception) {
                throw new RuntimeException("Failed to remove punishment", exception);
            }
        }
    }

    public void removeIpPunishment(String ipAddress) {
        String query = """
                UPDATE punishments SET is_active = 0
                WHERE ip_address = ? AND punishment_type = 'IP_BAN' AND is_active = 1
                """;

        synchronized (connection) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, ipAddress);
                statement.executeUpdate();
            } catch (SQLException exception) {
                throw new RuntimeException("Failed to remove IP ban", exception);
            }
        }
    }

    public PunishmentData getActivePunishment(UUID playerUuid, PunishmentData.PunishmentType type) {
        String query = """
                SELECT * FROM punishments
                WHERE player_uuid = ? AND punishment_type = ? AND is_active = 1
                ORDER BY created_at DESC LIMIT 1
                """;

        PunishmentData punishment;
        synchronized (connection) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, playerUuid.toString());
                statement.setString(2, type.name());

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (!resultSet.next()) return null;
                    punishment = mapResultSet(resultSet);
                }
            } catch (SQLException exception) {
                throw new RuntimeException("Failed to get punishment", exception);
            }
        }

        if (punishment.isExpired()) {
            removePunishment(playerUuid, type);
            return null;
        }
        return punishment;
    }

    public PunishmentData getActiveIpBan(String ipAddress) {
        String query = """
                SELECT * FROM punishments
                WHERE ip_address = ? AND punishment_type = 'IP_BAN' AND is_active = 1
                ORDER BY created_at DESC LIMIT 1
                """;

        PunishmentData punishment;
        synchronized (connection) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, ipAddress);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (!resultSet.next()) return null;
                    punishment = mapResultSet(resultSet);
                }
            } catch (SQLException exception) {
                throw new RuntimeException("Failed to get IP ban", exception);
            }
        }

        if (punishment.isExpired()) {
            removeIpPunishment(ipAddress);
            return null;
        }
        return punishment;
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

    public void addBan(Player target, Player moderator, String reason, Long expiresAt) {
        addPunishment(
                target.getUniqueId(),
                target.getName(),
                PunishmentData.PunishmentType.BAN,
                reason,
                moderator.getUniqueId(),
                moderator.getName(),
                expiresAt,
                getPlayerIp(target)
        );
    }

    public void addMute(Player target, Player moderator, String reason, Long expiresAt) {
        addPunishment(
                target.getUniqueId(),
                target.getName(),
                PunishmentData.PunishmentType.MUTE,
                reason,
                moderator.getUniqueId(),
                moderator.getName(),
                expiresAt,
                getPlayerIp(target)
        );
    }

    public void addIpBan(Player target, Player moderator, String reason, Long expiresAt) {
        addPunishment(
                target.getUniqueId(),
                target.getName(),
                PunishmentData.PunishmentType.IP_BAN,
                reason,
                moderator.getUniqueId(),
                moderator.getName(),
                expiresAt,
                getPlayerIp(target)
        );
    }

    private PunishmentData mapResultSet(ResultSet resultSet) throws SQLException {
        Long expiresAt = resultSet.getLong("expires_at");
        if (resultSet.wasNull()) expiresAt = null;

        return new PunishmentData(
                resultSet.getInt("id"),
                UUID.fromString(resultSet.getString("player_uuid")),
                resultSet.getString("player_name"),
                PunishmentData.PunishmentType.valueOf(resultSet.getString("punishment_type")),
                resultSet.getString("reason"),
                UUID.fromString(resultSet.getString("moderator_uuid")),
                resultSet.getString("moderator_name"),
                resultSet.getLong("created_at"),
                expiresAt,
                resultSet.getInt("is_active") == 1,
                resultSet.getString("ip_address")
        );
    }

    public List<PunishmentData> getAllPunishments(String playerName) {
        String query = """
                SELECT * FROM punishments
                WHERE LOWER(player_name) = LOWER(?)
                ORDER BY created_at DESC
                """;

        List<PunishmentData> list = new ArrayList<>();
        synchronized (connection) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, playerName);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        list.add(mapResultSet(resultSet));
                    }
                }
            } catch (SQLException exception) {
                throw new RuntimeException("Failed to fetch player punishments", exception);
            }
        }
        return list;
    }

    public static String getPlayerIp(Player player) {
        InetSocketAddress address = player.getAddress();
        if (address == null || address.getAddress() == null) return "0.0.0.0";
        String host = address.getAddress().getHostAddress();
        return host != null ? host : "0.0.0.0";
    }
}
