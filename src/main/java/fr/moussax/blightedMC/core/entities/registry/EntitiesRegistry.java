package fr.moussax.blightedMC.core.entities.registry;

import fr.moussax.blightedMC.core.entities.BlightedEntity;
import fr.moussax.blightedMC.core.entities.spawnable.SpawnableEntity;
import fr.moussax.blightedMC.gameplay.entities.Dummy;
import fr.moussax.blightedMC.gameplay.entities.bosses.RevenantHorror;
import fr.moussax.blightedMC.gameplay.entities.bosses.TheAncientKnight;
import fr.moussax.blightedMC.gameplay.entities.spawnable.LaserEngineer;
import fr.moussax.blightedMC.gameplay.entities.spawnable.BlightedZombie;
import fr.moussax.blightedMC.utils.debug.Log;

import java.util.*;

public final class EntitiesRegistry {
  private static final String REGISTRY_PREFIX = "EntitiesRegistry";
  private static final Map<String, BlightedEntity> ENTITIES = new HashMap<>();

  public static void clearEntities() {
    ENTITIES.clear();
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

  public static void initializeEntities() {
    clearEntities();

    registerEntity(new TheAncientKnight());
    registerEntity(new RevenantHorror());
    registerEntity(new BlightedZombie());
    registerEntity(new LaserEngineer());
    registerEntity(new Dummy());
    Log.info(REGISTRY_PREFIX,  "Registered " + ENTITIES.size() + " entities.");
  }
}
