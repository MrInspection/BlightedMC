package fr.moussax.blightedMC.smp.core.shared.loot;

import org.bukkit.World;
import org.bukkit.block.Biome;

/**
 * Represents a condition that determines whether a loot entry is eligible.
 *
 * <p>Conditions are evaluated against a {@link LootContext} and can be
 * combined to restrict loot by biome, world environment, or other criteria.</p>
 */
@FunctionalInterface
public interface LootCondition {

    /**
     * Tests whether the given context satisfies this condition.
     *
     * @param ctx the loot context
     * @return {@code true} if the condition passes, {@code false} otherwise
     */
    boolean test(LootContext ctx);

    /** Always returns {@code true}. */
    static LootCondition alwaysTrue() {
        return ctx -> true;
    }

    /**
     * Returns a condition that passes only in the specified biome.
     *
     * @param biome the allowed biome
     * @return a new condition
     */
    static LootCondition biome(Biome biome) {
        return context -> context.biome() == biome;
    }

    /**
     * Returns a condition that passes only in the specified world environment.
     *
     * @param environment the allowed environment
     * @return a new condition
     */
    static LootCondition environment(World.Environment environment) {
        return context -> context.world().getEnvironment() == environment;
    }
}
