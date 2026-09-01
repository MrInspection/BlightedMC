package fr.moussax.blightedMC;

import fr.moussax.blightedMC.engine.entities.spawnable.engine.BlightedSpawnEngine;
import fr.moussax.blightedMC.engine.fishing.hooks.LavaFishingHook;
import fr.moussax.blightedMC.engine.fishing.hooks.VoidFishingHook;
import fr.moussax.blightedMC.registry.CommandsRegistry;
import fr.moussax.blightedMC.registry.EventsRegistry;
import fr.moussax.blightedMC.registry.RegistrySystem;
import fr.moussax.blightedMC.server.BlightedServer;
import fr.moussax.blightedMC.server.PluginFiles;
import fr.moussax.blightedMC.server.PluginSettings;
import fr.moussax.blightedMC.server.database.PluginDatabase;
import fr.moussax.blightedMC.shared.scheduling.PluginContext;
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
        PluginContext.bind(this);

        Log.info("Plugin", "Initializing BlightedMC plugin...");
        BlightedServer.configureServer();

        String config = PluginFiles.CONFIG.getFileName();
        saveResource(config, false);

        settings = PluginSettings.load(this);
        if (!initializeDatabase()) {
            return;
        }

        CommandsRegistry.register(this);
        RegistrySystem.initialize();
        eventsRegistry = new EventsRegistry();
        eventsRegistry.initializeListeners();
        eventsRegistry.buildSpawnCache();

        BlightedServer.rehydrateEntitiesOnLoadedChunks(this);
        new BlightedSpawnEngine().runTaskTimer(this, 100L, 20L);
    }

    @Override
    public void onDisable() {
        LavaFishingHook.cleanupAll();
        VoidFishingHook.cleanupAll();
        if (database != null) {
            database.closeConnection();
        }
        if (eventsRegistry != null) {
            eventsRegistry.shutdownMenus();
            eventsRegistry.cleanup();
        }
        RegistrySystem.clear();
    }

    /**
     * Returns the menu manager associated with this plugin instance.
     *
     * @return active menu manager
     */
    public MenuManager getMenuManager() {
        return eventsRegistry != null ? eventsRegistry.getMenuManager() : null;
    }

    /**
     * Returns the menu system associated with this plugin instance.
     *
     * @return active menu system
     */
    public MenuSystem getMenuSystem() {
        return eventsRegistry != null ? eventsRegistry.getMenuSystem() : null;
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
        return instance != null ? instance.getMenuManager() : null;
    }

    /**
     * Returns the action bar service of the active plugin instance.
     *
     * @return active action bar service, or {@code null} if not initialized
     */
    public static ActionbarService actionBarService() {
        return instance != null ? instance.getActionBarService() : null;
    }

    private boolean initializeDatabase() {
        try {
            database = new PluginDatabase(getDataFolder().getAbsolutePath() + "/" + PluginFiles.DATABASE.getFileName());
            Log.success("Database", "Successfully connected to the database.");
            return true;
        } catch (SQLException e) {
            Log.debug(e.getMessage());
            Log.error("Database", "Unable to connect to the database.");
            Bukkit.getPluginManager().disablePlugin(this);
            return false;
        }
    }
}
