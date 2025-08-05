package fr.moussax.blightedMC.core.players;

import fr.moussax.blightedMC.BlightedMC;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Handles loading, saving, and managing per-player YAML configuration files.
 * <p>
 * Each player's data is stored in a separate YAML file named by their UUID
 * within the plugin's "players" data folder.
 * <p>
 * Provides access to the player's configuration for reading and writing,
 * and handles file creation and persistence.
 */
public class PlayerDataHandler {
  private final File file;
  private final FileConfiguration config;

  /**
   * Constructs a handler for a specific player's data file.
   * <p>
   * Ensures the data folder and file exist, creating them if necessary,
   * and loads the configuration into memory.
   *
   * @param uuid the unique identifier of the player
   * @throws RuntimeException if file creation fails
   */
  public PlayerDataHandler(UUID uuid){
    File directory = new File(BlightedMC.getInstance().getDataFolder(), "players");
    if(!directory.exists()) directory.mkdirs();

    this.file = new File(directory, uuid + ".yaml");
    if(!file.exists()) {
      try{
        file.createNewFile();
      } catch (IOException e) {
        throw new RuntimeException("Failed to create player data file.", e);
      }
    }

    this.config = YamlConfiguration.loadConfiguration(file);
  }

  /**
   * Gets the player's YAML configuration instance.
   *
   * @return the loaded FileConfiguration for this player
   */
  public FileConfiguration getConfig() {
    return config;
  }

  /**
   * Saves the current state of the player's configuration back to disk.
   *
   * @throws RuntimeException if saving the file fails
   */
  public void save() {
    try {
      config.save(file);
    } catch (IOException e) {
      throw new RuntimeException("Failed to save player data file.", e);
    }
  }
}
