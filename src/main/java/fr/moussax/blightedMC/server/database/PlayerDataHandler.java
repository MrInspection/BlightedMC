package fr.moussax.blightedMC.server.database;

import fr.moussax.blightedMC.BlightedMC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerDataHandler {
    private final UUID playerId;
    private final String playerName;
    private final Connection connection;

    private int gems;
    private double mana;
    private int forgeFuel;

    public PlayerDataHandler(UUID playerId, String playerName) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.connection = BlightedMC.getInstance().getDatabase().getConnection();

        load();
    }

    public int getGems() {
        return gems;
    }

    public double getMana() {
        return mana;
    }

    public int getForgeFuel() {
        return forgeFuel;
    }

    public void setGems(int gems) {
        this.gems = gems;
    }

    public void setMana(double mana) {
        this.mana = mana;
    }

    public void setForgeFuel(int forgeFuel) {
        this.forgeFuel = forgeFuel;
    }

    public void save() {
        String query = """
            INSERT INTO players (uuid, name, gems, mana, forge_fuel)
            VALUES (?, ?, ?, ?, ?)
            ON CONFLICT(uuid) DO UPDATE SET
              name = excluded.name,
              gems = excluded.gems,
              mana = excluded.mana,
              forge_fuel = excluded.forge_fuel
            """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, playerId.toString());
            statement.setString(2, playerName);
            statement.setInt(3, gems);
            statement.setDouble(4, mana);
            statement.setInt(5, forgeFuel);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save player data to database.", e);
        }
    }

    private void load() {
        String query = "SELECT name, gems, mana, forge_fuel FROM players WHERE uuid = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, playerId.toString());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    this.gems = resultSet.getInt("gems");
                    this.mana = resultSet.getDouble("mana");
                    this.forgeFuel = resultSet.getInt("forge_fuel");

                    String storedName = resultSet.getString("name");
                    if (!storedName.equals(playerName)) {
                        save();
                    }
                } else {
                    this.gems = 0;
                    this.mana = 100.0;
                    this.forgeFuel = 0;
                    save();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load player data from database.", e);
        }
    }
}
