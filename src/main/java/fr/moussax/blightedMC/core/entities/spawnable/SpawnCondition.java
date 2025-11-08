package fr.moussax.blightedMC.core.entities.spawnable;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * Interface representing a condition that determines whether an entity can spawn
 * at a specific location within a given world.
 */
public interface SpawnCondition {
  /**
   * Evaluates if spawning is allowed at the specified location and world.
   *
   * @param location the location to check for spawning
   * @param world the world where the spawn attempt occurs
   * @return true if spawning is permitted, false otherwise
   */
  boolean canSpawn(Location location, World world);

  default SpawnCondition and(SpawnCondition other) {
    return (loc, world) -> this.canSpawn(loc, world) && other.canSpawn(loc, world);
  }
  default SpawnCondition or(SpawnCondition other) {
    return (loc, world) -> this.canSpawn(loc, world) || other.canSpawn(loc, world);
  }
  default SpawnCondition not() {
    return (loc, world) -> !this.canSpawn(loc, world);
  }
}
