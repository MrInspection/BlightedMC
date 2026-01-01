package fr.moussax.blightedMC.smp.core.shared.loot;

import java.util.List;

/**
 * Strategy for selecting loot entries from a list of valid candidates.
 *
 * <p>Implementations define how to choose one or more entries based on the
 * provided {@link LootContext} (e.g., weighted or probabilistic selection).</p>
 */
@FunctionalInterface
public interface LootSelectionStrategy {
    /**
     * Selects loot entries from the given list according to the strategy.
     *
     * @param validEntries list of entries eligible for selection
     * @param context context of the loot roll
     * @return selected entries
     */
    List<LootEntry> select(List<LootEntry> validEntries, LootContext context);
}
