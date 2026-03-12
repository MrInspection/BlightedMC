package fr.moussax.blightedMC.server;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.utils.config.FlexiblePropertyUtils;
import fr.moussax.blightedMC.utils.debug.Log;
import lombok.Getter;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import org.yaml.snakeyaml.introspector.BeanAccess;

import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public final class PluginSettings {

    @Getter
    private double defaultManaRegenerationRate;
    @Getter
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

    public boolean hasBannersOnJoin() {
        return bannersOnJoin;
    }
}
