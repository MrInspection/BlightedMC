package fr.moussax.blightedSMP.engine.entities.spawnable.condition;

import org.bukkit.Material;
import org.bukkit.Raid;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.TrialSpawner;
import org.bukkit.block.data.type.TrialSpawner.State;
import org.bukkit.generator.structure.GeneratedStructure;
import org.bukkit.generator.structure.Structure;
import org.bukkit.generator.structure.StructurePiece;

import java.util.Collection;
import java.util.Set;

/**
 * Static factory utility providing standard {@link SpawnCondition} predicates.
 *
 * <p>All returned conditions are stateless lambdas that can be composed via
 * {@link SpawnCondition#and(SpawnCondition)}, {@link SpawnCondition#or(SpawnCondition)},
 * and {@link SpawnCondition#negate()}.</p>
 */
public final class SpawnRules {

    private SpawnRules() {
    }

    /**
     * Creates a condition permitting spawning only within the specified biomes.
     *
     * @param allowed biomes permitted for spawning
     * @return biome spawn condition predicate
     */
    public static SpawnCondition biome(Biome... allowed) {
        Set<Biome> biomeSet = Set.of(allowed);
        return (location, world) -> biomeSet.contains(location.getBlock().getBiome());
    }

    /**
     * Creates a condition permitting spawning only within the specified world environment.
     *
     * @param environment target world environment
     * @return environment spawn condition predicate
     */
    public static SpawnCondition environment(World.Environment environment) {
        return (location, world) -> world.getEnvironment() == environment;
    }

    /**
     * Creates a condition permitting spawning at or above a minimum Y coordinate.
     *
     * @param minY minimum Y coordinate
     * @return minimum height spawn condition predicate
     */
    public static SpawnCondition atLeastY(int minY) {
        return (location, world) -> location.getBlockY() >= minY;
    }

    /**
     * Creates a condition permitting spawning at or below a maximum Y coordinate.
     *
     * @param maxY maximum Y coordinate
     * @return maximum height spawn condition predicate
     */
    public static SpawnCondition atMostY(int maxY) {
        return (location, world) -> location.getBlockY() <= maxY;
    }

    /**
     * Creates a condition permitting spawning when block light is at or below the specified maximum.
     *
     * @param max maximum block light level
     * @return block light spawn condition predicate
     */
    public static SpawnCondition maxBlockLight(int max) {
        return (location, world) -> location.getBlock().getLightFromBlocks() <= max;
    }

    /**
     * Creates a condition permitting spawning when combined light is at or below the specified maximum.
     *
     * @param max maximum total light level
     * @return light level spawn condition predicate
     */
    public static SpawnCondition maxLightLevel(int max) {
        return (location, world) -> location.getBlock().getLightLevel() <= max;
    }

    /**
     * Creates a condition permitting spawning only when the location is exposed to the sky.
     *
     * @return sky exposure spawn condition predicate
     */
    public static SpawnCondition skyExposed() {
        return (location, world) -> world.getHighestBlockYAt(location) <= location.getBlockY();
    }

    /**
     * Creates a condition permitting spawning only outside liquid blocks (water or lava).
     *
     * @return non-liquid spawn condition predicate
     */
    public static SpawnCondition notInLiquid() {
        return (location, world) -> !location.getBlock().isLiquid();
    }

    /**
     * Creates a condition permitting spawning during nighttime hours (ticks 13000 to 23000).
     *
     * @return nighttime spawn condition predicate
     */
    public static SpawnCondition nightTime() {
        return (location, world) -> {
            long time = world.getTime();
            return time >= 13000 && time <= 23000;
        };
    }

    /**
     * Creates a condition permitting spawning only during clear weather.
     *
     * @return clear sky spawn condition predicate
     */
    public static SpawnCondition clearSky() {
        return (location, world) -> !world.hasStorm();
    }

    /**
     * Creates a condition permitting spawning during rain or stormy weather.
     *
     * @return rain spawn condition predicate
     */
    public static SpawnCondition isRaining() {
        return (location, world) -> world.hasStorm();
    }

    /**
     * Creates a condition permitting spawning during thunder storms.
     *
     * @return thunder spawn condition predicate
     */
    public static SpawnCondition isThundering() {
        return (location, world) -> world.isThundering();
    }

