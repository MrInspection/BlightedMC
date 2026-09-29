package fr.moussax.blightedSMP.engine.entities.registry;

import fr.moussax.blightedSMP.engine.entities.spawnable.SpawnableEntity;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Convenience view delegating spawnable entity queries to {@link EntitiesRegistry}.
 */
public final class SpawnableEntitiesRegistry {

    private SpawnableEntitiesRegistry() {
    }

    /**
     * Retrieves a cloned instance of a registered spawnable entity prototype by ID.
     *
     * @param entityId target entity identifier
     * @return cloned spawnable entity instance, or {@code null} if untracked or not spawnable
     */
    @Nullable
    public static SpawnableEntity get(String entityId) {
        return EntitiesRegistry.getSpawnable(entityId);
    }

    /**
     * Returns a list of cloned instances of all registered spawnable entity prototypes.
     *
     * @return list of cloned spawnable entity prototypes
     */
    public static List<SpawnableEntity> getAll() {
        return EntitiesRegistry.getSpawnables();
    }

    /**
     * Returns total number of registered spawnable entity prototypes.
     *
     * @return registered spawnable entity count
     */
    public static int count() {
        return EntitiesRegistry.getSpawnables().size();
    }

    /**
     * No-op delegate for compatibility; delegate clearing is handled by {@link EntitiesRegistry#clear()}.
     */
    public static void clear() {
        // No-op: delegate handled by EntitiesRegistry.clear()
    }
}
