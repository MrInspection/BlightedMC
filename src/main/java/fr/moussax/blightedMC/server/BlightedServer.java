package fr.moussax.blightedMC.server;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.smp.core.entities.listeners.BlightedEntitiesListener;
import fr.moussax.blightedMC.utils.debug.Log;
import org.bukkit.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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
        Log.info("BlightedServer", "Configuring server for a Blighted Survival...");
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
        List<Chunk> chunks = new ArrayList<>();
        for (World world : plugin.getServer().getWorlds()) {
            Collections.addAll(chunks, world.getLoadedChunks());
        }

        if (chunks.isEmpty()) return;

        Log.info("Entity System", "Starting rehydration for " + chunks.size() + " loaded chunks...");

        new BukkitRunnable() {
            final Iterator<Chunk> iterator = chunks.iterator();
            final int BATCH_SIZE = 100;
            int processedCount = 0;
            long startTime = System.currentTimeMillis();

            @Override
            public void run() {
                int currentBatch = 0;

                while (iterator.hasNext() && currentBatch < BATCH_SIZE) {
                    Chunk chunk = iterator.next();

                    if (chunk.isLoaded()) {
                        BlightedEntitiesListener.rehydrateChunk(chunk);
                    }

                    currentBatch++;
                    processedCount++;
                }

                if (!iterator.hasNext()) {
                    long duration = System.currentTimeMillis() - startTime;
                    Log.info("Entity System", "Rehydration complete: " + processedCount + " chunks processed in " + duration + "ms (Async-like feel).");
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }

    public static BlightedServer getInstance() {
        if (instance == null) throw new IllegalStateException("BlightedServer has not been initialized yet.");
        return instance;
    }
}
