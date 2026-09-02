package fr.moussax.blightedSMP.server;

import fr.moussax.blightedSMP.engine.entities.listeners.BlightedEntitiesListener;
import fr.moussax.bedrock.utils.debug.Log;
import org.bukkit.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Provides static server configuration and startup chunk rehydration routines.
 */
public final class BlightedServer {

    private BlightedServer() {
    }

    /**
     * Applies default server gamerules and difficulty settings.
     */
    public static void configureServer() {
        for (World world : Bukkit.getWorlds()) {
            world.setDifficulty(Difficulty.HARD);
            world.setGameRule(GameRule.FIRE_SPREAD_RADIUS_AROUND_PLAYER, 0);
        }
    }

    /**
     * Rehydrates loaded entities across all loaded chunks asynchronously in batches.
     *
     * @param plugin plugin instance handling task scheduling
     */
    public static void rehydrateEntitiesOnLoadedChunks(JavaPlugin plugin) {
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
                    Log.info("Entity System", "Rehydration complete: " + processedCount + " chunks processed in " + duration + "ms.");
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }
}
