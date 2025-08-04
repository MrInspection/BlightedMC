package fr.moussax.blightedMC.core.entities;

import fr.moussax.blightedMC.core.registry.entities.ExperimentalMob;
import fr.moussax.blightedMC.core.registry.entities.GaiaConstruct;
import fr.moussax.blightedMC.core.entities.spawning.SpawnableEntity;
import fr.moussax.blightedMC.core.entities.spawning.SpawnableEntitiesRegistry;
import fr.moussax.blightedMC.core.registry.entities.bosses.AtonedHorror;
import fr.moussax.blightedMC.core.registry.entities.bosses.RevenantHorror;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EntitiesRegistry {
  public static final Map<String, BlightedEntity> BLIGHTED_ENTITIES = new HashMap<>();

  public static void clearEntities() {
    BLIGHTED_ENTITIES.clear();
  }

  public static void addEntity(BlightedEntity entity) {
    if (BLIGHTED_ENTITIES.containsKey(entity.getEntityId())) {
      throw new IllegalArgumentException("Duplicate entity ID: " + entity.getEntityId());
    }
    BLIGHTED_ENTITIES.put(entity.getEntityId(), entity);
  }

  public static BlightedEntity getEntity(String entityId) {
    return BLIGHTED_ENTITIES.get(entityId);
  }

  public static List<BlightedEntity> getAllEntities() {
    return List.copyOf(BLIGHTED_ENTITIES.values());
  }

  public static void initializeEntities() {
    clearEntities();

    addEntity(new ExperimentalMob());
    addEntity(new GaiaConstruct());
    addEntity(new RevenantHorror());
    addEntity(new AtonedHorror());

    // Register spawnable entities from SpawnableEntitiesRegistry
    List<SpawnableEntity> spawnableEntities = SpawnableEntitiesRegistry.getAllEntities();
    for (SpawnableEntity entity : spawnableEntities) {
      addEntity(entity);
    }
  }
}
