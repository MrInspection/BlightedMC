package fr.moussax.blightedMod.database;

import fr.moussax.bedrock.utils.debug.Log;
import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Getter
public final class PluginDatabase {
    private final Connection connection;

    public PluginDatabase(String path) throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        initializeSchema();
    }

    public void closeConnection() {
        if (connection == null) return;
        synchronized (connection) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException exception) {
                Log.error("PluginDatabase", exception.getMessage());
                throw new RuntimeException("Unable to close the database connection", exception);
            }
        }
    }

    private void initializeSchema() throws SQLException {
        synchronized (connection) {
            try (Statement statement = connection.createStatement()) {
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
            }
        }
    }
}
