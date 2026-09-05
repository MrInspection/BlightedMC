package fr.moussax.blightedMod;

import fr.moussax.bedrock.scheduling.PluginContext;
import fr.moussax.bedrock.utils.debug.Log;
import fr.moussax.blightedMod.database.PluginDatabase;
import fr.moussax.blightedMod.moderator.ModerationManager;
import fr.moussax.blightedMod.moderator.punishments.PunishmentManager;
import fr.moussax.blightedMod.registry.CommandsRegistry;
import fr.moussax.blightedMod.registry.EventsRegistry;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class BlightedMod extends JavaPlugin {

    @Getter
    private static BlightedMod instance;

    @Getter
    private PluginDatabase pluginDatabase;
    @Getter
    private ModerationManager moderationManager;
    private EventsRegistry eventsRegistry;

    @Override
    public void onEnable() {
        instance = this;
        PluginContext.bind(this);

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        try {
            pluginDatabase = new PluginDatabase(getDataFolder().getAbsolutePath() + "/blighted_mod.db");
            Log.success("Database", "Successfully connected to BlightedMod database.");
        } catch (SQLException exception) {
            Log.error("Database", "Unable to connect to BlightedMod database: " + exception.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        PunishmentManager punishmentManager = new PunishmentManager(pluginDatabase.getConnection());
        moderationManager = ModerationManager.init(punishmentManager);

        CommandsRegistry.registerCommands(this);

        eventsRegistry = new EventsRegistry();
        eventsRegistry.initializeListeners();
    }

    @Override
    public void onDisable() {
        if (eventsRegistry != null) {
            eventsRegistry.shutdownMenus();
            eventsRegistry.cleanup();
        }

        if (pluginDatabase != null) {
            pluginDatabase.closeConnection();
        }
    }
}
