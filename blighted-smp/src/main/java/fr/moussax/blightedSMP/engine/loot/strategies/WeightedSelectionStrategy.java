package fr.moussax.blightedSMP.engine.loot.strategies;

import fr.moussax.blightedSMP.engine.loot.LootContext;
import fr.moussax.blightedSMP.engine.loot.LootEntry;
import fr.moussax.blightedSMP.engine.loot.LootSelectionStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A {@link LootSelectionStrategy} that selects a single loot entry proportionally by weight.
 */
public final class WeightedSelectionStrategy implements LootSelectionStrategy {

    /**
     * Selects one entry from eligible weighted candidates based on relative weight.
     *
     * @param validEntries list of eligible loot entries
     * @param context      loot context providing randomness
     * @return list containing the single selected entry, or an empty list if no weighted entries exist
     */
    @Override
    public List<LootEntry> select(List<LootEntry> validEntries, LootContext context) {
        List<LootEntry.Weighted> weightedEntries = new ArrayList<>();
        for (LootEntry entry : validEntries) {
            if (entry instanceof LootEntry.Weighted weightedEntry) {
                weightedEntries.add(weightedEntry);
            }
        }

        if (weightedEntries.isEmpty()) {
            return Collections.emptyList();
        }

        double totalWeight = 0;
        for (LootEntry.Weighted entry : weightedEntries) {
            totalWeight += entry.weight();
        }

        double roll = context.random().nextDouble() * totalWeight;
        double accumulated = 0;

        for (LootEntry.Weighted entry : weightedEntries) {
            accumulated += entry.weight();
            if (roll <= accumulated) {
                return List.of(entry);
            }
        }

        return List.of(weightedEntries.getLast());
    }
}
