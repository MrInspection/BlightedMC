package fr.moussax.blightedMC.core.entities.spawning.condition;

import org.bukkit.Location;
import org.bukkit.World;

public class YLevelCondition implements SpawnCondition {
  private final int minY;
  private final int maxY;

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
