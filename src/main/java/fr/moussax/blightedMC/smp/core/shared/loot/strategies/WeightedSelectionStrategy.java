package fr.moussax.blightedMC.smp.core.shared.loot.strategies;

import fr.moussax.blightedMC.smp.core.shared.loot.LootContext;
import fr.moussax.blightedMC.smp.core.shared.loot.LootEntry;
import fr.moussax.blightedMC.smp.core.shared.loot.LootSelectionStrategy;

import java.util.Collections;
import java.util.List;

/**
 * A {@link LootSelectionStrategy} that selects a single loot entry based on weights.
 * The probability of each entry being selected is proportional to its weight.
 */
public final class WeightedSelectionStrategy implements LootSelectionStrategy {

    /**
     * Selects one entry from the valid entries based on their weights.
     *
     * @param validEntries the list of valid loot entries
     * @param context the loot context, used for randomness
     * @return a list containing the selected entry or empty if no valid entries exist
     */
    @Override
    public List<LootEntry> select(List<LootEntry> validEntries, LootContext context) {
        double totalWeight = 0;
        for (LootEntry entry : validEntries) {
            totalWeight += entry.weight();
        }

        double roll = context.random().nextDouble() * totalWeight;
        double accumulated = 0;

        for (LootEntry entry : validEntries) {
            accumulated += entry.weight();
            if (roll <= accumulated) {
                return List.of(entry);
            }
        }

        return validEntries.isEmpty() ? Collections.emptyList() : List.of(validEntries.getLast());
    }
}
