package fr.moussax.blightedSMP.engine.entities.spawnable.condition;

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

    /**
     * Creates a condition requiring at least one of the provided conditions to pass (logical OR).
     *
     * <p>Short-circuits upon the first matching condition. If no conditions are supplied,
     * this condition always fails.</p>
     *
     * @param conditions conditions to evaluate
     * @return composite OR condition
     */
    static SpawnCondition anyOf(SpawnCondition... conditions) {
        if (conditions.length == 0) {
            return (_, _) -> false;
        }
        if (conditions.length == 1) {
            return java.util.Objects.requireNonNull(conditions[0], "condition cannot be null");
        }
        SpawnCondition[] copy = conditions.clone();
        for (SpawnCondition condition : copy) {
            java.util.Objects.requireNonNull(condition, "condition cannot be null");
        }
        return (location, world) -> {
            for (SpawnCondition condition : copy) {
                if (condition.testCanSpawnAt(location, world)) {
                    return true;
                }
            }
            return false;
        };
    }

    /**
     * Creates a condition requiring all provided conditions to pass (logical AND).
     *
     * <p>Short-circuits upon the first failing condition. If no conditions are supplied,
     * this condition always passes (vacuous truth).</p>
     *
     * @param conditions conditions to evaluate
     * @return composite AND condition
     */
    static SpawnCondition allOf(SpawnCondition... conditions) {
        if (conditions.length == 0) {
            return (_, _) -> true;
        }
        if (conditions.length == 1) {
            return java.util.Objects.requireNonNull(conditions[0], "condition cannot be null");
        }
        SpawnCondition[] copy = conditions.clone();
        for (SpawnCondition condition : copy) {
            java.util.Objects.requireNonNull(condition, "condition cannot be null");
        }
        return (location, world) -> {
            for (SpawnCondition condition : copy) {
                if (!condition.testCanSpawnAt(location, world)) {
                    return false;
                }
            }
            return true;
        };
    }

    /**
     * Creates a condition requiring none of the provided conditions to pass (logical NOR).
     *
     * @param conditions conditions that must not pass
     * @return composite NOR condition
     */
    static SpawnCondition noneOf(SpawnCondition... conditions) {
        return anyOf(conditions).negate();
    }
}
