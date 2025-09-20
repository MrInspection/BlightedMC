package fr.moussax.blightedMC.core.entities.spawning;

import fr.moussax.blightedMC.core.entities.BlightedEntity;
import fr.moussax.blightedMC.core.entities.spawning.condition.SpawnCondition;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

public abstract class SpawnableEntity extends BlightedEntity implements Cloneable {
  private SpawnEntitiesProfile spawnProfile;
  private final double spawnChance;
  private final String entityId;

  public SpawnableEntity(String entityId, String name, int maxHealth, EntityType entityType, double spawnChance) {
    super(name, maxHealth, entityType);
    this.entityId = entityId;
    this.spawnChance = spawnChance;
    this.spawnProfile = new SpawnEntitiesProfile();
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

  public SpawnEntitiesProfile getSpawnProfile() {
    return spawnProfile;
  }

  @Override
  public SpawnableEntity clone() {
    SpawnableEntity clone = (SpawnableEntity) super.clone();
    clone.spawnProfile = this.spawnProfile.clone();
    return clone;
  }
}
