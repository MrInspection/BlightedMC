package fr.moussax.blightedMC.engine.entities.spawnable;

import fr.moussax.blightedMC.engine.entities.spawnable.condition.SpawnCondition;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.World;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds a set of {@link SpawnCondition} rules evaluated with AND semantics.
 *
 * <p>All registered conditions must be satisfied for spawning to be allowed.
 * Evaluation fails fast on the first failing condition.</p>
 */
@NoArgsConstructor
public final class SpawnProfile {

    private final List<SpawnCondition> conditions = new ArrayList<>();

    private SpawnProfile(List<SpawnCondition> conditions) {
        this.conditions.addAll(conditions);
    }

    /**
     * Adds a spawn condition rule to this profile.
     *
     * @param condition condition rule to add
     */
    public void addCondition(@NonNull SpawnCondition condition) {
        conditions.add(condition);
    }

    /**
     * Tests whether all conditions in this profile permit spawning at the given location.
     *
     * @param location target spawn location
     * @param world    target spawn world
     * @return {@code true} if all conditions pass, {@code false} if any condition fails
     */
    public boolean canSpawn(Location location, World world) {
        for (SpawnCondition condition : conditions) {
            if (!condition.testCanSpawnAt(location, world)) return false;
        }
        return true;
    }

    /**
     * Creates an independent copy of this spawn profile and its conditions.
     *
     * @return a new spawn profile instance with identical conditions
     */
    public SpawnProfile copy() {
        return new SpawnProfile(this.conditions);
    }
}
