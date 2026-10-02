package fr.moussax.blightedSMP.engine.entities.spawnable;

import fr.moussax.blightedSMP.engine.entities.spawnable.condition.SpawnCondition;
import org.bukkit.Location;
import org.bukkit.World;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * Holds a set of {@link SpawnCondition} rules evaluated with AND semantics.
 *
 * <p>All registered conditions must be satisfied for spawning to be allowed.
 * Evaluation fails fast on the first failing condition.</p>
 */
public record SpawnProfile(List<SpawnCondition> conditions) {

    /**
     * Constructs a spawn profile with an immutable snapshot of conditions.
     *
     * @param conditions conditions to evaluate
     */
    public SpawnProfile(@NonNull List<SpawnCondition> conditions) {
        this.conditions = List.copyOf(conditions);
    }

    /**
     * Constructs an empty spawn profile permitting spawning everywhere.
     */
    public SpawnProfile() {
        this(Collections.emptyList());
    }

    /**
     * Tests whether all conditions in this profile permit spawning at the given location.
     *
     * @param location target spawn location
     * @param world    target spawn world
     * @return {@code true} if all conditions pass, {@code false} if any condition fails
     */
    public boolean canSpawn(@Nullable Location location, @Nullable World world) {
        if (location == null || world == null) {
            return false;
        }
        for (SpawnCondition condition : conditions) {
            if (!condition.testCanSpawnAt(location, world)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns an unmodifiable snapshot of registered spawn conditions.
     *
     * @return list of spawn conditions
     */
    @Override
    @NonNull
    public List<SpawnCondition> conditions() {
        return conditions;
    }
}
