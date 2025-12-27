package fr.moussax.blightedMC.smp.core.entities.registry;

import fr.moussax.blightedMC.smp.core.entities.spawnable.SpawnableEntity;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registry for all spawnable entities in BlightedMC.
 * <p>
 * Maintains a mapping of entity IDs to spawnable entity prototypes. Entities
 * are cloned on retrieval to prevent shared state between instances.
 * <p>
 * This registry is automatically populated by {@link EntitiesRegistry} when
 * spawnable entities are registered.
 */
public final class SpawnableEntitiesRegistry {

    private static final Map<String, SpawnableEntity> SPAWNABLE_ENTITIES = new HashMap<>();

    private SpawnableEntitiesRegistry() {}

    /**
     * Registers a spawnable entity prototype.
     *
     * @param entity the spawnable entity to register
     */
    static void register(SpawnableEntity entity) {
        SPAWNABLE_ENTITIES.put(entity.getEntityId(), entity);
    }

    /**
     * Retrieves a cloned instance of a spawnable entity by its ID.
     *
     * @param entityId the unique entity ID
     * @return a cloned spawnable entity, or null if not found
     */
    @Nullable
    public static SpawnableEntity get(String entityId) {
        SpawnableEntity prototype = SPAWNABLE_ENTITIES.get(entityId);
        return prototype != null ? prototype.clone() : null;
    }

    /**
     * Retrieves clones of all registered spawnable entities.
     *
     * @return a list of cloned spawnable entities
     */
    public static List<SpawnableEntity> getAll() {
        return SPAWNABLE_ENTITIES.values().stream()
            .map(SpawnableEntity::clone)
            .toList();
    }

    /**
     * Returns the total number of registered spawnable entities.
     *
     * @return count of spawnable entities
     */
    public static int count() {
        return SPAWNABLE_ENTITIES.size();
    }

    /** Clears all registered spawnable entities. */
    public static void clear() {
        SPAWNABLE_ENTITIES.clear();
    }
}
