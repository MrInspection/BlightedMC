package fr.moussax.blightedMC.core.entities.spawning.condition;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;

import java.util.Set;

public class BiomeCondition implements SpawnCondition {
  private final Set<Biome> allowedBiomes;

  public BiomeCondition(Set<Biome> allowedBiomes) {
    this.allowedBiomes = allowedBiomes;
  }

  @Override
  public boolean canSpawn(Location location, World world) {
    return allowedBiomes.contains(location.getBlock().getBiome());
  }
}
