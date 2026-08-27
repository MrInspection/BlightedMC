package fr.moussax.blightedMC.engine.entities.spawnable.condition;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * Functional predicate defining whether a custom entity may spawn at a given {@link Location} in a {@link World}.
 *
 * <p>Conditions are composable via {@link #and(SpawnCondition)}, {@link #or(SpawnCondition)},
 * and {@link #negate()}.</p>
 */
@FunctionalInterface
public interface SpawnCondition {

    /**
     * Evaluates whether spawning is allowed at the target location and world.
     *
     * @param location target spawn location
     * @param world    target spawn world
     * @return {@code true} if spawning is permitted, {@code false} otherwise
     */
    boolean testCanSpawnAt(Location location, World world);

    /**
     * Combines this condition with another using short-circuiting logical AND.
     *
     * @param other condition to evaluate if this condition succeeds
     * @return composed condition requiring both predicates to pass
     */
    default SpawnCondition and(SpawnCondition other) {
        return (location, world) -> this.testCanSpawnAt(location, world) && other.testCanSpawnAt(location, world);
    }

    /**
     * Combines this condition with another using short-circuiting logical OR.
     *
     * @param other fallback condition evaluated if this condition fails
     * @return composed condition requiring either predicate to pass
     */
    default SpawnCondition or(SpawnCondition other) {
        return (location, world) -> this.testCanSpawnAt(location, world) || other.testCanSpawnAt(location, world);
    }

    /**
     * Inverts the logical evaluation of this condition.
     *
     * @return negated spawn condition predicate
     */
    default SpawnCondition negate() {
        return (location, world) -> !this.testCanSpawnAt(location, world);
    }
}
