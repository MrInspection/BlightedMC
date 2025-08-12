package fr.moussax.blightedMC.core.entities.spawning;

import fr.moussax.blightedMC.core.registry.entities.spawnable.Boulder;
import fr.moussax.blightedMC.core.registry.entities.spawnable.LaserEngineer;
import fr.moussax.blightedMC.core.registry.entities.spawnable.blighted.BlightedZombie;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SpawnableEntitiesRegistry {
  public static final Map<String, SpawnableEntity> SPAWNABLE_ENTITIES = new HashMap<>();

  public static void clearEntities() {
    SPAWNABLE_ENTITIES.clear();
  }

  public static void addEntity(SpawnableEntity entity) {
    if (SPAWNABLE_ENTITIES.containsKey(entity.getEntityId())) {
      throw new IllegalArgumentException("Duplicate entity ID: " + entity.getEntityId());
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

  public static void initializeEntities() {
    clearEntities();

    addEntity(new BlightedZombie());
    addEntity(new LaserEngineer());
    addEntity(new Boulder());
  }
}
