package fr.moussax.blightedMC;

import fr.moussax.blightedMC.core.entities.listeners.BlightedEntitiesListener;
import fr.moussax.blightedMC.server.PluginFiles;
import fr.moussax.blightedMC.server.PluginSettings;
import fr.moussax.blightedMC.server.database.PluginDatabase;
import fr.moussax.blightedMC.utils.commands.CommandBuilder;
import fr.moussax.blightedMC.utils.config.FlexiblePropertyUtils;
import fr.moussax.blightedMC.utils.debug.Log;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import org.yaml.snakeyaml.introspector.BeanAccess;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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

        try (final Reader reader = Files.newBufferedReader(PluginFiles.CONFIG.getFile().toPath(), StandardCharsets.UTF_8)) {
            final CustomClassLoaderConstructor constructor = new CustomClassLoaderConstructor(getClassLoader(), new LoaderOptions());
            constructor.setPropertyUtils(new FlexiblePropertyUtils());

            final Yaml yaml = new Yaml(constructor);
            yaml.setBeanAccess(BeanAccess.FIELD);

            settings = yaml.loadAs(reader, PluginSettings.class);
            Log.success("Config", "Successfully loaded the configuration file.");
        } catch (IOException e) {
            Log.error(e.getMessage());
        }

        try {
            database = new PluginDatabase(getDataFolder().getAbsolutePath() + "/" + PluginFiles.DATABASE.getFileName());
            Log.success("Database", "Successfully connected to the database.");
        } catch (SQLException e) {
            Log.debug(e.getMessage());
            Log.error("Database", "Unable to connect to the database.");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        GlobalRegistry.initializeAllRegistries();
        eventsRegistry = new EventsRegistry();
        eventsRegistry.initializeListeners();

        CommandBuilder.initializeCommands();

        rehydrateEntitiesOnLoadedChunks();
    }

    @Override
    public void onDisable() {
        database.closeConnection();
        if (eventsRegistry != null && eventsRegistry.getBlockListener() != null) {
            eventsRegistry.getBlockListener().saveData();
        }
        GlobalRegistry.clearAllRegistries();
    }

    /**
     * Rehydrate any loaded entities in already-loaded chunks (e.g., after /reload) by
     * manually firing the chunk scan once on enabling. This complements the runtime
     */
    private void rehydrateEntitiesOnLoadedChunks() {
        instance.getServer().getWorlds().forEach(world -> {
            for (Chunk chunk : world.getLoadedChunks()) {
                BlightedEntitiesListener.rehydrateChunk(chunk);
            }
        });
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
