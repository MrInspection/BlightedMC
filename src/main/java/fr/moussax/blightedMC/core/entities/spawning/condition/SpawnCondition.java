package fr.moussax.blightedMC.core.entities.spawning.condition;

import org.bukkit.Location;
import org.bukkit.World;

public interface SpawnCondition {
  boolean canSpawn(Location location, World world);
}