    /**
     * Creates a condition permitting spawning inside the bounding box of a world structure.
     *
     * @param structure world structure type
     * @return structure spawn condition predicate
     */
    public static SpawnCondition insideStructure(Structure structure) {
        return (location, world) -> {
            int chunkX = location.getBlockX() >> 4;
            int chunkZ = location.getBlockZ() >> 4;

            Collection<GeneratedStructure> structures = world.getStructures(chunkX, chunkZ, structure);

            for (GeneratedStructure generatedStructure : structures) {
                for (StructurePiece piece : generatedStructure.getPieces()) {
                    if (piece.getBoundingBox().contains(location.getX(), location.getY(), location.getZ())) {
                        return true;
                    }
                }
            }
            return false;
        };
    }

    /**
     * Creates a condition permitting spawning inside Trial Chambers structures.
     *
     * @return trial chamber structure spawn condition predicate
     */
    public static SpawnCondition insideTrialChamber() {
        return insideStructure(Structure.TRIAL_CHAMBERS);
    }

    /**
     * Creates a condition permitting spawning near an active raid within a specified block radius.
     *
     * @param radius search radius in blocks
     * @return active raid spawn condition predicate
     */
    public static SpawnCondition nearActiveRaid(int radius) {
        return (location, world) -> {
            Raid nearest = world.locateNearestRaid(location, radius);
            return nearest != null && nearest.isStarted();
        };
    }

    /**
     * Creates a condition permitting spawning only when no active raid is within the specified radius.
     *
     * @param radius search radius in blocks
     * @return no nearby raid spawn condition predicate
     */
    public static SpawnCondition noNearbyRaid(int radius) {
        return nearActiveRaid(radius).negate();
    }

    /**
     * Creates a condition permitting spawning near an active Trial Spawner within a specified block radius.
     *
     * @param radius search radius in blocks
     * @return active trial spawner spawn condition predicate
     */
    public static SpawnCondition nearActiveTrialSpawner(int radius) {
        return (location, world) -> {
            int originX = location.getBlockX();
            int originY = location.getBlockY();
            int originZ = location.getBlockZ();

            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -radius; dy <= radius; dy++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        Block block = world.getBlockAt(originX + dx, originY + dy, originZ + dz);
                        if (block.getType() != Material.TRIAL_SPAWNER) continue;

                        if (block.getBlockData() instanceof TrialSpawner trialData
                            && trialData.getTrialSpawnerState() == State.ACTIVE) {
                            return true;
                        }
                    }
                }
            }
            return false;
        };
    }

    /**
     * Creates a condition permitting spawning near an active Ominous Trial Spawner within a specified block radius.
     *
     * @param radius search radius in blocks
     * @return active ominous trial spawner spawn condition predicate
     */
    public static SpawnCondition nearOminousTrialSpawner(int radius) {
        return (location, world) -> {
            int originX = location.getBlockX();
            int originY = location.getBlockY();
            int originZ = location.getBlockZ();

            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -radius; dy <= radius; dy++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        Block block = world.getBlockAt(originX + dx, originY + dy, originZ + dz);
                        if (block.getType() != Material.TRIAL_SPAWNER) continue;

                        if (block.getBlockData() instanceof TrialSpawner trialData
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

    /**
     * Composite condition for standard Overworld hostile monster spawning (darkness, non-liquid).
     *
     * @return Overworld hostile spawn condition predicate
     */
    public static SpawnCondition overworldHostile() {
        return maxBlockLight(0).and(maxLightLevel(7)).and(notInLiquid());
    }

    /**
     * Composite condition for surface-only Overworld hostile monster spawning (sky exposed, darkness, non-liquid).
     *
     * @return Overworld surface hostile spawn condition predicate
     */
    public static SpawnCondition overworldSurfaceHostile() {
        return maxBlockLight(0).and(maxLightLevel(7)).and(skyExposed()).and(notInLiquid());
    }

    /**
     * Composite condition for Nether hostile monster spawning (non-liquid, light level <= 11).
     *
     * @return Nether hostile spawn condition predicate
     */
    public static SpawnCondition netherHostile() {
        return maxBlockLight(11).and(notInLiquid());
    }
}
