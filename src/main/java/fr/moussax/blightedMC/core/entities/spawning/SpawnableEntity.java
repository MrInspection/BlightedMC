package fr.moussax.blightedMC.core.entities.spawning;

import fr.moussax.blightedMC.core.entities.BlightedEntity;
import fr.moussax.blightedMC.core.entities.spawning.condition.SpawnCondition;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for entities that can spawn naturally in the world.
 * Extends BlightedEntity and includes spawn profile functionality.
 */
public abstract class SpawnableEntity extends BlightedEntity {
  private final SpawnEntitiesProfile spawnProfile;
  private final double spawnChance;
  private final String entityId;

  /**
   * Creates a spawnable entity with the given parameters.
   *
   * @param entityId     unique identifier for this entity type
   * @param name         display the name of the entity
   * @param maxHealth    maximum health of the entity
   * @param entityType   the vanilla entity type to use as a base
   * @param spawnChance  probability of spawning when conditions are met (0.0-1.0)
   */
  public SpawnableEntity(String entityId, String name, int maxHealth, org.bukkit.entity.EntityType entityType, double spawnChance) {
    super(name, maxHealth, entityType);
    this.entityId = entityId;
    this.spawnChance = spawnChance;
    this.spawnProfile = new SpawnEntitiesProfile();
    setupSpawnConditions();
  }

  /**
   * Sets up the spawn conditions for this entity.
   * Override this method to add specific spawn conditions.
   */
  protected abstract void setupSpawnConditions();

  /**
   * Adds a spawn condition to this entity's spawn profile.
   *
   * @param condition the spawn condition to add
   * @return this SpawnableEntity for chaining
   */
  protected SpawnableEntity addSpawnCondition(SpawnCondition condition) {
    spawnProfile.addCondition(condition);
    return this;
  }

  /**
   * Checks if this entity can spawn at the given location.
   *
   * @param location the location to check
   * @param world    the world to check
   * @return true if the entity can spawn at the location
   */
  public boolean canSpawnAt(Location location, World world) {
    return spawnProfile.canSpawn(location, world);
  }

  /**
   * Gets the spawn chance for this entity.
   *
   * @return the spawn chance (0.0-1.0)
   */
  public double getSpawnChance() {
    return spawnChance;
  }

  /**
   * Gets the unique identifier for this entity type.
   *
   * @return the entity ID
   */
  @Override
  public String getEntityId() {
    return entityId;
  }

  /**
   * Gets the spawn profile for this entity.
   *
   * @return the spawn profile
   */
  public SpawnEntitiesProfile getSpawnProfile() {
    return spawnProfile;
  }
} 