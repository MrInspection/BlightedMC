package fr.moussax.blightedMC.engine.entities.registry;

import fr.moussax.blightedMC.engine.entities.spawnable.SpawnableEntity;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SpawnableEntitiesRegistry {

    private static final Map<String, SpawnableEntity> SPAWNABLE_ENTITIES = new HashMap<>();

    private SpawnableEntitiesRegistry() {}

    static void register(SpawnableEntity entity) {
        SPAWNABLE_ENTITIES.put(entity.getEntityId(), entity);
    }

    @Nullable
    public static SpawnableEntity get(String entityId) {
        SpawnableEntity prototype = SPAWNABLE_ENTITIES.get(entityId);
        return prototype != null ? prototype.clone() : null;
    }

    public static List<SpawnableEntity> getAll() {
        return SPAWNABLE_ENTITIES.values().stream()
            .map(SpawnableEntity::clone)
            .toList();
    }

    public static int count() {
        return SPAWNABLE_ENTITIES.size();
    }

    public static void clear() {
        SPAWNABLE_ENTITIES.clear();
    }
}
