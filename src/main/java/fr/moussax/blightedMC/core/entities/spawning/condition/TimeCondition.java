package fr.moussax.blightedMC.core.entities.spawning.condition;

import org.bukkit.Location;
import org.bukkit.World;

public class TimeCondition implements SpawnCondition {
  private final boolean onlyNightTime;

  public TimeCondition(boolean onlyNightTime) {
    this.onlyNightTime = onlyNightTime;
  }

  @Override
  public boolean canSpawn(Location location, World world) {
    long time = world.getTime();
    boolean isNight = time > 13000 && time < 23000;
    return onlyNightTime == isNight;
  }
}
