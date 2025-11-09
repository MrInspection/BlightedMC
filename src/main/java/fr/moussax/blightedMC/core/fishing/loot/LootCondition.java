package fr.moussax.blightedMC.core.fishing.loot;

import org.bukkit.World;
import org.bukkit.block.Biome;

import java.util.Collections;
import java.util.Set;

public class LootCondition {
  private final Set<Biome> allowedBiomes;
  private final Set<World.Environment> allowedEnvironments;

  public LootCondition(Set<Biome> biomes, Set<World.Environment> environments) {
    this.allowedBiomes = biomes != null ? biomes : Collections.emptySet();
    this.allowedEnvironments = environments != null ? environments : Collections.emptySet();
  }

  public boolean test(LootContext ctx) {
    boolean isValidBiome = allowedBiomes.isEmpty() || allowedBiomes.contains(ctx.biome());
    boolean isValidEnvironment = allowedEnvironments.isEmpty() || allowedEnvironments.contains(ctx.environment());
    return isValidBiome && isValidEnvironment;
  }

  public static LootCondition alwaysTrue() {
    return new LootCondition(null, null);
  }

  public static LootCondition environment(World.Environment environment) {
    return new LootCondition(null, Collections.singleton(environment));
  }

  public static LootCondition biome(Biome biome) {
    return new LootCondition(Collections.singleton(biome), null);
  }
}
