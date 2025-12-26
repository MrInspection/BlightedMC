package fr.moussax.blightedMC.server;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.entities.listeners.BlightedEntitiesListener;
import fr.moussax.blightedMC.utils.debug.Log;
import org.bukkit.*;

public final class BlightedServer {
    private static BlightedServer instance;
    private final BlightedMC plugin;

    private BlightedServer(BlightedMC plugin) {
        this.plugin = plugin;
    }

    public static void initialize(BlightedMC plugin) {
        if (instance != null) return;
        instance = new BlightedServer(plugin);
    }

    public void configureServer() {
        Log.debug("SERVER", "Configuring server...");
        for (World world : Bukkit.getWorlds()) {
            world.setDifficulty(Difficulty.HARD);
            world.setGameRule(GameRule.FIRE_SPREAD_RADIUS_AROUND_PLAYER, 0);
        }
    }

    /**
     * Rehydrate any loaded entities in already-loaded chunks (e.g., after /reload) by
     * manually firing the chunk scan once on enabling. This complements the runtime
     */
    public void rehydrateEntitiesOnLoadedChunks() {
        plugin.getServer().getWorlds().forEach(world -> {
            for (Chunk chunk : world.getLoadedChunks()) {
                BlightedEntitiesListener.rehydrateChunk(chunk);
            }
        });
    }

    public static BlightedServer getInstance() {
        if (instance == null) throw new IllegalStateException("BlightedServer has not been initialized yet.");
        return instance;
    }
}
