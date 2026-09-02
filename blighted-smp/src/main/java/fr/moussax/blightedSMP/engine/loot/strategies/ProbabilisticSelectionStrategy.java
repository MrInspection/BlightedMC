package fr.moussax.blightedSMP.engine.loot.strategies;

import fr.moussax.blightedSMP.engine.loot.LootContext;
import fr.moussax.blightedSMP.engine.loot.LootEntry;
import fr.moussax.blightedSMP.engine.loot.LootSelectionStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link LootSelectionStrategy} that selects loot entries based on individual drop probabilities.
 */
public final class ProbabilisticSelectionStrategy implements LootSelectionStrategy {
    private final int maxDrops;

    /**
     * Constructs a probabilistic selection strategy with a drop count cap.
     *
     * @param maxDrops maximum number of entries to select
     */
    public ProbabilisticSelectionStrategy(int maxDrops) {
        this.maxDrops = maxDrops;
    }

    /**
     * Selects probabilistic entries based on context RNG, capped at maximum drops.
     *
     * @param validEntries list of eligible loot entries
     * @param context      loot context providing randomness
     * @return selected loot entries capped at maximum drops
     */
    @Override
    public List<LootEntry> select(List<LootEntry> validEntries, LootContext context) {
        List<LootEntry> selected = new ArrayList<>();

        for (LootEntry entry : validEntries) {
            if (entry instanceof LootEntry.Probabilistic probabilisticEntry) {
                if (context.random().nextDouble() <= probabilisticEntry.probability()) {
                    selected.add(entry);
                }
            }
        }

        return SelectionCapper.capToMaxDrops(selected, maxDrops, context.random());
    }
}
