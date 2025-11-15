package fr.moussax.blightedMC.server.database;

import fr.moussax.blightedMC.BlightedMC;

import javax.annotation.Nonnull;
import java.sql.*;
import java.util.UUID;

public class PlayerDataHandler {
  private final UUID playerId;
  private final String playerName;
  private final Connection connection;

  private int gems;
  private double mana;

  public PlayerDataHandler(@Nonnull UUID playerId, @Nonnull String playerName) {
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

  public void setGems(int gems) {
    this.gems = gems;
  }

  public void setMana(double mana) {
    this.mana = mana;
  }

  public void save() {
    String query = """
      INSERT INTO players (uuid, name, gems, mana)
      VALUES (?, ?, ?, ?)
      ON CONFLICT(uuid) DO UPDATE SET
        name = excluded.name,
        gems = excluded.gems,
        mana = excluded.mana
      """;

    try (PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setString(1, playerId.toString());
      statement.setString(2, playerName);
      statement.setInt(3, gems);
      statement.setDouble(4, mana);
      statement.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Failed to save player data to database.", e);
    }
  }

  private void load() {
    String query = "SELECT name, gems, mana FROM players WHERE uuid = ?";

    try (PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setString(1, playerId.toString());

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          this.gems = resultSet.getInt("gems");
          this.mana = resultSet.getDouble("mana");

          String storedName = resultSet.getString("name");
          if (!storedName.equals(playerName)) {
            save();
          }
        } else {
          this.gems = 0;
          this.mana = 100.0;
          save();
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to load player data from database.", e);
    }
  }
}
