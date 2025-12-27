package fr.moussax.blightedMC;

import fr.moussax.blightedMC.registry.EventsRegistry;
import fr.moussax.blightedMC.registry.RegistrySystem;
import fr.moussax.blightedMC.server.BlightedServer;
import fr.moussax.blightedMC.server.PluginFiles;
import fr.moussax.blightedMC.server.PluginSettings;
import fr.moussax.blightedMC.server.database.PluginDatabase;
import fr.moussax.blightedMC.utils.commands.CommandBuilder;
import fr.moussax.blightedMC.utils.debug.Log;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.sql.SQLException;

public final class BlightedMC extends JavaPlugin {
    private static BlightedMC instance;
    private PluginSettings settings;
    private PluginDatabase database;
    private EventsRegistry eventsRegistry;

    @Override
    public void onEnable() {
        instance = this;

        Log.info("Plugin", "Initializing BlightedMC plugin...");
        String config = PluginFiles.CONFIG.getFileName();
        saveResourcesAs(config, config);

        settings = PluginSettings.load(this);
        initializeDatabase();

        BlightedServer.initialize(this);
        BlightedServer.getInstance().configureServer();

        CommandBuilder.initializeCommands();
        RegistrySystem.initialize();
        eventsRegistry = new EventsRegistry();
        eventsRegistry.initializeListeners();

        BlightedServer.getInstance().rehydrateEntitiesOnLoadedChunks();
    }

    private void initializeDatabase() {
        try {
            database = new PluginDatabase(getDataFolder().getAbsolutePath() + "/" + PluginFiles.DATABASE.getFileName());
            Log.success("Database", "Successfully connected to the database.");
        } catch (SQLException e) {
            Log.debug(e.getMessage());
            Log.error("Database", "Unable to connect to the database.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    private void saveResourcesAs(String resourcePath, String destinationPath) {
        if (resourcePath.isEmpty()) throw new IllegalArgumentException("Resource path cannot be null or empty.");

        try (InputStream in = getResource(resourcePath)) {
            if (in == null) throw new IllegalArgumentException("Resource cannot be found at path: " + resourcePath);

            if (!getDataFolder().exists() && !getDataFolder().mkdirs()) {
                Log.error("Config", "Unable to create data folder.");
                throw new IllegalStateException("Failed to create plugin data folder.");
            }

            File outputFile = new File(getDataFolder(), destinationPath);

            try (OutputStream out = new FileOutputStream(outputFile)) {
                in.transferTo(out);
            }

            Log.success("Config", "Successfully created the " + resourcePath + " file.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        database.closeConnection();
        RegistrySystem.clear();
    }

    public PluginSettings getSettings() {
        return settings;
    }

    public PluginDatabase getDatabase() {
        return database;
    }

    public static BlightedMC getInstance() {
        return instance;
    }
}
