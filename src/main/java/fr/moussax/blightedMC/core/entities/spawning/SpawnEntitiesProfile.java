package fr.moussax.blightedMC.core.entities.spawning;

import fr.moussax.blightedMC.core.entities.spawning.condition.SpawnCondition;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class SpawnEntitiesProfile {
  private final List<SpawnCondition> conditions = new ArrayList<>();

  public SpawnEntitiesProfile addCondition(SpawnCondition condition) {
    conditions.add(condition);
    return this;
  }

  public boolean canSpawn(Location location, World world) {
    for(SpawnCondition condition : conditions) {
      if(!condition.canSpawn(location, world)) {
        return false;
      }
    }
    return true;
  }
}
