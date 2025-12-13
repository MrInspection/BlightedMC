package fr.moussax.blightedMC.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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
            e.printStackTrace();
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

            try {
                statement.execute("ALTER TABLE players ADD COLUMN forge_fuel INTEGER NOT NULL DEFAULT 0");
            } catch (SQLException ignored) {
                // Column likely exists
            }
        }
    }
}
