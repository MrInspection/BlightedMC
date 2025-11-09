package fr.moussax.blightedMC.core.entities.spawnable;

import org.bukkit.block.Biome;

import java.util.Set;

public final class SpawnConditions {
  private SpawnConditions() {}

  public static SpawnCondition biome(Biome... allowed) {
    Set<Biome> biomeSet = Set.of(allowed);
    return (loc, world) -> biomeSet.contains(loc.getBlock().getBiome());
  }

  public static SpawnCondition minY(int minY) {
    return (loc, world) -> loc.getBlockY() >= minY;
  }

  public static SpawnCondition maxY(int maxY) {
    return (loc, world) -> loc.getBlockY() <= maxY;
  }

  public static SpawnCondition skyExposed() {
    return (loc, world) -> world.getHighestBlockYAt(loc) < loc.getBlockY();
  }

  public static SpawnCondition notInWater() {
    return (loc, world) -> !loc.getBlock().isLiquid();
  }
}
