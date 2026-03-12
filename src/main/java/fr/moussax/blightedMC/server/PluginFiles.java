package fr.moussax.blightedMC.server;

import fr.moussax.blightedMC.BlightedMC;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public enum PluginFiles {
    CONFIG("config.yml"),
    DATABASE("blighted_database.db");

    @Getter
    private final String fileName;
    private final File dataFolder;

    PluginFiles(String fileName) {
        this.fileName = fileName;
        this.dataFolder = BlightedMC.getInstance().getDataFolder();
    }

    public File getFile() {
        return new File(dataFolder, fileName);
    }

    public FileConfiguration getFileConfig() {
        return YamlConfiguration.loadConfiguration(getFile());
    }

    public void saveFileConfig(FileConfiguration config) {
        try {
            config.save(getFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
