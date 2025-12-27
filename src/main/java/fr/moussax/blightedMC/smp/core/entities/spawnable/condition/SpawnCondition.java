package fr.moussax.blightedMC.smp.core.entities.spawnable.condition;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * Predicate defining whether an entity may spawn at a given {@link Location}
 * in a {@link World}.
 * <p>
 * Spawn conditions are composable via logical operations:
 * {@link #and(SpawnCondition)}, {@link #or(SpawnCondition)}, and {@link #not()}.
 *
 * <p>Example:
 * <pre>{@code
 * SpawnCondition nearSurface = (loc, world) -> loc.getBlockY() > 60;
 * SpawnCondition notNether = (loc, world) ->
 *     world.getEnvironment() != World.Environment.NETHER;
 *
 * SpawnCondition validSpawn = nearSurface.and(notNether);
 * }</pre>
 */
@FunctionalInterface
public interface SpawnCondition {

    /**
     * Evaluates whether spawning is allowed at the given location.
     *
     * @param location target spawn location
     * @param world    target world
     * @return {@code true} if spawning is permitted
     */
    boolean testCanSpawnAt(Location location, World world);

    /**
     * Returns a condition that passes only if both conditions pass.
     */
    default SpawnCondition and(SpawnCondition other) {
        return (location, world) -> this.testCanSpawnAt(location, world) && other.testCanSpawnAt(location, world);
    }

    /**
     * Returns a condition that passes if either condition passes.
     */
    default SpawnCondition or(SpawnCondition other) {
        return (location, world) -> this.testCanSpawnAt(location, world) || other.testCanSpawnAt(location, world);
    }

    /**
     * Returns the logical negation of this condition.
     */
    default SpawnCondition not() {
        return (location, world) -> !this.testCanSpawnAt(location, world);
    }
}
