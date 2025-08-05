package fr.moussax.blightedMC.core.entities.spawning.condition;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * SpawnCondition that restricts spawning based on the time of day.
 */
public class TimeCondition implements SpawnCondition {
  private final boolean onlyNightTime;

  /**
   * Constructs a TimeCondition specifying whether spawning is allowed only at night.
   *
   * @param onlyNightTime true to allow spawning only during night time, false for day time
   */
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
