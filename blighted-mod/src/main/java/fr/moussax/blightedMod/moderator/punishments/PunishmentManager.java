package fr.moussax.blightedMod.moderator.punishments;

import fr.moussax.bedrock.utils.debug.Log;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.net.InetSocketAddress;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class PunishmentManager {
    public static final UUID CONSOLE_UUID = UUID.nameUUIDFromBytes("CONSOLE".getBytes(java.nio.charset.StandardCharsets.UTF_8));

    private final Connection connection;
    private final Set<UUID> activeMutedPlayers = ConcurrentHashMap.newKeySet();
    private final Set<UUID> activeBannedPlayers = ConcurrentHashMap.newKeySet();
    private final Map<String, OfflineTargetIdentity> playerIdentityCache = new ConcurrentHashMap<>();

    public PunishmentManager(@NonNull Connection connection) {
        this.connection = connection;
        initializeCache();
    }

    private void initializeCache() {
        String query = """
                SELECT DISTINCT player_uuid, player_name, punishment_type, ip_address, expires_at
                FROM punishments
                WHERE is_active = 1
                """;
        synchronized (connection) {
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {
                long currentTime = System.currentTimeMillis();
                while (resultSet.next()) {
                    Long expiresAt = resultSet.getLong("expires_at");
                    if (resultSet.wasNull()) expiresAt = null;
                    if (expiresAt != null && currentTime > expiresAt) continue;

                    UUID playerUuid = UUID.fromString(resultSet.getString("player_uuid"));
                    String playerName = resultSet.getString("player_name");
                    String ipAddress = resultSet.getString("ip_address");
                    PunishmentData.PunishmentType type = PunishmentData.PunishmentType.valueOf(resultSet.getString("punishment_type"));

                    if (type == PunishmentData.PunishmentType.MUTE) {
                        activeMutedPlayers.add(playerUuid);
                    } else if (type == PunishmentData.PunishmentType.BAN || type == PunishmentData.PunishmentType.IP_BAN) {
                        activeBannedPlayers.add(playerUuid);
                    }

                    if (playerName != null && !playerName.isBlank()) {
                        playerIdentityCache.put(playerName.toLowerCase(Locale.ROOT), new OfflineTargetIdentity(playerUuid, playerName, ipAddress));
                    }
                }
            } catch (SQLException exception) {
                Log.error("PunishmentManager", "Failed to initialize active punishments cache: " + exception.getMessage());
            }
        }
    }

    public void addPunishment(@NonNull PunishmentRequest request) {
        String query = """
                INSERT INTO punishments (player_uuid, player_name, punishment_type, reason,
                                         moderator_uuid, moderator_name, created_at, expires_at, ip_address)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        synchronized (connection) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, request.playerUuid().toString());
                statement.setString(2, request.playerName());
                statement.setString(3, request.type().name());
                statement.setString(4, request.reason());
                statement.setString(5, request.moderatorUuid().toString());
                statement.setString(6, request.moderatorName());
                statement.setLong(7, System.currentTimeMillis());
                if (request.expiresAt() != null) {
                    statement.setLong(8, request.expiresAt());
                } else {
                    statement.setNull(8, Types.INTEGER);
                }
                statement.setString(9, request.ipAddress());
                statement.executeUpdate();

                if (request.type() == PunishmentData.PunishmentType.MUTE) {
                    activeMutedPlayers.add(request.playerUuid());
                } else if (request.type() == PunishmentData.PunishmentType.BAN || request.type() == PunishmentData.PunishmentType.IP_BAN) {
                    activeBannedPlayers.add(request.playerUuid());
                }

                playerIdentityCache.put(request.playerName().toLowerCase(Locale.ROOT),
                        new OfflineTargetIdentity(request.playerUuid(), request.playerName(), request.ipAddress()));
            } catch (SQLException exception) {
                throw new RuntimeException("Failed to add " + request.type() + " punishment for player " + request.playerName(), exception);
            }
        }
    }

    public void addPunishment(@NonNull UUID playerUuid, @NonNull String playerName, PunishmentData.@NonNull PunishmentType type,
                              @NonNull String reason, @NonNull UUID moderatorUuid, @NonNull String moderatorName,
                              @Nullable Long expiresAt, @Nullable String ipAddress) {
        addPunishment(new PunishmentRequest(playerUuid, playerName, type, reason, moderatorUuid, moderatorName, expiresAt, ipAddress));
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

                if (type == PunishmentData.PunishmentType.MUTE) {
                    activeMutedPlayers.remove(playerUuid);
                } else if (type == PunishmentData.PunishmentType.BAN || type == PunishmentData.PunishmentType.IP_BAN) {
                    activeBannedPlayers.remove(playerUuid);
                }
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

    private void deactivatePunishment(int id) {
        String query = """
                UPDATE punishments SET is_active = 0
                WHERE id = ? AND is_active = 1
                """;

        synchronized (connection) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, id);
                statement.executeUpdate();
            } catch (SQLException exception) {
                throw new RuntimeException("Failed to deactivate punishment", exception);
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
            deactivatePunishment(punishment.id());
            if (type == PunishmentData.PunishmentType.MUTE) {
                activeMutedPlayers.remove(playerUuid);
            } else if (type == PunishmentData.PunishmentType.BAN || type == PunishmentData.PunishmentType.IP_BAN) {
                activeBannedPlayers.remove(playerUuid);
            }
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
            deactivatePunishment(punishment.id());
            return null;
        }
        return punishment;
    }

    public OfflineTargetIdentity resolvePlayerIdentity(String playerName) {
        Player onlinePlayer = Bukkit.getPlayerExact(playerName);
        if (onlinePlayer != null) {
            OfflineTargetIdentity identity = new OfflineTargetIdentity(onlinePlayer.getUniqueId(), onlinePlayer.getName(), getPlayerIp(onlinePlayer));
            playerIdentityCache.put(playerName.toLowerCase(Locale.ROOT), identity);
            return identity;
        }

        OfflineTargetIdentity cachedIdentity = playerIdentityCache.get(playerName.toLowerCase(Locale.ROOT));
        if (cachedIdentity != null) {
            return cachedIdentity;
        }

        String query = """
                SELECT player_uuid, player_name, ip_address FROM punishments
                WHERE LOWER(player_name) = LOWER(?)
                ORDER BY created_at DESC LIMIT 1
                """;
        synchronized (connection) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, playerName);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        UUID uniqueId = UUID.fromString(resultSet.getString("player_uuid"));
                        String resolvedName = resultSet.getString("player_name");
                        String ipAddress = resultSet.getString("ip_address");
                        OfflineTargetIdentity identity = new OfflineTargetIdentity(uniqueId, resolvedName, ipAddress);
                        playerIdentityCache.put(playerName.toLowerCase(Locale.ROOT), identity);
                        return identity;
                    }
                }
            } catch (SQLException exception) {
                Log.error("PunishmentManager", "Failed to resolve player identity for " + playerName + ": " + exception.getMessage());
            }
        }
        return null;
    }

    public UUID resolveBannedUuidByName(String playerName) {
        OfflineTargetIdentity identity = resolvePlayerIdentity(playerName);
        if (identity == null) return null;

        PunishmentData ban = getActivePunishment(identity.uniqueId(), PunishmentData.PunishmentType.BAN);
        return ban != null ? identity.uniqueId() : null;
    }

    public boolean isMuted(UUID playerUuid) {
        if (!activeMutedPlayers.contains(playerUuid)) {
            return false;
        }
        return getActivePunishment(playerUuid, PunishmentData.PunishmentType.MUTE) != null;
    }

    public boolean isBanned(UUID playerUuid) {
        if (!activeBannedPlayers.contains(playerUuid)) {
            return false;
        }
        return getActivePunishment(playerUuid, PunishmentData.PunishmentType.BAN) != null;
    }

    public boolean isIpBanned(String ipAddress) {
        return getActiveIpBan(ipAddress) != null;
    }

    public static UUID getModeratorUuid(CommandSender sender) {
        return sender instanceof Player player ? player.getUniqueId() : CONSOLE_UUID;
    }

    public void addBan(OfflineTargetIdentity targetIdentity, CommandSender moderator, String reason, Long expiresAt) {
        addPunishment(PunishmentRequest.builder()
                .target(targetIdentity)
                .type(PunishmentData.PunishmentType.BAN)
                .moderator(moderator)
                .reason(reason)
                .expiresAt(expiresAt)
                .build());
    }

    public void addBan(Player target, CommandSender moderator, String reason, Long expiresAt) {
        addPunishment(PunishmentRequest.builder()
                .target(target)
                .type(PunishmentData.PunishmentType.BAN)
                .moderator(moderator)
                .reason(reason)
                .expiresAt(expiresAt)
                .build());
    }

    public void addMute(OfflineTargetIdentity targetIdentity, CommandSender moderator, String reason, Long expiresAt) {
        addPunishment(PunishmentRequest.builder()
                .target(targetIdentity)
                .type(PunishmentData.PunishmentType.MUTE)
                .moderator(moderator)
                .reason(reason)
                .expiresAt(expiresAt)
                .build());
    }

    public void addMute(Player target, CommandSender moderator, String reason, Long expiresAt) {
        addPunishment(PunishmentRequest.builder()
                .target(target)
                .type(PunishmentData.PunishmentType.MUTE)
                .moderator(moderator)
                .reason(reason)
                .expiresAt(expiresAt)
                .build());
    }

    public void addIpBan(OfflineTargetIdentity targetIdentity, CommandSender moderator, String reason, Long expiresAt) {
        addPunishment(PunishmentRequest.builder()
                .target(targetIdentity)
                .type(PunishmentData.PunishmentType.IP_BAN)
                .moderator(moderator)
                .reason(reason)
                .expiresAt(expiresAt)
                .build());
    }

    public void addIpBan(Player target, CommandSender moderator, String reason, Long expiresAt) {
        addPunishment(PunishmentRequest.builder()
                .target(target)
                .type(PunishmentData.PunishmentType.IP_BAN)
                .moderator(moderator)
                .reason(reason)
                .expiresAt(expiresAt)
                .build());
    }

    public void addKick(OfflineTargetIdentity targetIdentity, CommandSender moderator, String reason) {
        addPunishment(PunishmentRequest.builder()
                .target(targetIdentity)
                .type(PunishmentData.PunishmentType.KICK)
                .moderator(moderator)
                .reason(reason)
                .build());
    }

    public void addKick(Player target, CommandSender moderator, String reason) {
        addPunishment(PunishmentRequest.builder()
                .target(target)
                .type(PunishmentData.PunishmentType.KICK)
                .moderator(moderator)
                .reason(reason)
                .build());
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
        try {
            if (player == null) return "0.0.0.0";
            InetSocketAddress address = player.getAddress();
            if (address == null || address.getAddress() == null) return "0.0.0.0";
            String host = address.getAddress().getHostAddress();
            return host != null ? host : "0.0.0.0";
        } catch (Throwable _) {
            return "0.0.0.0";
        }
    }
}
