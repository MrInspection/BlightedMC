package fr.moussax.blightedMC.smp.core.shared.loot;

import java.util.*;

/**
 * Represents a table of loot entries that can be rolled and executed.
 *
 * <p>The table applies a global condition, a roll chance, and a
 * {@link LootSelectionStrategy} to determine which entries are selected.
 * Selected entries are then executed via their {@link LootResult}.</p>
 */
public final class LootTable {
    private final List<LootEntry> entries;
    private final LootCondition globalCondition;
    private final LootSelectionStrategy selectionStrategy;
    private final double rollChance;

    private LootTable(
        List<LootEntry> entries,
        LootCondition globalCondition,
        LootSelectionStrategy selectionStrategy,
        double rollChance
    ) {
        this.entries = List.copyOf(entries);
        this.globalCondition = globalCondition;
        this.selectionStrategy = selectionStrategy;
        this.rollChance = rollChance;
    }

    /**
     * Rolls the loot table for the given context, returning the selected loot
     * with rolled amounts.
     *
     * @param context the loot context
     * @return list of selected loot, empty if none selected
     */
    public List<SelectedLoot> roll(LootContext context) {
        if (!globalCondition.test(context)) {
            return Collections.emptyList();
        }

        if (context.random().nextDouble() > rollChance) {
            return Collections.emptyList();
        }

        List<LootEntry> validEntries = entries.stream()
            .filter(entry -> entry.isValid(context))
            .toList();

        if (validEntries.isEmpty()) {
            return Collections.emptyList();
        }

        List<LootEntry> selectedEntries = selectionStrategy.select(validEntries, context);
        List<SelectedLoot> results = new ArrayList<>();

        for (LootEntry entry : selectedEntries) {
            int amount = entry.rollAmount(context);
            results.add(new SelectedLoot(entry.result(), amount));
        }

        return results;
    }

    /**
     * Rolls and executes all selected loot for the given context.
     *
     * @param context the loot context
     */
    public void execute(LootContext context) {
        List<SelectedLoot> selected = roll(context);
        for (SelectedLoot loot : selected) {
            loot.result().execute(context, loot.amount());
        }
    }

    /** @return true if the table contains no entries */
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    /** @return a new builder for constructing a LootTable */
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final List<LootEntry> entries = new ArrayList<>();
        private LootCondition globalCondition = LootCondition.alwaysTrue();
        private LootSelectionStrategy selectionStrategy;
        private double rollChance = 1.0;

        /** Adds a single loot entry to the table. */
        public Builder addEntry(LootEntry entry) {
            entries.add(entry);
            return this;
        }

        /** Adds multiple loot entries to the table. */
        public Builder addEntries(LootEntry... entries) {
            Collections.addAll(this.entries, entries);
            return this;
        }

        /** Adds a list of loot entries to the table. */
        public Builder addEntries(List<LootEntry> entries) {
            this.entries.addAll(entries);
            return this;
        }

        /** Sets the global condition applied to all entries. */
        public Builder globalCondition(LootCondition condition) {
            this.globalCondition = condition;
            return this;
        }

        /** Sets the selection strategy for this table. */
        public Builder selectionStrategy(LootSelectionStrategy strategy) {
            this.selectionStrategy = strategy;
            return this;
        }

        /** Sets the overall chance that the table will completely roll (0.0-1.0). */
        public Builder rollChance(double chance) {
            this.rollChance = Math.max(0.0, Math.min(1.0, chance));
            return this;
        }

        /** Builds the {@link LootTable}. Must have a selection strategy. */
        public LootTable build() {
            if (selectionStrategy == null) {
                throw new IllegalStateException("Selection strategy must be set");
            }
            return new LootTable(entries, globalCondition, selectionStrategy, rollChance);
        }
    }

    /**
     * Represents a single selected loot result with its rolled amount.
     *
     * @param result the loot result
     * @param amount the amount rolled
     */
    public record SelectedLoot(LootResult result, int amount) {}
}
