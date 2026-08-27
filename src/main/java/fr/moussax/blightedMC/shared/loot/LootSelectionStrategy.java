package fr.moussax.blightedMC.shared.loot;

import java.util.List;

/**
 * Strategy for selecting eligible entries from a {@link LootTable}.
 */
@FunctionalInterface
public interface LootSelectionStrategy {

    /**
     * Selects one or more entries from a list of valid candidates.
     *
     * @param validEntries list of eligible entries whose conditions passed
     * @param context      loot context
     * @return selected loot entries
     */
    List<LootEntry> select(List<LootEntry> validEntries, LootContext context);
}
