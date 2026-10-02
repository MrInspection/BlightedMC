package fr.moussax.blightedSMP.engine.entities.spawnable.condition;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.generator.structure.GeneratedStructure;
import org.bukkit.generator.structure.Structure;
import org.bukkit.generator.structure.StructurePiece;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Objects;
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
    public static SpawnCondition biome(@NonNull Biome... allowed) {
        Set<Biome> biomeSet = Set.of(allowed);
        return (location, _) -> biomeSet.contains(location.getBlock().getBiome());
    }

    /**
     * Creates a condition permitting spawning only within the specified world environment.
     *
     * @param environment target world environment
     * @return environment spawn condition predicate
     */
    public static SpawnCondition environment(@NonNull Environment environment) {
        Objects.requireNonNull(environment, "environment cannot be null");
        return (_, world) -> world.getEnvironment() == environment;
    }

    /**
     * Creates a condition permitting spawning only in the Overworld environment.
     *
     * @return Overworld environment spawn condition predicate
     */
    public static SpawnCondition overworld() {
        return environment(World.Environment.NORMAL);
    }

    /**
     * Creates a condition permitting spawning only in the Nether environment.
     *
     * @return Nether environment spawn condition predicate
     */
    public static SpawnCondition nether() {
        return environment(World.Environment.NETHER);
    }

    /**
     * Creates a condition permitting spawning only in The End environment.
     *
     * @return The End environment spawn condition predicate
     */
    public static SpawnCondition theEnd() {
        return environment(World.Environment.THE_END);
    }

    /**
     * Creates a condition permitting spawning when block light is at or below the specified maximum.
     *
     * @param max maximum block light level
     * @return block light spawn condition predicate
     */
    public static SpawnCondition maxBlockLight(int max) {
        return (location, _) -> location.getBlock().getLightFromBlocks() <= max;
    }

    /**
     * Creates a condition permitting spawning when combined light is at or below the specified maximum.
     *
     * @param max maximum total light level
     * @return light level spawn condition predicate
     */
    public static SpawnCondition maxLightLevel(int max) {
        return (location, _) -> location.getBlock().getLightLevel() <= max;
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
     * Creates a condition permitting spawning inside liquid blocks (water or lava).
     *
     * @return liquid spawn condition predicate
     */
    public static SpawnCondition inLiquid() {
        return (location, _) -> location.getBlock().isLiquid();
    }

    /**
     * Creates a condition permitting spawning only outside liquid blocks (water or lava).
     *
     * @return non-liquid spawn condition predicate
     */
    public static SpawnCondition notInLiquid() {
        return inLiquid().negate();
    }

    /**
     * Creates a condition permitting spawning inside the bounding box of a world structure.
     *
     * @param structure world structure type
     * @return structure spawn condition predicate
     */
    public static SpawnCondition insideStructure(@NonNull Structure structure) {
        Objects.requireNonNull(structure, "structure cannot be null");
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
