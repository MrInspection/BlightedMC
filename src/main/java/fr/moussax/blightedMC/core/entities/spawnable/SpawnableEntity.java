package fr.moussax.blightedMC.core.entities.spawnable;

import fr.moussax.blightedMC.core.entities.BlightedEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;

public abstract class SpawnableEntity extends BlightedEntity {
  private SpawnableEntityProfile spawnProfile;
  private final double spawnChance;
  private final String entityId;

  public SpawnableEntity(String entityId, String name, int maxHealth, EntityType entityType, double spawnChance) {
    super(name, maxHealth, entityType);
    this.entityId = entityId;
    this.spawnChance = spawnChance;
    this.spawnProfile = new SpawnableEntityProfile();
    setupSpawnConditions();
  }

  protected abstract void setupSpawnConditions();

  @SuppressWarnings("UnusedReturnValue")
  protected SpawnableEntity addSpawnCondition(SpawnCondition condition) {
    spawnProfile.addCondition(condition);
    return this;
  }

  public boolean canSpawnAt(Location location, World world) {
    return spawnProfile.canSpawn(location, world);
  }

  public double getSpawnChance() {
    return spawnChance;
  }

  @Override
  public String getEntityId() {
    return entityId;
  }

  public SpawnableEntityProfile getSpawnProfile() {
    return spawnProfile;
  }

  /**
   * Spawns this custom entity at a location using the default (CUSTOM) spawn reason.
   */
  public LivingEntity spawn(Location location) {
    return spawn(location, CreatureSpawnEvent.SpawnReason.CUSTOM);
  }

  /**
   * Spawns this custom entity at a location using the given spawn reason.
   */
  public LivingEntity spawn(Location location, CreatureSpawnEvent.SpawnReason reason) {
    return super.spawn(location);
  }

  @Override
  public SpawnableEntity clone() {
    SpawnableEntity clone = (SpawnableEntity) super.clone();
    clone.spawnProfile = this.spawnProfile.clone();
    return clone;
  }
}
