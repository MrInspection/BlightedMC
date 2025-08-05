package fr.moussax.blightedMC.core.entities.spawning;

import fr.moussax.blightedMC.core.entities.spawning.condition.SpawnCondition;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a profile containing multiple spawn conditions that must be met for entities to spawn.
 * <p>
 * Conditions can be added and evaluated collectively to determine spawn eligibility at a given location and world.
 */
public class SpawnEntitiesProfile {
  private final List<SpawnCondition> conditions = new ArrayList<>();

  /**
   * Adds a spawn condition to this profile.
   *
   * @param condition the spawn condition to add
   * @return this profile instance for method chaining
   */
  public SpawnEntitiesProfile addCondition(SpawnCondition condition) {
    conditions.add(condition);
    return this;
  }

  /**
   * Checks if entities can spawn at the specified location and world based on all added conditions.
   *
   * @param location the location to check spawning
   * @param world the world in which spawning is checked
   * @return true if all conditions allow spawning, false otherwise
   */
  public boolean canSpawn(Location location, World world) {
    for(SpawnCondition condition : conditions) {
      if(!condition.canSpawn(location, world)) {
        return false;
      }
    }
    return true;
  }
}
