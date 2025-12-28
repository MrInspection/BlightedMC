package fr.moussax.blightedMC.smp.core.shared.loot.strategies;

import fr.moussax.blightedMC.smp.core.shared.loot.LootContext;
import fr.moussax.blightedMC.smp.core.shared.loot.LootEntry;
import fr.moussax.blightedMC.smp.core.shared.loot.LootSelectionStrategy;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A probabilistic {@link LootSelectionStrategy} that considers the player's looting level.
 * Each entry's probability is adjusted based on the looting enchantment of the main-hand weapon.
 * The number of selected entries is capped by {@code maxDrops}.
 */
public final class LootingAwareProbabilisticStrategy implements LootSelectionStrategy {
    private final int maxDrops;

    public LootingAwareProbabilisticStrategy(int maxDrops) {
        this.maxDrops = maxDrops;
    }

    /**
     * Selects loot entries probabilistically, adjusting each entry's chance based on looting level.
     *
     * @param validEntries the list of valid entries after condition checks
     * @param context the loot context, used for randomness and player looting level
     * @return a list of selected loot entries
     */
    @Override
    public List<LootEntry> select(List<LootEntry> validEntries, LootContext context) {
        int lootingLevel = extractLootingLevel(context);
        List<LootEntry> selected = new ArrayList<>();

        for (LootEntry entry : validEntries) {
            double adjustedProbability = adjustForLooting(entry.probability(), lootingLevel);
            if (context.random().nextDouble() <= adjustedProbability) {
                selected.add(entry);
            }
        }

        if (selected.size() > maxDrops) {
            Collections.shuffle(selected, context.random());
            return selected.subList(0, maxDrops);
        }

        return selected;
    }

    /**
     * Extracts the looting level from the player's main-hand weapon.
     *
     * @param context the loot context
     * @return looting level (0 if player or weapon is null)
     */
    private int extractLootingLevel(LootContext context) {
        if (context.player() == null || context.player().getPlayer() == null) {
            return 0;
        }

        ItemStack weapon = context.player().getPlayer().getInventory().getItemInMainHand();
        if (weapon.hasItemMeta() && Objects.requireNonNull(weapon.getItemMeta()).hasEnchant(Enchantment.LOOTING)) {
            return weapon.getItemMeta().getEnchantLevel(Enchantment.LOOTING);
        }

        return 0;
    }

    /**
     * Adjusts the base probability by the looting level.
     *
     * @param baseProbability the base probability of the entry
     * @param lootingLevel the player's looting level
     * @return adjusted probability, capped at 1.0
     */
    private double adjustForLooting(double baseProbability, int lootingLevel) {
        return Math.min(1.0, baseProbability * (1.0 + lootingLevel * 0.1));
    }
}
