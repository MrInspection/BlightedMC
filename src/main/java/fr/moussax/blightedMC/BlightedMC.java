package fr.moussax.blightedMC;

import fr.moussax.blightedMC.engine.fishing.hooks.LavaFishingHook;
import fr.moussax.blightedMC.engine.fishing.hooks.VoidFishingHook;
import fr.moussax.blightedMC.registry.CommandsRegistry;
import fr.moussax.blightedMC.registry.EventsRegistry;
import fr.moussax.blightedMC.registry.RegistrySystem;
import fr.moussax.blightedMC.server.BlightedServer;
import fr.moussax.blightedMC.server.PluginFiles;
import fr.moussax.blightedMC.server.PluginSettings;
import fr.moussax.blightedMC.server.database.PluginDatabase;
import fr.moussax.blightedMC.engine.entities.spawnable.engine.BlightedSpawnEngine;
import fr.moussax.blightedMC.shared.ui.actionbar.ActionbarService;
import fr.moussax.blightedMC.shared.ui.menu.system.MenuManager;
import fr.moussax.blightedMC.shared.ui.menu.system.MenuSystem;
import fr.moussax.blightedMC.utils.debug.Log;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

/**
 * Main plugin entry point for BlightedMC.
 *
 * <p>Coordinates initialization and shutdown of the plugin's core systems,
 * including server configuration, persistence, registries, commands, event
 * listeners, entity spawning, and custom fishing hooks.</p>
 */
public final class BlightedMC extends JavaPlugin {

    @Getter
    private static BlightedMC instance;
    @Getter
    private PluginSettings settings;
    @Getter
    private PluginDatabase database;
    @Getter
    private EventsRegistry eventsRegistry;

    @Override
    public void onEnable() {
        instance = this;

        Log.info("Plugin", "Initializing BlightedMC plugin...");
        BlightedServer.initialize(this);
        BlightedServer.getInstance().configureServer();

        String config = PluginFiles.CONFIG.getFileName();
        saveResource(config, false);

        settings = PluginSettings.load(this);
        initializeDatabase();

        CommandsRegistry.register(this);
        RegistrySystem.initialize();
        eventsRegistry = new EventsRegistry();
        eventsRegistry.initializeListeners();
        eventsRegistry.buildSpawnCache();

        BlightedServer.getInstance().rehydrateEntitiesOnLoadedChunks();
        new BlightedSpawnEngine().runTaskTimer(this, 100L, 1L);
    }

    @Override
    public void onDisable() {
        LavaFishingHook.cleanupAll();
        VoidFishingHook.cleanupAll();
        database.closeConnection();
        eventsRegistry.shutdownMenus();
        eventsRegistry.cleanup();
        RegistrySystem.clear();
    }

    /**
     * Returns the menu manager associated with this plugin instance.
     *
     * @return active menu manager
     */
    public MenuManager getMenuManager() {
        return eventsRegistry.getMenuManager();
    }

    /**
     * Returns the menu system associated with this plugin instance.
     *
     * @return active menu system
     */
    public MenuSystem getMenuSystem() {
        return eventsRegistry.getMenuSystem();
    }

    /**
     * Returns the action bar service associated with this plugin instance.
     *
     * @return active action bar service, or {@code null} if not initialized
     */
    public ActionbarService getActionBarService() {
        return eventsRegistry != null ? eventsRegistry.getActionBarService() : null;
    }

    /**
     * Returns the menu manager of the active plugin instance.
     *
     * @return active menu manager
     */
    public static MenuManager menuManager() {
        return instance.getMenuManager();
    }

    /**
     * Returns the action bar service of the active plugin instance.
     *
     * @return active action bar service, or {@code null} if not initialized
     */
    public static ActionbarService actionBarService() {
        return instance != null ? instance.getActionBarService() : null;
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
}
