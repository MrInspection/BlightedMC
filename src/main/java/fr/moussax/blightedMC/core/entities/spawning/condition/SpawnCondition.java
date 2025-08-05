package fr.moussax.blightedMC.core.entities.spawning.condition;

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
}
