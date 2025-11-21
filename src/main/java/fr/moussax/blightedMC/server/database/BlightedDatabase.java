package fr.moussax.blightedMC.server.database;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class BlightedDatabase {
    private final Connection connection;

    public BlightedDatabase(@Nonnull String path) throws SQLException {
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
            statement.execute(
                    """
                            CREATE TABLE IF NOT EXISTS players (
                                uuid TEXT PRIMARY KEY,
                                name TEXT NOT NULL,
                                gems INTEGER NOT NULL DEFAULT 0,
                                mana REAL NOT NULL DEFAULT 0
                            )
                            """
            );
        }
    }
}
