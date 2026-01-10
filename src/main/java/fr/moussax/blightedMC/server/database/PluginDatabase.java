package fr.moussax.blightedMC.server.database;

import fr.moussax.blightedMC.utils.debug.Log;

import java.sql.*;
import java.util.UUID;

public class PluginDatabase {
    private final Connection connection;

    public PluginDatabase(String path) throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        initializeSchema();
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            Log.error("PluginDatabase", e.getMessage());
            throw new RuntimeException("Unable to close the database connection");
        }
    }

    private void initializeSchema() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                CREATE TABLE IF NOT EXISTS players (
                    uuid TEXT PRIMARY KEY,
                    name TEXT NOT NULL,
                    gems INTEGER NOT NULL DEFAULT 0,
                    mana REAL NOT NULL DEFAULT 0,
                    forge_fuel INTEGER NOT NULL DEFAULT 0
                )
                """
            );

            statement.execute("""
                CREATE TABLE IF NOT EXISTS blighted_blocks (
                    world_uid TEXT NOT NULL,
                    x INTEGER NOT NULL,
                    y INTEGER NOT NULL,
                    z INTEGER NOT NULL,
                    block_id TEXT NOT NULL,
                    PRIMARY KEY (world_uid, x, y, z)
                )
                """
            );

            statement.execute("""
                CREATE TABLE IF NOT EXISTS punishments (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    player_uuid TEXT NOT NULL,
                    player_name TEXT NOT NULL,
                    punishment_type TEXT NOT NULL,
                    reason TEXT NOT NULL,
                    moderator_uuid TEXT NOT NULL,
                    moderator_name TEXT NOT NULL,
                    created_at INTEGER NOT NULL,
                    expires_at INTEGER,
                    is_active INTEGER NOT NULL DEFAULT 1,
                    ip_address TEXT
                )
                """
            );

            statement.execute("""
                CREATE INDEX IF NOT EXISTS idx_punishments_player 
                ON punishments(player_uuid, is_active)
                """
            );

            statement.execute("""
                CREATE INDEX IF NOT EXISTS idx_punishments_ip 
                ON punishments(ip_address, is_active)
                """
            );

            try {
                statement.execute("ALTER TABLE players ADD COLUMN forge_fuel INTEGER NOT NULL DEFAULT 0");
            } catch (SQLException ignored) {
            }
        }
    }

    public void addBlock(UUID worldId, int x, int y, int z, String blockId) {
        String query = "INSERT OR REPLACE INTO blighted_blocks(world_uid, x, y, z, block_id) VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, worldId.toString());
            statement.setInt(2, x);
            statement.setInt(3, y);
            statement.setInt(4, z);
            statement.setString(5, blockId);
            statement.executeUpdate();
        } catch (SQLException e) {
            Log.error("PluginDatabase", e.getMessage());
        }
    }

    public void removeBlock(UUID worldId, int x, int y, int z) {
        String query = "DELETE FROM blighted_blocks WHERE world_uid = ? AND x = ? AND y = ? AND z = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, worldId.toString());
            statement.setInt(2, x);
            statement.setInt(3, y);
            statement.setInt(4, z);
            statement.executeUpdate();
        } catch (SQLException e) {
            Log.error("PluginDatabase", e.getMessage());
        }
    }

    public String getBlockId(UUID worldId, int x, int y, int z) {
        String query = "SELECT block_id FROM blighted_blocks WHERE world_uid = ? AND x = ? AND y = ? AND z = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, worldId.toString());
            statement.setInt(2, x);
            statement.setInt(3, y);
            statement.setInt(4, z);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("block_id");
                }
            }
        } catch (SQLException e) {
            Log.error("PluginDatabase", e.getMessage());
        }
        return null;
    }
}
