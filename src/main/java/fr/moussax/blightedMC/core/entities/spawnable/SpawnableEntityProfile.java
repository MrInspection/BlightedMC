package fr.moussax.blightedMC.core.entities.spawnable;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a profile defining the spawning conditions for a specific type of entity.
 * <p>
 * A {@code SpawnableEntityProfile} holds a list of {@link SpawnCondition} instances,
 * each representing an independent rule that must be satisfied for spawning to occur.
 * The {@link #canSpawn(Location, World)} method evaluates all conditions and returns
 * {@code true} only if every condition passes.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * SpawnableEntityProfile profile = new SpawnableEntityProfile();
 * profile.addCondition(SpawnConditions.biome(Biome.PLAINS));
 * profile.addCondition(SpawnConditions.nightTime());
 * profile.addCondition(SpawnConditions.notInWater());
 *
 * boolean canSpawn = profile.canSpawn(location, world);
 * }</pre>
 */
public class SpawnableEntityProfile {
    private final List<SpawnCondition> conditions;

    /**
     * Creates an empty spawnable entity profile with no conditions.
     * All spawns will be allowed until conditions are added.
     */
    public SpawnableEntityProfile() {
        this.conditions = new ArrayList<>();
    }

    /**
     * Internal constructor used to create deep copies of profiles.
     *
     * @param conditions the list of conditions to copy
     */
    private SpawnableEntityProfile(List<SpawnCondition> conditions) {
        this.conditions = new ArrayList<>(conditions);
    }

    /**
     * Adds a new {@link SpawnCondition} to this profile.
     * <p>
     * Multiple conditions are combined with a logical AND,
     * meaning all must pass for the entity to spawn.
     *
     * @param condition the condition to add
     */
    public void addCondition(SpawnCondition condition) {
        conditions.add(condition);
    }

    /**
     * Checks if an entity with this profile can spawn at the given location and world.
     * <p>
     * The check iterates through all registered conditions and fails fast
     * when any condition returns {@code false}.
     *
     * @param location the location to test
     * @param world    the world in which the entity would spawn
     * @return {@code true} if all conditions pass, {@code false} otherwise
     */
    public boolean canSpawn(Location location, World world) {
        for (SpawnCondition condition : conditions) {
            if (!condition.canSpawn(location, world)) {
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
     * @return a new {@link SpawnableEntityProfile} with identical conditions
     */
    public SpawnableEntityProfile deepCopy() {
        return new SpawnableEntityProfile(this.conditions);
    }
}
