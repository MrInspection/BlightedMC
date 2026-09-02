package fr.moussax.blightedSMP.server;

import fr.moussax.blightedSMP.BlightedSMP;
import fr.moussax.bedrock.utils.debug.Log;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Loads and holds runtime configuration settings from {@code config.yml}.
 */
public final class PluginSettings {

    /**
     * Default passive mana regeneration rate per tick.
     */
    @Getter
    private double defaultManaRegenerationRate;

    /**
     * Base probability for custom loot drops.
     */
    @Getter
    private double customLootChance;

    private PluginSettings() {
    }

    /**
     * Loads plugin configuration settings from the specified plugin instance.
     *
     * @param plugin plugin instance providing configuration access
     * @return loaded settings instance with defensive defaults
     */
    public static PluginSettings load(BlightedSMP plugin) {
        PluginSettings settings = new PluginSettings();
        try {
            plugin.reloadConfig();
            FileConfiguration config = plugin.getConfig();

            settings.customLootChance = config.getDouble("custom_loot_chance", 0.50);
            settings.defaultManaRegenerationRate = config.getDouble("default_mana_regeneration_rate", 2.0);
            
            Log.success("Config", "Successfully loaded the configuration file.");
        } catch (Exception exception) {
            Log.error("Config", "Failed to load configuration: " + exception.getMessage());
        }
        return settings;
    }
}
