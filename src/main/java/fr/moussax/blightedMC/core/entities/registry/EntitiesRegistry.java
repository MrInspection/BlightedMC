package fr.moussax.blightedMC.core.entities.registry;

import fr.moussax.blightedMC.core.entities.BlightedEntity;
import fr.moussax.blightedMC.core.entities.spawnable.SpawnableEntity;
import fr.moussax.blightedMC.game.entities.Dummy;
import fr.moussax.blightedMC.game.entities.bosses.RevenantHorror;
import fr.moussax.blightedMC.game.entities.bosses.TheAncientKnight;
import fr.moussax.blightedMC.game.entities.spawnable.BlightedZombie;
import fr.moussax.blightedMC.game.entities.spawnable.LaserEngineer;
import fr.moussax.blightedMC.utils.debug.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EntitiesRegistry {
    private static final String REGISTRY_PREFIX = "EntitiesRegistry";
    private static final Map<String, BlightedEntity> ENTITIES = new HashMap<>();

    public static void clearEntities() {
        ENTITIES.clear();
        SpawnableEntitiesRegistry.clearEntities();
    }

    public static void registerEntity(BlightedEntity entity) {
        if (ENTITIES.containsKey(entity.getEntityId())) {
            throw new IllegalArgumentException("Duplicate entity ID: " + entity.getEntityId());
        }
        ENTITIES.put(entity.getEntityId(), entity);

        if (entity instanceof SpawnableEntity spawnable) {
            SpawnableEntitiesRegistry.registerSpawnableEntity(spawnable);
        }
    }

    public static BlightedEntity getEntity(String entityId) {
        BlightedEntity prototype = ENTITIES.get(entityId);
        return prototype != null ? prototype.clone() : null;
    }

    public static List<BlightedEntity> getAllEntities() {
        return ENTITIES.values().stream()
            .map(BlightedEntity::clone)
            .toList();
    }

    private static final List<BlightedEntity> DEFAULT_ENTITIES = List.of(
        new TheAncientKnight(),
        new RevenantHorror(),
        new BlightedZombie(),
        new LaserEngineer(),
        new Dummy()
    );

    public static void initializeEntities() {
        clearEntities();
        DEFAULT_ENTITIES.forEach(EntitiesRegistry::registerEntity);
        Log.success("EntitiesRegistry", "Registered " + ENTITIES.size() + " entities.");
    }
}
