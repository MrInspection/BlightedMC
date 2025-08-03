package fr.moussax.blightedMC.core.entities.spawning.condition;

import org.bukkit.Location;
import org.bukkit.World;

public class WeatherCondition implements SpawnCondition {
  public enum WeatherType {
    CLEAR,
    RAIN,
    THUNDERSTORM
  }

  private final WeatherType requiredWeather;

  public WeatherCondition(WeatherType requiredWeather) {
    this.requiredWeather = requiredWeather;
  }

  @Override
  public boolean canSpawn(Location location, World world) {
    switch (requiredWeather) {
      case CLEAR -> {
        return !world.hasStorm() && !world.isThundering();
      }
      case RAIN -> {
        return world.hasStorm() && !world.isThundering();
      }
      case THUNDERSTORM -> {
        return world.isThundering();
      }
      default -> {
        return false;
      }
    }
  }
}
