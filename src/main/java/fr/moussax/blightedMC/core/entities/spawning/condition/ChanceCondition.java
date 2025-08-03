package fr.moussax.blightedMC.core.entities.spawning.condition;

import org.bukkit.Location;
import org.bukkit.World;

public class ChanceCondition implements SpawnCondition {
  private final double chance;

  public ChanceCondition(double chance) {
    this.chance = chance;
  }

  @Override
  public boolean canSpawn(Location location, World world) {
    return Math.random() < chance;
  }
}
