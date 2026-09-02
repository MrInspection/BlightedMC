package fr.moussax.blightedSMP.engine.entities.spawnable.engine;

import fr.moussax.blightedSMP.BlightedSMP;
import fr.moussax.blightedSMP.engine.entities.registry.SpawnableEntitiesRegistry;
import fr.moussax.blightedSMP.engine.entities.spawnable.SpawnableEntity;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Periodic engine responsible for spawning custom entities outside of vanilla spawning mechanics.
 *
 * <p>Selects loaded chunks around online players and attempts to spawn registered
 * {@link SpawnableEntity} instances with {@link SpawnMode#INDEPENDENT} or {@link SpawnMode#HYBRID}
 * modes based on their configured spawn rules.</p>
 */
public final class BlightedSpawnEngine extends BukkitRunnable {

    private static final int CHUNKS_PER_PLAYER_PER_TICK = 1;
    private static final int MIN_CHUNK_DISTANCE = 3; // ~48 blocks away minimum
    private static final int MAX_CHUNK_DISTANCE = 8; // ~128 blocks away maximum
    private static final int CACHE_REFRESH_INTERVAL = 10; // 10 cycles (10s at 20L period)

    private static final int MAX_MONSTERS_PER_PLAYER = 70;
    private static final int MAX_MONSTERS_PER_CHUNK = 5;

    private final List<SpawnableEntity> cachedIndependentEntities = new ArrayList<>();
    private final List<Player> cachedPlayers = new ArrayList<>();
    private int tickCounter = CACHE_REFRESH_INTERVAL;

    /**
     * Executes a periodic spawn cycle around online players, evaluating loaded chunk candidates.
     */
    @Override
    public void run() {
        if (tickCounter++ % CACHE_REFRESH_INTERVAL == 0) {
            refreshCaches();
        }

        if (cachedIndependentEntities.isEmpty() || cachedPlayers.isEmpty()) return;

        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (Player player : cachedPlayers) {
            if (!player.isOnline()) continue;
            int nearbyMonsters = 0;
            for (Entity entity : player.getNearbyEntities(128.0, 128.0, 128.0)) {
                if (entity instanceof Monster) {
                    nearbyMonsters++;
                }
            }

            if (nearbyMonsters >= MAX_MONSTERS_PER_PLAYER) continue;

            World world = player.getWorld();
            Chunk playerChunk = player.getLocation().getChunk();

            for (int i = 0; i < CHUNKS_PER_PLAYER_PER_TICK; i++) {
                int distance = random.nextInt(MIN_CHUNK_DISTANCE, MAX_CHUNK_DISTANCE + 1);
                double angle = random.nextDouble() * 2 * Math.PI;

                int targetChunkX = playerChunk.getX() + (int) Math.round(distance * Math.cos(angle));
                int targetChunkZ = playerChunk.getZ() + (int) Math.round(distance * Math.sin(angle));

                if (!world.isChunkLoaded(targetChunkX, targetChunkZ)) continue;
                attemptSpawnInChunk(world.getChunkAt(targetChunkX, targetChunkZ), world, random);
            }
        }
    }

    private void refreshCaches() {
        cachedIndependentEntities.clear();
        for (SpawnableEntity entity : SpawnableEntitiesRegistry.getAll()) {
            SpawnMode mode = entity.getSpawnMode();
            if (mode == SpawnMode.INDEPENDENT || mode == SpawnMode.HYBRID) {
                cachedIndependentEntities.add(entity);
            }
        }

        cachedPlayers.clear();
        cachedPlayers.addAll(BlightedSMP.getInstance().getServer().getOnlinePlayers());
    }

    private void attemptSpawnInChunk(Chunk chunk, World world, ThreadLocalRandom random) {
        int monsterCount = 0;
        for (Entity entity : chunk.getEntities()) {
            if (entity instanceof Monster) {
                monsterCount++;
            }
        }
        if (monsterCount >= MAX_MONSTERS_PER_CHUNK) return;

        int x = (chunk.getX() << 4) + random.nextInt(16);
        int z = (chunk.getZ() << 4) + random.nextInt(16);

        int startY;
        int endY;

        if (world.getEnvironment() == World.Environment.NORMAL) {
            startY = world.getHighestBlockYAt(x, z) + 1;
            endY = Math.max(world.getMinHeight() + 1, startY - 64);
        } else {
            startY = Math.min(120, world.getMaxHeight() - 1);
            endY = world.getMinHeight() + 1;
        }

        for (int y = startY; y >= endY; y--) {
            Block block = world.getBlockAt(x, y, z);
            if (!block.getType().isAir()) continue;

            Block below = block.getRelative(0, -1, 0);
            if (!below.getType().isSolid()) continue;

            Block above = block.getRelative(0, 1, 0);
            if (!above.getType().isAir()) continue;

            Location location = new Location(world, x + 0.5, y, z + 0.5);

            List<SpawnableEntity> eligible = null;
            for (SpawnableEntity entity : cachedIndependentEntities) {
                if (!entity.canSpawnAt(location, world)) continue;
                if (eligible == null) eligible = new ArrayList<>(cachedIndependentEntities.size());
                eligible.add(entity);
            }

            if (eligible == null) continue;

            double totalChance = 0.0;
            for (SpawnableEntity entity : eligible) {
                totalChance += entity.getSpawnProbability();
            }

            if (random.nextDouble() >= Math.min(totalChance, 1.0)) continue;

            double selectionRoll = random.nextDouble() * totalChance;
            double cumulative = 0.0;
            for (SpawnableEntity entity : eligible) {
                cumulative += entity.getSpawnProbability();
                if (selectionRoll < cumulative) {
                    entity.clone().spawn(location);
                    return; // Stop scanning column once an entity spawns
                }
            }
        }
    }
}
