package fr.moussax.blightedMC.smp.core.shared.loot.strategies;

import fr.moussax.blightedMC.smp.core.shared.loot.LootContext;
import fr.moussax.blightedMC.smp.core.shared.loot.LootEntry;
import fr.moussax.blightedMC.smp.core.shared.loot.LootSelectionStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A {@link LootSelectionStrategy} that selects loot entries based on their individual probabilities.
 * At most {@code maxDrops} entries can be selected.
 */
public final class ProbabilisticSelectionStrategy implements LootSelectionStrategy {
    private final int maxDrops;

    /**
     * Constructs a probabilistic selection strategy.
     *
     * @param maxDrops the maximum number of entries to select
     */
    public ProbabilisticSelectionStrategy(int maxDrops) {
        this.maxDrops = maxDrops;
    }

    /**
     * Selects entries from the valid list based on their probability.
     *
     * @param validEntries the list of valid loot entries
     * @param context the loot context, providing randomness
     * @return a list of selected entries, up to {@code maxDrops} in size
     */
    @Override
    public List<LootEntry> select(List<LootEntry> validEntries, LootContext context) {
        List<LootEntry> selected = new ArrayList<>();

        for (LootEntry entry : validEntries) {
            if (context.random().nextDouble() <= entry.probability()) {
                selected.add(entry);
            }
        }

        if (selected.size() > maxDrops) {
            Collections.shuffle(selected, context.random());
            return selected.subList(0, maxDrops);
        }

        return selected;
    }
}
