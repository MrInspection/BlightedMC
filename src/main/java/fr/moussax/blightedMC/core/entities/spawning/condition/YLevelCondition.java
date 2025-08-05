package fr.moussax.blightedMC.core.entities.spawning.condition;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * SpawnCondition that restricts spawning based on the Y-coordinate (vertical level) of the spawn location.
 */
public class YLevelCondition implements SpawnCondition {
  private final int minY;
  private final int maxY;

  /**
   * Constructs a YLevelCondition with minimum and maximum Y levels allowed for spawning.
   *
   * @param minY minimum Y level (inclusive) where spawning is allowed
   * @param maxY maximum Y level (inclusive) where spawning is allowed
   */
  public YLevelCondition(int minY, int maxY) {
    this.minY = minY;
    this.maxY = maxY;
  }

  @Override
  public boolean canSpawn(Location location, World world) {
    int y = location.getBlockY();
    return y >= minY && y <= maxY;
  }
}
