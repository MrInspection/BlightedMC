package fr.moussax.blightedMC.engine.entities.spawnable;

import fr.moussax.blightedMC.engine.entities.spawnable.condition.SpawnCondition;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.World;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds a set of {@link SpawnCondition}s evaluated with AND semantics.
 * All conditions must pass for spawning to be allowed. Fails fast on the first rejection.
 *
 * <pre>{@code
 * SpawnProfile profile = new SpawnProfile();
 * profile.addCondition(SpawnRules.biome(Biome.PLAINS));
 * profile.addCondition(SpawnRules.nightTime());
 * boolean allowed = profile.canSpawn(location, world);
 * }</pre>
 */
@NoArgsConstructor
public final class SpawnProfile {

    private final List<SpawnCondition> conditions = new ArrayList<>();

    private SpawnProfile(List<SpawnCondition> conditions) {
        this.conditions.addAll(conditions);
    }

    /**
     * Adds a spawn condition. All conditions must pass for spawning to be allowed.
     */
    public void addCondition(@NonNull SpawnCondition condition) {
        conditions.add(condition);
    }

    /**
     * Evaluates all conditions at the given location.
     *
     * @return {@code true} if all conditions pass or no conditions are registered
     */
    public boolean canSpawn(Location location, World world) {
        for (SpawnCondition condition : conditions) {
            if (!condition.testCanSpawnAt(location, world)) return false;
        }
        return true;
    }

    /** Returns a copy of this profile with the same conditions. */
    public SpawnProfile copy() {
        return new SpawnProfile(this.conditions);
    }
}
