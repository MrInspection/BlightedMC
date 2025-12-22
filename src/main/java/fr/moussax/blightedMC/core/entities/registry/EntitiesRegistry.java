package fr.moussax.blightedMC.core.entities.registry;

import fr.moussax.blightedMC.core.entities.AbstractBlightedEntity;
import fr.moussax.blightedMC.core.entities.spawnable.SpawnableEntity;
import fr.moussax.blightedMC.game.entities.Dummy;
import fr.moussax.blightedMC.game.entities.bosses.Goldor;
import fr.moussax.blightedMC.game.entities.bosses.RevenantHorror;
import fr.moussax.blightedMC.game.entities.bosses.TheAncientKnight;
import fr.moussax.blightedMC.game.entities.spawnable.LaserEngineer;
import fr.moussax.blightedMC.game.entities.spawnable.blighted.BlightedZombie;
import fr.moussax.blightedMC.utils.debug.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EntitiesRegistry {
    private static final String REGISTRY_PREFIX = "EntitiesRegistry";
    private static final Map<String, AbstractBlightedEntity> ENTITIES = new HashMap<>();

    public static void clearEntities() {
        ENTITIES.clear();
        SpawnableEntitiesRegistry.clearEntities();
    }

    public static void registerEntity(AbstractBlightedEntity entity) {
        if (ENTITIES.containsKey(entity.getEntityId())) {
            throw new IllegalArgumentException("Duplicate entity ID: " + entity.getEntityId());
        }
        ENTITIES.put(entity.getEntityId(), entity);

        if (entity instanceof SpawnableEntity spawnable) {
            SpawnableEntitiesRegistry.registerSpawnableEntity(spawnable);
        }
    }

    public static AbstractBlightedEntity getEntity(String entityId) {
        AbstractBlightedEntity prototype = ENTITIES.get(entityId);
        return prototype != null ? prototype.clone() : null;
    }

    public static List<AbstractBlightedEntity> getAllEntities() {
        return ENTITIES.values().stream()
            .map(AbstractBlightedEntity::clone)
            .toList();
    }

    private static final List<AbstractBlightedEntity> DEFAULT_ENTITIES = List.of(
        new TheAncientKnight(),
        new RevenantHorror(),
        new BlightedZombie(),
        new LaserEngineer(),
        new Dummy(),
        new Goldor()
    );

    public static void initializeEntities() {
        clearEntities();
        DEFAULT_ENTITIES.forEach(EntitiesRegistry::registerEntity);
        Log.success("EntitiesRegistry", "Registered " + ENTITIES.size() + " entities.");
    }
}
