package fr.moussax.blightedMC.shared.loot.strategies;

import fr.moussax.blightedMC.shared.loot.LootEntry;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Utility capping selection lists to a maximum drop limit.
 */
final class SelectionCapper {

    private SelectionCapper() {
    }

    /**
     * Caps selected entries to {@code maxDrops} after shuffling randomly.
     *
     * @param selected list of selected entries
     * @param maxDrops maximum allowed drops
     * @param random   random number generator
     * @return capped list of selected entries
     */
    static List<LootEntry> capToMaxDrops(List<LootEntry> selected, int maxDrops, ThreadLocalRandom random) {
        return capToMaxDrops(selected, maxDrops, random, null);
    }

    /**
     * Caps selected entries to {@code maxDrops} after shuffling randomly and optionally sorting.
     *
     * @param selected   list of selected entries
     * @param maxDrops   maximum allowed drops
     * @param random     random number generator
     * @param comparator optional comparator to order entries post-shuffle
     * @return capped list of selected entries
     */
    static List<LootEntry> capToMaxDrops(
            List<LootEntry> selected,
            int maxDrops,
            ThreadLocalRandom random,
            Comparator<LootEntry> comparator
    ) {
        if (selected.size() <= maxDrops) {
            return selected;
        }

        Collections.shuffle(selected, random);
        if (comparator != null) {
            selected.sort(comparator);
        }

        return selected.subList(0, maxDrops);
    }
}
