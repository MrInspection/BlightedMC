package fr.moussax.blightedMC.server;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.utils.config.FlexiblePropertyUtils;
import fr.moussax.blightedMC.utils.debug.Log;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import org.yaml.snakeyaml.introspector.BeanAccess;

import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class PluginSettings {
    private double defaultMaxMana;
    private double defaultManaRegenerationRate;
    private double defaultPlayerHealth;
    private double customLootChance;
    private boolean bannersOnJoin;

    public static PluginSettings load(BlightedMC plugin) {
        try (Reader reader = Files.newBufferedReader(PluginFiles.CONFIG.getFile().toPath(), StandardCharsets.UTF_8)) {
            CustomClassLoaderConstructor constructor = new CustomClassLoaderConstructor(
                plugin.getClass().getClassLoader(),
                new LoaderOptions()
            );
            constructor.setPropertyUtils(new FlexiblePropertyUtils());

            Yaml yaml = new Yaml(constructor);
            yaml.setBeanAccess(BeanAccess.FIELD);

            PluginSettings settings = yaml.loadAs(reader, PluginSettings.class);
            Log.success("Config", "Successfully loaded the configuration file.");

            return settings;
        } catch (Exception e) {
            Log.error("Config", "Failed to load configuration: " + e.getMessage());
            return new PluginSettings();
        }
    }

    public double getDefaultMaxMana() {
        return defaultMaxMana;
    }

    public double getDefaultManaRegenerationRate() {
        return defaultManaRegenerationRate;
    }

    public double getDefaultPlayerHealth() {
        return defaultPlayerHealth;
    }

    public double getCustomLootChance() {
        return customLootChance;
    }

    public boolean hasBannersOnJoin() {
        return bannersOnJoin;
    }
}
