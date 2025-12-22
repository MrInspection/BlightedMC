package fr.moussax.blightedMC.core.entities.spawnable;

import org.bukkit.Location;
import org.bukkit.World;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines a set of {@link SpawnCondition}s that must all pass
 * for an entity to spawn.
 * <p>
 * Conditions are evaluated using logical AND semantics and
 * fail fast on the first rejection.
 *
 * <p>Example:
 * <pre>{@code
 * SpawnProfile profile = new SpawnProfile();
 * profile.addSpawnCondition(SpawnConditionFactory.biome(Biome.PLAINS));
 * profile.addSpawnCondition(SpawnConditionFactory.nightTime());
 *
 * boolean allowed = profile.canSpawn(location, world);
 * }</pre>
 */
public class SpawnProfile {
    private final List<SpawnCondition> conditions;

    /**
     * Creates an empty spawnable entity profile with no conditions.
     * All spawns will be allowed until conditions are added.
     */
    public SpawnProfile() {
        this.conditions = new ArrayList<>();
    }

    /**
     * Internal constructor used to create deep copies of profiles.
     *
     * @param conditions the list of conditions to copy
     */
    private SpawnProfile(List<SpawnCondition> conditions) {
        this.conditions = new ArrayList<>(conditions);
    }

    /**
     * Adds a spawn condition.
     * All conditions must pass for spawning to be allowed.
     */
    public void addSpawnCondition(@NonNull SpawnCondition condition) {
        conditions.add(condition);
    }

    /**
     * Evaluates all spawn conditions at the given location.
     *
     * @return {@code true} if all conditions pass
     */
    public boolean canSpawn(Location location, World world) {
        for (SpawnCondition condition : conditions) {
            if (!condition.testCanSpawnAt(location, world)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the number of registered spawn conditions.
     *
     * @return the total number of conditions
     */
    public int getConditionCount() {
        return conditions.size();
    }

    /**
     * Creates a deep copy of this spawnable entity profile.
     * <p>
     * The copied profile will contain the same conditions but as a separate list instance.
     *
     * @return a new {@link SpawnProfile} with identical conditions
     */
    public SpawnProfile copy() {
        return new SpawnProfile(this.conditions);
    }
}
