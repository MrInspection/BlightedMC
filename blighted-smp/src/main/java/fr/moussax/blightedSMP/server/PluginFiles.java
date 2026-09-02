package fr.moussax.blightedSMP.server;

import lombok.Getter;

/**
 * Declares plugin configuration and database file names.
 */
public enum PluginFiles {

    /**
     * Primary YAML configuration file name.
     */
    CONFIG("config.yml"),

    /**
     * Primary SQLite database file name.
     */
    DATABASE("blighted_database.db");

    /**
     * Relative file name within the plugin data directory.
     */
    @Getter
    private final String fileName;

    PluginFiles(String fileName) {
        this.fileName = fileName;
    }
}
