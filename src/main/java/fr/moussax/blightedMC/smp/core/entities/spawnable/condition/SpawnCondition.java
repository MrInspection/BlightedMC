package fr.moussax.blightedMC.smp.core.entities.spawnable.condition;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * Predicate defining whether an entity may spawn at a given {@link Location} in a {@link World}.
 *
 * <p>Conditions are composable via {@link #and}, {@link #or}, and {@link #negate}.
 *
 * <pre>{@code
 * SpawnCondition validSpawn = SpawnRules.biome(Biome.PLAINS)
 *     .and(SpawnRules.nightTime())
 *     .and(SpawnRules.skyExposed());
 * }</pre>
 */
@FunctionalInterface
public interface SpawnCondition {

    /**
     * Evaluates whether spawning is allowed at the given location.
     *
     * @return {@code true} if spawning is permitted
     */
    boolean testCanSpawnAt(Location location, World world);

    default SpawnCondition and(SpawnCondition other) {
        return (location, world) -> this.testCanSpawnAt(location, world) && other.testCanSpawnAt(location, world);
    }

    default SpawnCondition or(SpawnCondition other) {
        return (location, world) -> this.testCanSpawnAt(location, world) || other.testCanSpawnAt(location, world);
    }

    default SpawnCondition negate() {
        return (location, world) -> !this.testCanSpawnAt(location, world);
    }
}
