package fr.moussax.blightedMC.smp.core.fishing.loot;

import org.bukkit.World;
import org.bukkit.block.Biome;

import java.util.Collections;
import java.util.Set;

/**
 * Represents conditions under which a loot entry can be applied.
 * <p>
 * Conditions can be based on allowed biomes and world environments.
 */
public record LootCondition(Set<Biome> allowedBiomes, Set<World.Environment> allowedEnvironments) {

    /**
     * Constructs a LootCondition with the specified allowed biomes and environments.
     * Null values are treated as empty sets.
     *
     * @param allowedBiomes       the set of allowed biomes, or null
     * @param allowedEnvironments the set of allowed environments, or null
     */
    public LootCondition(Set<Biome> allowedBiomes, Set<World.Environment> allowedEnvironments) {
        this.allowedBiomes = allowedBiomes != null ? allowedBiomes : Collections.emptySet();
        this.allowedEnvironments = allowedEnvironments != null ? allowedEnvironments : Collections.emptySet();
    }

    /**
     * Tests whether the given loot context satisfies this condition.
     *
     * @param ctx the loot context
     * @return true if the context matches the condition, false otherwise
     */
    public boolean test(LootContext ctx) {
        boolean isValidBiome = allowedBiomes.isEmpty() || allowedBiomes.contains(ctx.biome());
        boolean isValidEnvironment = allowedEnvironments.isEmpty() || allowedEnvironments.contains(ctx.environment());
        return isValidBiome && isValidEnvironment;
    }

    /**
     * Returns a LootCondition that always evaluates to true.
     */
    public static LootCondition alwaysTrue() {
        return new LootCondition(null, null);
    }

    /**
     * Returns a LootCondition restricting to a specific world environment.
     *
     * @param environment the environment to allow
     * @return a new LootCondition for the environment
     */
    public static LootCondition environment(World.Environment environment) {
        return new LootCondition(null, Collections.singleton(environment));
    }

    /**
     * Returns a LootCondition restricting to a specific biome.
     *
     * @param biome the biome to allow
     * @return a new LootCondition for the biome
     */
    public static LootCondition biome(Biome biome) {
        return new LootCondition(Collections.singleton(biome), null);
    }
}
