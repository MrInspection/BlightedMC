package fr.moussax.blightedMC.server;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.utils.debug.Log;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

public final class PluginSettings {

    @Getter
    private double defaultManaRegenerationRate;
    @Getter
    private double customLootChance;

    public static PluginSettings load(BlightedMC plugin) {
        PluginSettings settings = new PluginSettings();
        try {
            plugin.reloadConfig();
            FileConfiguration config = plugin.getConfig();

            settings.customLootChance = config.getDouble("custom_loot_chance", 0.50);
            settings.defaultManaRegenerationRate = config.getDouble("default_mana_regeneration_rate", 2.0);
            
            Log.success("Config", "Successfully loaded the configuration file.");
        } catch (Exception e) {
            Log.error("Config", "Failed to load configuration: " + e.getMessage());
        }
        return settings;
    }
}
