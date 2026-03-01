package fr.moussax.blightedMC.smp.core.entities.spawnable.condition;

import org.bukkit.Material;
import org.bukkit.Raid;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.TrialSpawner.State;
import org.bukkit.generator.structure.GeneratedStructure;
import org.bukkit.generator.structure.Structure;
import org.bukkit.generator.structure.StructurePiece;

import java.util.Collection;
import java.util.Set;

/**
 * Static utility for common {@link SpawnCondition} predicates.
 *
 * <p>All returned conditions are stateless lambdas and can be composed via
 * {@link SpawnCondition#and}, {@link SpawnCondition#or}, and {@link SpawnCondition#negate}.
 *
 * <pre>{@code
 * SpawnCondition condition = SpawnRules.biome(Biome.PLAINS)
 *     .and(SpawnRules.nightTime())
 *     .and(SpawnRules.skyExposed());
 * }</pre>
 */
public final class SpawnRules {

    private SpawnRules() {}

    /**
     * Restricts spawning to the given biomes.
     */
    public static SpawnCondition biome(org.bukkit.block.Biome... allowed) {
        Set<org.bukkit.block.Biome> biomeSet = Set.of(allowed);
        return (loc, world) -> biomeSet.contains(loc.getBlock().getBiome());
    }

    /**
     * Restricts spawning to the given world environment (e.g. NETHER, THE_END, NORMAL).
     */
    public static SpawnCondition environment(World.Environment environment) {
        return (loc, world) -> world.getEnvironment() == environment;
    }

    /** Requires the spawn Y level to be at least {@code minY}. */
    public static SpawnCondition atLeastY(int minY) {
        return (loc, world) -> loc.getBlockY() >= minY;
    }

    /** Requires the spawn Y level to be at most {@code maxY}. */
    public static SpawnCondition atMostY(int maxY) {
        return (loc, world) -> loc.getBlockY() <= maxY;
    }

    /** Requires block light (from torches, lava, etc.) to be at most {@code max}. */
    public static SpawnCondition maxBlockLight(int max) {
        return (loc, world) -> loc.getBlock().getLightFromBlocks() <= max;
    }

    /** Requires total light level (block + sky) to be at most {@code max}. */
    public static SpawnCondition maxLightLevel(int max) {
        return (loc, world) -> loc.getBlock().getLightLevel() <= max;
    }

    /** Requires the spawn location to be directly exposed to the sky. */
    public static SpawnCondition skyExposed() {
        return (loc, world) -> world.getHighestBlockYAt(loc) <= loc.getBlockY();
    }

    /** Disallows spawning in liquid blocks. */
    public static SpawnCondition notInLiquid() {
        return (loc, world) -> !loc.getBlock().isLiquid();
    }

    /** Allows spawning only at night (world time between 13000 and 23000). */
    public static SpawnCondition nightTime() {
        return (loc, world) -> {
            long time = world.getTime();
            return time >= 13000 && time <= 23000;
        };
    }

    /** Allows spawning only when no storm is active. */
    public static SpawnCondition clearSky() {
        return (loc, world) -> !world.hasStorm();
    }

    /** Allows spawning only during a storm. */
    public static SpawnCondition isRaining() {
        return (loc, world) -> world.hasStorm();
    }

    /** Allows spawning only during a thunderstorm. */
    public static SpawnCondition isThundering() {
        return (loc, world) -> world.isThundering();
    }

    /**
     * Allows spawning only inside the bounding box of the given structure.
     *
     * @param structure target structure type
     * @return condition matching locations within structure pieces
     */
    public static SpawnCondition insideStructure(Structure structure) {
        return (loc, world) -> {
            int chunkX = loc.getBlockX() >> 4;
            int chunkZ = loc.getBlockZ() >> 4;

            Collection<GeneratedStructure> structures = world.getStructures(chunkX, chunkZ, structure);

            for (GeneratedStructure generatedStructure : structures) {
                for (StructurePiece piece : generatedStructure.getPieces()) {
                    if (piece.getBoundingBox().contains(loc.getX(), loc.getY(), loc.getZ())) {
                        return true;
                    }
                }
            }
            return false;
        };
    }

    public static SpawnCondition insideTrialChamber() {
        return insideStructure(Structure.TRIAL_CHAMBERS);
    }

    public static SpawnCondition nearActiveRaid(int radius) {
        return (location, world) -> {
            Raid nearest = world.locateNearestRaid(location, radius);
            return nearest != null && nearest.isStarted();
        };
    }

    public static SpawnCondition noNearbyRaid(int radius) {
        return nearActiveRaid(radius).negate();
    }

    public static SpawnCondition nearActiveTrialSpawner(int radius) {
        return (loc, world) -> {
            int originX = loc.getBlockX();
            int originY = loc.getBlockY();
            int originZ = loc.getBlockZ();

            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -radius; dy <= radius; dy++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        Block block = world.getBlockAt(originX + dx, originY + dy, originZ + dz);
                        if (block.getType() != Material.TRIAL_SPAWNER) continue;

                        if (block.getBlockData() instanceof org.bukkit.block.data.type.TrialSpawner trialData
                            && trialData.getTrialSpawnerState() == State.ACTIVE) {
                            return true;
                        }
                    }
                }
            }
            return false;
        };
    }

    public static SpawnCondition nearOminousTrialSpawner(int radius) {
        return (loc, world) -> {
            int originX = loc.getBlockX();
            int originY = loc.getBlockY();
            int originZ = loc.getBlockZ();

            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -radius; dy <= radius; dy++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        Block block = world.getBlockAt(originX + dx, originY + dy, originZ + dz);
                        if (block.getType() != Material.TRIAL_SPAWNER) continue;

                        if (block.getBlockData() instanceof org.bukkit.block.data.type.TrialSpawner trialData
                            && trialData.isOminous()
                            && trialData.getTrialSpawnerState() == State.ACTIVE) {
                            return true;
                        }
                    }
                }
            }
            return false;
        };
    }
}
