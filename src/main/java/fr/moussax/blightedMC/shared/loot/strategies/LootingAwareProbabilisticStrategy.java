package fr.moussax.blightedMC.shared.loot.strategies;

import fr.moussax.blightedMC.shared.loot.LootContext;
import fr.moussax.blightedMC.shared.loot.LootEntry;
import fr.moussax.blightedMC.shared.loot.LootSelectionStrategy;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * A probabilistic {@link LootSelectionStrategy} that adjusts drop chances using the player's looting level.
 */
public final class LootingAwareProbabilisticStrategy implements LootSelectionStrategy {
    private final int maxDrops;

    /**
     * Constructs a looting-aware probabilistic selection strategy.
     *
     * @param maxDrops maximum number of entries to select
     * @throws IllegalArgumentException if {@code maxDrops <= 0}
     */
    public LootingAwareProbabilisticStrategy(int maxDrops) {
        if (maxDrops <= 0) {
            throw new IllegalArgumentException("maxDrops must be positive, got: " + maxDrops);
        }
        this.maxDrops = maxDrops;
    }

    /**
     * Selects loot entries probabilistically, adjusting probabilities by weapon looting level.
     *
     * @param validEntries list of eligible entries
     * @param context      loot context containing player and RNG state
     * @return selected loot entries capped at maximum drops
     */
    @Override
    public List<LootEntry> select(List<LootEntry> validEntries, LootContext context) {
        int lootingLevel = extractLootingLevel(context);
        List<LootEntry> selected = new ArrayList<>();

        for (LootEntry entry : validEntries) {
            if (!(entry instanceof LootEntry.Probabilistic probabilisticEntry)) {
                selected.add(entry);
                continue;
            }
            double adjustedProbability = adjustForLooting(probabilisticEntry.probability(), lootingLevel);
            if (context.random().nextDouble() <= adjustedProbability) {
                selected.add(entry);
            }
        }

        return SelectionCapper.capToMaxDrops(selected, maxDrops, context.random(), (firstEntry, secondEntry) -> {
            double firstProbability = firstEntry instanceof LootEntry.Probabilistic probabilisticFirst ? probabilisticFirst.probability() : 1.0;
            double secondProbability = secondEntry instanceof LootEntry.Probabilistic probabilisticSecond ? probabilisticSecond.probability() : 1.0;
            return Double.compare(secondProbability, firstProbability);
        });
    }

    private int extractLootingLevel(LootContext context) {
        if (context.blightedPlayer() == null || context.blightedPlayer().getPlayer() == null) {
            return 0;
        }

        ItemStack weapon = context.blightedPlayer().getPlayer().getInventory().getItemInMainHand();
        return weapon.getEnchantmentLevel(Enchantment.LOOTING);
    }

    private double adjustForLooting(double baseProbability, int lootingLevel) {
        return Math.min(1.0, baseProbability * (1.0 + lootingLevel * 0.1));
    }
}
