package fr.moussax.blightedMC.smp.core.entities.registry;

import fr.moussax.blightedMC.smp.core.entities.spawnable.SpawnableEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SpawnableEntitiesRegistry {
    private static final Map<String, SpawnableEntity> SPAWNABLE_ENTITIES = new HashMap<>();

    public static void clearEntities() {
        SPAWNABLE_ENTITIES.clear();
    }

    public static void registerSpawnableEntity(SpawnableEntity entity) {
        if (SPAWNABLE_ENTITIES.containsKey(entity.getEntityId())) {
            throw new IllegalArgumentException("Duplicate spawnable entity ID: " + entity.getEntityId());
        }
        SPAWNABLE_ENTITIES.put(entity.getEntityId(), entity);
    }

    public static SpawnableEntity getEntity(String entityId) {
        SpawnableEntity prototype = SPAWNABLE_ENTITIES.get(entityId);
        return prototype != null ? prototype.clone() : null;
    }

    public static List<SpawnableEntity> getAllEntities() {
        return SPAWNABLE_ENTITIES.values().stream()
            .map(SpawnableEntity::clone)
            .toList();
    }
}
