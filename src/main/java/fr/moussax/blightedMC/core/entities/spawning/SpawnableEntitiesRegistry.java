package fr.moussax.blightedMC.core.entities.spawning;

import fr.moussax.blightedMC.core.registry.entities.spawnable.ExampleSpawnableZombie;
import fr.moussax.blightedMC.core.registry.entities.spawnable.ExampleSpawnableSkeleton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SpawnableEntitiesRegistry {
  public static final Map<String, SpawnableEntity> SPAWNABLE_ENTITIES = new HashMap<>();

  /**
   * Clears all registered spawnable entities.
   */
  public static void clearEntities() {
    SPAWNABLE_ENTITIES.clear();
  }

  /**
   * Adds a spawnable entity to the registry.
   *
   * @param entity the spawnable entity to register
   * @throws IllegalArgumentException if an entity with the same ID already exists
   */
  public static void addEntity(SpawnableEntity entity) {
    if (SPAWNABLE_ENTITIES.containsKey(entity.getEntityId())) {
      throw new IllegalArgumentException("Duplicate entity ID: " + entity.getEntityId());
    }
    SPAWNABLE_ENTITIES.put(entity.getEntityId(), entity);
  }

  /**
   * Gets a spawnable entity by its ID.
   *
   * @param entityId the entity ID
   * @return the spawnable entity, or null if not found
   */
  public static SpawnableEntity getEntity(String entityId) {
    return SPAWNABLE_ENTITIES.get(entityId);
  }

  /**
   * Gets all registered spawnable entities.
   *
   * @return a list of all spawnable entities
   */
  public static List<SpawnableEntity> getAllEntities() {
    return List.copyOf(SPAWNABLE_ENTITIES.values());
  }

  /**
   * Initializes the spawnable entities registry.
   * This method should be called to register all spawnable entities.
   */
  public static void initializeEntities() {
    clearEntities();
    
    // Register spawnable entities here
    addEntity(new ExampleSpawnableZombie());
    addEntity(new ExampleSpawnableSkeleton());
    
    // Add more spawnable entities as needed:
    // addEntity(new CustomCreeper());
    // addEntity(new CustomSpider());
    // etc.
  }
}
