package fr.moussax.blightedMC.core.players;

import fr.moussax.blightedMC.BlightedMC;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayerDataHandler {
  private final File file;
  private final FileConfiguration config;

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

  public FileConfiguration getConfig() {
    return config;
  }

  public void save() {
    try {
      config.save(file);
    } catch (IOException e) {
      throw new RuntimeException("Failed to save player data file.", e);
    }
  }
}
