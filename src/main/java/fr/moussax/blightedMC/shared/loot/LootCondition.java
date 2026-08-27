package fr.moussax.blightedMC.shared.loot;

import org.bukkit.World;
import org.bukkit.block.Biome;

/**
 * Condition determining whether a loot entry is eligible for selection.
 */
@FunctionalInterface
public interface LootCondition {

    /**
     * Evaluates whether the loot condition passes for the given context.
     *
     * @param context loot context
     * @return {@code true} if the condition passes
     */
    boolean test(LootContext context);

    /**
     * Returns a condition that always evaluates to {@code true}.
     *
     * @return unconditional condition
     */
    static LootCondition alwaysTrue() {
        return context -> true;
    }

    /**
     * Returns a condition matching a specific biome.
     *
     * @param biome required biome
     * @return biome condition
     */
    static LootCondition biome(Biome biome) {
        return context -> context.biome() == biome;
    }

    /**
     * Returns a condition matching a specific world environment.
     *
     * @param environment required world environment
     * @return environment condition
     */
    static LootCondition environment(World.Environment environment) {
        return context -> context.world().getEnvironment() == environment;
    }

    /**
     * Returns a condition matching players at or below a maximum Y-coordinate.
     *
     * @param maximumY maximum allowed Y-coordinate (inclusive)
     * @return Y-coordinate condition
     */
    static LootCondition atMostY(int maximumY) {
        return context -> {
            if (context.blightedPlayer() == null || context.blightedPlayer().getPlayer() == null) {
                return false;
            }
            return context.blightedPlayer().getPlayer().getLocation().getY() <= maximumY;
        };
    }
}
