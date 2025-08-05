package fr.moussax.blightedMC.core.entities.spawning.condition;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * SpawnCondition that allows spawning based on a random chance.
 */
public class ChanceCondition implements SpawnCondition {
  private final double chance;

  /**
   * Constructs a ChanceCondition with the given spawn probability.
   *
   * @param chance the probability (0.0 to 1.0) that spawning is allowed
   */
  public ChanceCondition(double chance) {
    this.chance = chance;
  }

  @Override
  public boolean canSpawn(Location location, World world) {
    return Math.random() < chance;
  }
}
