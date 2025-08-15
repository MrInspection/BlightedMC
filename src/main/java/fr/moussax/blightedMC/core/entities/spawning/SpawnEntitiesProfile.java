package fr.moussax.blightedMC.core.entities.spawning;

import fr.moussax.blightedMC.core.entities.spawning.condition.SpawnCondition;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class SpawnEntitiesProfile implements Cloneable {
  private final List<SpawnCondition> conditions = new ArrayList<>();

  public SpawnEntitiesProfile addCondition(SpawnCondition condition) {
    conditions.add(condition);
    return this;
  }

  public boolean canSpawn(Location location, World world) {
    for (SpawnCondition condition : conditions) {
      if (!condition.canSpawn(location, world)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public SpawnEntitiesProfile clone() {
    try {
      SpawnEntitiesProfile clone = (SpawnEntitiesProfile) super.clone();
      clone.conditions.clear();
      clone.conditions.addAll(this.conditions);
      return clone;
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException("Failed to clone SpawnEntitiesProfile", e);
    }
  }
}
