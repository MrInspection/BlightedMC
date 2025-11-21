package fr.moussax.blightedMC.core.entities.spawnable;

import org.bukkit.block.Biome;

import java.util.Set;

/**
 * Utility class providing common predefined {@link SpawnCondition} factories
 * for entity spawning logic.
 * <p>
 * Each method returns a reusable lambda-based {@link SpawnCondition} that can be
 * combined using {@link SpawnCondition#and(SpawnCondition)},
 * {@link SpawnCondition#or(SpawnCondition)}, and {@link SpawnCondition#not()}.
 * <p>
 * Example usage:
 * <pre>{@code
 * SpawnCondition plainsNightSpawn = SpawnConditions.biome(Biome.PLAINS)
 *     .and(SpawnConditions.nightTime())
 *     .and(SpawnConditions.clearWeather());
 * }</pre>
 */
public final class SpawnConditions {
    private SpawnConditions() {
    }

    /**
     * Creates a condition allowing spawning only in the specified biomes.
     *
     * @param allowed one or more allowed {@link Biome}s
     * @return a condition that passes only if the location's biome is in the allowed set
     */
    public static SpawnCondition biome(Biome... allowed) {
        Set<Biome> biomeSet = Set.of(allowed);
        return (loc, world) -> biomeSet.contains(loc.getBlock().getBiome());
    }

    /**
     * Creates a condition requiring the spawn location's Y coordinate
     * to be at or above a specified minimum.
     *
     * @param minY the minimum Y level allowed
     * @return a condition that passes only if {@code Y >= minY}
     */
    public static SpawnCondition minY(int minY) {
        return (loc, world) -> loc.getBlockY() >= minY;
    }

    /**
     * Creates a condition requiring the spawn location's Y coordinate
     * to be at or below a specified maximum.
     *
     * @param maxY the maximum Y level allowed
     * @return a condition that passes only if {@code Y <= maxY}
     */
    public static SpawnCondition maxY(int maxY) {
        return (loc, world) -> loc.getBlockY() <= maxY;
    }

    /**
     * Creates a condition allowing spawning only if the location
     * is directly exposed to the sky (no blocks above).
     *
     * @return a condition that passes if the location is under open sky
     */
    public static SpawnCondition skyExposed() {
        return (loc, world) -> world.getHighestBlockYAt(loc) <= loc.getBlockY();
    }

    /**
     * Creates a condition disallowing spawning in water or other liquid blocks.
     *
     * @return a condition that passes only if the block is not liquid
     */
    public static SpawnCondition notInWater() {
        return (loc, world) -> !loc.getBlock().isLiquid();
    }

    /**
     * Creates a condition allowing spawning only during nighttime.
     * <p>Nighttime is defined as in-game time between {@code 13000} and {@code 23000} ticks.
     *
     * @return a condition that passes only at night
     */
    public static SpawnCondition nightTime() {
        return (loc, world) -> {
            long time = world.getTime();
            return time >= 13000 && time <= 23000;
        };
    }

    /**
     * Creates a condition allowing spawning only when the weather is clear
     * (i.e., not raining or storming).
     *
     * @return a condition that passes only if the world has no active storm
     */
    public static SpawnCondition clearWeather() {
        return (loc, world) -> !world.hasStorm();
    }
}
