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

/**
 * Central registry for all custom {@link BlightedEntity} instances in the plugin.
 * <p>
 * This class handles registration, retrieval, and initialization of entities,
 * including bosses and spawnable mobs.
 */
public final class EntitiesRegistry {
  public static final Map<String, BlightedEntity> BLIGHTED_ENTITIES = new HashMap<>();

  /**
   * Removes all entities from the registry.
   */
  public static void clearEntities() {
    BLIGHTED_ENTITIES.clear();
  }

  /**
   * Registers a new entity in the registry.
   *
   * @param entity the entity to register
   * @throws IllegalArgumentException if an entity with the same ID is already registered
   */
  public static void addEntity(BlightedEntity entity) {
    if (BLIGHTED_ENTITIES.containsKey(entity.getEntityId())) {
      throw new IllegalArgumentException("Duplicate entity ID: " + entity.getEntityId());
    }
    BLIGHTED_ENTITIES.put(entity.getEntityId(), entity);
  }

  /**
   * Retrieves a registered entity by its unique ID.
   *
   * @param entityId the entity ID
   * @return the corresponding {@link BlightedEntity}, or {@code null} if not found
   */
  public static BlightedEntity getEntity(String entityId) {
    return BLIGHTED_ENTITIES.get(entityId);
  }

  /**
   * Returns an immutable list of all registered entities.
   *
   * @return a list containing all {@link BlightedEntity} instances
   */
  public static List<BlightedEntity> getAllEntities() {
    return List.copyOf(BLIGHTED_ENTITIES.values());
  }

  /**
   * Clears and re-registers all default and spawnable entities.
   * <p>
   * This method initializes:
   * <ul>
   *   <li>Experimental and construct mobs</li>
   *   <li>Bosses like {@link RevenantHorror} and {@link AtonedHorror}</li>
   *   <li>All entities from {@link SpawnableEntitiesRegistry}</li>
   * </ul>
   */
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
