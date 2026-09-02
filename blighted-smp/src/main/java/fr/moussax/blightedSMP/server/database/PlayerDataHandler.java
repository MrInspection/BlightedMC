package fr.moussax.blightedSMP.server.database;

import fr.moussax.blightedSMP.BlightedSMP;
import lombok.Getter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Manages SQLite database persistence for player resources and forge fuel state.
 */
public final class PlayerDataHandler {
    private final UUID playerId;
    private final String playerName;
    private final Connection connection;

    @Getter
    private int savedGems;
    @Getter
    private double savedMana;
    @Getter
    private int savedForgeFuel;

    /**
     * Creates a player data handler for the specified player, loading initial state from storage.
     *
     * @param playerId   unique identifier of the player
     * @param playerName current username of the player
     */
    public PlayerDataHandler(UUID playerId, String playerName) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.connection = BlightedSMP.getInstance().getDatabase().getConnection();

        load();
    }

    /**
     * Persists player gems, mana, and forge fuel state to the database.
     *
     * @param gems      current gems balance to save
     * @param mana      current mana level to save
     * @param forgeFuel current forge fuel amount to save
     */
    public void save(int gems, double mana, int forgeFuel) {
        this.savedGems = gems;
        this.savedMana = mana;
        this.savedForgeFuel = forgeFuel;

        String query = """
                INSERT INTO players (uuid, name, gems, mana, forge_fuel)
                VALUES (?, ?, ?, ?, ?)
                ON CONFLICT(uuid) DO UPDATE SET
                  name = excluded.name,
                  gems = excluded.gems,
                  mana = excluded.mana,
                  forge_fuel = excluded.forge_fuel
                """;

        synchronized (connection) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, playerId.toString());
                statement.setString(2, playerName);
                statement.setInt(3, gems);
                statement.setDouble(4, mana);
                statement.setInt(5, forgeFuel);
                statement.executeUpdate();
            } catch (SQLException exception) {
                throw new RuntimeException("Failed to save player data to database.", exception);
            }
        }
    }

    private void load() {
        String query = "SELECT name, gems, mana, forge_fuel FROM players WHERE uuid = ?";

        synchronized (connection) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, playerId.toString());

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        this.savedGems = resultSet.getInt("gems");
                        this.savedMana = resultSet.getDouble("mana");
                        this.savedForgeFuel = resultSet.getInt("forge_fuel");

                        String storedName = resultSet.getString("name");
                        if (!storedName.equals(playerName)) {
                            save(savedGems, savedMana, savedForgeFuel);
                        }
                    } else {
                        this.savedGems = 0;
                        this.savedMana = 100.0;
                        this.savedForgeFuel = 0;
                        save(savedGems, savedMana, savedForgeFuel);
                    }
                }
            } catch (SQLException exception) {
                throw new RuntimeException("Failed to load player data from database.", exception);
            }
        }
    }
}
