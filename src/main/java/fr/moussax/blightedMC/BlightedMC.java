package fr.moussax.blightedMC;

import fr.moussax.blightedMC.registry.EventsRegistry;
import fr.moussax.blightedMC.registry.RegistrySystem;
import fr.moussax.blightedMC.server.BlightedServer;
import fr.moussax.blightedMC.server.PluginFiles;
import fr.moussax.blightedMC.server.PluginSettings;
import fr.moussax.blightedMC.server.database.PluginDatabase;
import fr.moussax.blightedMC.smp.core.entities.spawnable.engine.BlightedSpawnEngine;
import fr.moussax.blightedMC.smp.core.shared.ui.menu.system.MenuManager;
import fr.moussax.blightedMC.smp.core.shared.ui.menu.system.MenuSystem;
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
        BlightedServer.initialize(this);
        BlightedServer.getInstance().configureServer();

        String config = PluginFiles.CONFIG.getFileName();
        saveResourcesAs(config, config);

        settings = PluginSettings.load(this);
        initializeDatabase();

        CommandBuilder.initializeCommands();
        RegistrySystem.initialize();
        eventsRegistry = new EventsRegistry();
        eventsRegistry.initializeListeners();

        BlightedServer.getInstance().rehydrateEntitiesOnLoadedChunks();
        new BlightedSpawnEngine().runTaskTimer(this, 100L, 1L);
    }

    @Override
    public void onDisable() {
        database.closeConnection();
        eventsRegistry.shutdownMenus();
        RegistrySystem.clear();
    }

    /**
     * Retrieves the plugin settings.
     *
     * @return the plugin settings instance
     */
    public PluginSettings getSettings() {
        return settings;
    }

    /**
     * Retrieves the plugin database connection.
     *
     * @return the plugin database instance
     */
    public PluginDatabase getDatabase() {
        return database;
    }

    /**
     * Retrieves the instance of the menu manager.
     *
     * @return the menu manager
     */
    public MenuManager getMenuManager() {
        return eventsRegistry.getMenuManager();
    }

    /**
     * Retrieves the instance of the menu system.
     *
     * @return the menu system
     */
    public MenuSystem getMenuSystem() {
        return eventsRegistry.getMenuSystem();
    }

    /**
     * Static accessor to the plugin's menu manager.
     *
     * @return the menu manager
     */
    public static MenuManager menuManager() {
        return instance.getMenuManager();
    }

    /**
     * Retrieves the singleton instance of the plugin.
     *
     * @return the plugin instance
     */
    public static BlightedMC getInstance() {
        return instance;
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
}
