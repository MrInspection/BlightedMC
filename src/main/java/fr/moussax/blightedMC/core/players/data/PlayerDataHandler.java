package fr.moussax.blightedMC.core.players.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.moussax.blightedMC.BlightedMC;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

public class PlayerDataHandler {
  private final File file;
  private final Gson gson;
  private PlayerData playerData;

  public PlayerDataHandler(UUID playerId, String playerName) {
    File directory = new File(BlightedMC.getInstance().getDataFolder(), "players");
    if (!directory.exists()) directory.mkdirs();

    this.file = new File(directory, playerId + ".json");
    this.gson = new GsonBuilder().setPrettyPrinting().create();

    if (!file.exists()) {
      this.playerData = new PlayerData(playerName, playerId.toString(), 0, 100.0);
      save();
    } else {
      load();
      // Backward compatibility: initialize missing mana to full if absent (older files)
      if (playerData.getMana() <= 0) {
        playerData.setMana(100.0);
        save();
      }
      if (!playerData.getName().equals(playerName)) {
        playerData.setName(playerName);
        save();
      }
    }
  }

  public PlayerData getPlayerData() {
    return playerData;
  }

  public void setFavors(int favors) {
    playerData.setGems(favors);
  }

  public void setMana(double mana) {
    playerData.setMana(mana);
  }

  public void save() {
    try (FileWriter writer = new FileWriter(file)) {
      gson.toJson(playerData, writer);
    } catch (IOException e) {
      throw new RuntimeException("Failed to save player data file.", e);
    }
  }

  private void load() {
    try (FileReader reader = new FileReader(file)) {
      this.playerData = gson.fromJson(reader, PlayerData.class);
    } catch (IOException e) {
      throw new RuntimeException("Failed to load player data file.", e);
    }
  }
}
