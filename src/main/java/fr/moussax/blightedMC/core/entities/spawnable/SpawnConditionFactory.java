package fr.moussax.blightedMC.core.entities.spawnable;

import org.bukkit.block.Biome;
import org.bukkit.generator.structure.GeneratedStructure;
import org.bukkit.generator.structure.Structure;
import org.bukkit.generator.structure.StructurePiece;

import java.util.Collection;
import java.util.Set;

/**
 * Factory utility for common {@link SpawnCondition} predicates.
 * <p>
 * Provides reusable, composable conditions for entity spawning logic.
 * Returned conditions are stateless lambdas and can be combined via
 * {@link SpawnCondition#and(SpawnCondition)},
 * {@link SpawnCondition#or(SpawnCondition)}, and
 * {@link SpawnCondition#not()}.
 *
 * <p>Example:
 * <pre>{@code
 * SpawnCondition condition = SpawnConditions.biome(Biome.PLAINS)
 *     .and(SpawnConditions.nightTime())
 *     .and(SpawnConditions.clearWeather());
 * }</pre>
 */
public final class SpawnConditionFactory {
    private SpawnConditionFactory() {
    }

    /**
     * Restricts spawning to the given biomes.
     *
     * @param allowed allowed {@link Biome}s
     * @return condition matching the location biome
     */
    public static SpawnCondition biome(Biome... allowed) {
        Set<Biome> biomeSet = Set.of(allowed);
        return (loc, world) -> biomeSet.contains(loc.getBlock().getBiome());
    }

    /**
     * Requires the spawn Y level to be at least {@code minY}.
     */
    public static SpawnCondition atLeastY(int minY) {
        return (loc, world) -> loc.getBlockY() >= minY;
    }

    /**
     * Requires the spawn Y level to be at most {@code maxY}.
     */
    public static SpawnCondition atMostY(int maxY) {
        return (loc, world) -> loc.getBlockY() <= maxY;
    }

    /**
     * Requires the spawn location to be directly exposed to the sky.
     */
    public static SpawnCondition skyExposed() {
        return (loc, world) -> world.getHighestBlockYAt(loc) <= loc.getBlockY();
    }

    /**
     * Disallows spawning in liquid blocks.
     */
    public static SpawnCondition notInWater() {
        return (loc, world) -> !loc.getBlock().isLiquid();
    }

    /**
     * Allows spawning only at night
     * (world time between {@code 13000} and {@code 23000}).
     */
    public static SpawnCondition nightTime() {
        return (loc, world) -> {
            long time = world.getTime();
            return time >= 13000 && time <= 23000;
        };
    }

    /**
     * Allows spawning only when no storm is active.
     */
    public static SpawnCondition isOpenSky() {
        return (loc, world) -> !world.hasStorm();
    }

    /**
     * Allows spawning only during a storm.
     */
    public static SpawnCondition isRaining() {
        return (loc, world) -> world.hasStorm();
    }

    /**
     * Allows spawning only during a thunderstorm.
     */
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
}
