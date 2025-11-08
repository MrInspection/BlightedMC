package fr.moussax.blightedMC.core.entities.spawnable;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class SpawnableEntityProfile implements Cloneable {
  private final List<SpawnCondition> conditions = new ArrayList<>();
  private double weight = 1.0;
  private int minGroup = 1, maxGroup = 1;
  private double spawnChance = 1.0;

  @SuppressWarnings("UnusedReturnValue")
  public SpawnableEntityProfile addCondition(SpawnCondition condition) {
    conditions.add(condition);
    return this;
  }

  public SpawnableEntityProfile spawnChance(double chance) {
    this.spawnChance = chance;
    return this;
  }

  public SpawnableEntityProfile weight(double weight) {
    this.weight = weight;
    return this;
  }

  public SpawnableEntityProfile groupSize(int min, int max) {
    this.minGroup = min;
    this.maxGroup = max;
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
  public SpawnableEntityProfile clone() {
    try {
      SpawnableEntityProfile clone = (SpawnableEntityProfile) super.clone();
      clone.conditions.clear();
      clone.conditions.addAll(this.conditions);
      return clone;
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException("Failed to clone SpawnEntitiesProfile", e);
    }
  }
}
