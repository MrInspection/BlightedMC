package fr.moussax.blightedMC.core.entities.spawning.condition;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;

import java.util.Set;

/**
 * SpawnCondition implementation that restricts spawning to specified biomes.
 */
public class BiomeCondition implements SpawnCondition {
  private final Set<Biome> allowedBiomes;

  /**
   * Constructs a BiomeCondition with a set of allowed biomes.
   *
   * @param allowedBiomes the biomes in which spawning is permitted
   */
  public BiomeCondition(Set<Biome> allowedBiomes) {
    this.allowedBiomes = allowedBiomes;
  }

  @Override
  public boolean canSpawn(Location location, World world) {
    return allowedBiomes.contains(location.getBlock().getBiome());
  }
}
