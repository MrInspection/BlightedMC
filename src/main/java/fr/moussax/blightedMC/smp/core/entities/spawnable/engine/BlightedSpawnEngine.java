package fr.moussax.blightedMC.smp.core.entities.spawnable.engine;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.smp.core.entities.registry.SpawnableEntitiesRegistry;
import fr.moussax.blightedMC.smp.core.entities.spawnable.SpawnableEntity;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
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
 *
 * <p>Y selection uses the highest block at the target column to avoid wasteful
 * checks deep inside solid terrain.</p>
 */
public final class BlightedSpawnEngine extends BukkitRunnable {

    private static final int CHUNKS_PER_PLAYER_PER_TICK = 1;
    private static final int MIN_CHUNK_DISTANCE = 3;
    private static final int MAX_CHUNK_DISTANCE = 8;
    private static final int CACHE_REFRESH_INTERVAL = 200;

    private final List<SpawnableEntity> cachedIndependentEntities = new ArrayList<>();
    private final List<Player> cachedPlayers = new ArrayList<>();
    private int tickCounter = 0;

    @Override
    public void run() {
        if (tickCounter++ % CACHE_REFRESH_INTERVAL == 0) {
            refreshCaches();
        }

        if (cachedIndependentEntities.isEmpty() || cachedPlayers.isEmpty()) return;

        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (Player player : cachedPlayers) {
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
        cachedPlayers.addAll(BlightedMC.getInstance().getServer().getOnlinePlayers());
    }

    private void attemptSpawnInChunk(Chunk chunk, World world, ThreadLocalRandom random) {
        int x = (chunk.getX() << 4) + random.nextInt(16);
        int z = (chunk.getZ() << 4) + random.nextInt(16);

        int surfaceY = world.getHighestBlockYAt(x, z);
        int y = surfaceY + 1;

        Location location = new Location(world, x + 0.5, y, z + 0.5);

        Block block = location.getBlock();
        if (!block.getType().isAir()) return;

        Block below = block.getRelative(0, -1, 0);
        if (!below.getType().isSolid()) return;

        Block above = block.getRelative(0, 1, 0);
        if (!above.getType().isAir()) return;

        for (SpawnableEntity entity : cachedIndependentEntities) {
            if (!entity.canSpawnAt(location, world)) continue;

            if (random.nextDouble() <= entity.getSpawnProbability()) {
                entity.clone().spawn(location);
                return;
            }
        }
    }
}
