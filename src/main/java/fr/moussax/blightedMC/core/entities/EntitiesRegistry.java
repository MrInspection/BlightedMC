package fr.moussax.blightedMC.core.entities;

import fr.moussax.blightedMC.core.registry.entities.Dummy;
import fr.moussax.blightedMC.core.entities.spawning.SpawnableEntity;
import fr.moussax.blightedMC.core.entities.spawning.SpawnableEntitiesRegistry;
import fr.moussax.blightedMC.core.registry.entities.bosses.RevenantHorror;
import fr.moussax.blightedMC.core.registry.entities.bosses.TheAncientKnight;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EntitiesRegistry {
  private static final Map<String, BlightedEntity> ENTITY_PROTOTYPES = new HashMap<>();

  public static void clearEntities() {
    ENTITY_PROTOTYPES.clear();
  }

  public static void addEntity(BlightedEntity entity) {
    if (ENTITY_PROTOTYPES.containsKey(entity.getEntityId())) {
      throw new IllegalArgumentException("Duplicate entity ID: " + entity.getEntityId());
    }
    ENTITY_PROTOTYPES.put(entity.getEntityId(), entity);
  }

  public static BlightedEntity getEntity(String entityId) {
    BlightedEntity prototype = ENTITY_PROTOTYPES.get(entityId);
    return prototype != null ? prototype.clone() : null;
  }

  public static List<BlightedEntity> getAllEntities() {
    return ENTITY_PROTOTYPES.values().stream()
      .map(BlightedEntity::clone)
      .toList();
  }

  public static void initializeEntities() {
    clearEntities();

    addEntity(new RevenantHorror());
    addEntity(new TheAncientKnight());
    addEntity(new Dummy());

    List<SpawnableEntity> spawnableEntities = SpawnableEntitiesRegistry.getAllEntities();
    for (SpawnableEntity entity : spawnableEntities) {
      addEntity(entity);
    }
  }
}
