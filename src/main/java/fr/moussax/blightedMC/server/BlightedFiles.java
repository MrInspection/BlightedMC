package fr.moussax.blightedMC.server;

import fr.moussax.blightedMC.BlightedMC;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public enum BlightedFiles {
    CONFIG("config.yml"),
    DATABASE("blighted.db"),
    CUSTOM_BLOCKS("custom_blocks.yml");

    private final String fileName;
    private final File dataFolder;

    BlightedFiles(String fileName) {
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

    public String getFileName() {
        return fileName;
    }
}
