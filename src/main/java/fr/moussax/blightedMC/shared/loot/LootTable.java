package fr.moussax.blightedMC.shared.loot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Collection of {@link LootEntry} items evaluated according to a global condition and selection strategy.
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
     * Evaluates and selects loot entries without executing them.
     *
     * @param context loot context
     * @return selected results with rolled quantities, or an empty list if roll fails or no entries pass
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
     * Rolls and executes all selected loot results.
     *
     * @param context loot context
     */
    public void execute(LootContext context) {
        List<SelectedLoot> selected = roll(context);
        for (SelectedLoot loot : selected) {
            loot.result().execute(context, loot.amount());
        }
    }

    /**
     * Checks if this table contains zero entries.
     *
     * @return {@code true} if empty
     */
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    /**
     * Returns a new builder for configuring a loot table.
     *
     * @return loot table builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Fluent builder for constructing {@link LootTable} instances.
     */
    public static final class Builder {
        private final List<LootEntry> entries = new ArrayList<>();
        private LootCondition globalCondition = LootCondition.alwaysTrue();
        private LootSelectionStrategy selectionStrategy;
        private double rollChance = 1.0;

        /**
         * Adds a loot entry to the table.
         *
         * @param entry entry to add
         * @return this builder
         */
        public Builder addEntry(LootEntry entry) {
            entries.add(entry);
            return this;
        }

        /**
         * Adds multiple loot entries to the table.
         *
         * @param entries entries to add
         * @return this builder
         */
        public Builder addEntries(LootEntry... entries) {
            Collections.addAll(this.entries, entries);
            return this;
        }

        /**
         * Adds a list of loot entries to the table.
         *
         * @param entries list of entries to add
         * @return this builder
         */
        public Builder addEntries(List<LootEntry> entries) {
            this.entries.addAll(entries);
            return this;
        }

        /**
         * Sets the global condition evaluated before individual entries.
         *
         * @param condition global condition
         * @return this builder
         */
        public Builder globalCondition(LootCondition condition) {
            this.globalCondition = condition;
            return this;
        }

        /**
         * Sets the selection strategy used to choose eligible entries.
         *
         * @param strategy selection strategy
         * @return this builder
         */
        public Builder selectionStrategy(LootSelectionStrategy strategy) {
            this.selectionStrategy = strategy;
            return this;
        }

        /**
         * Sets the overall chance of rolling this table (clamped between 0.0 and 1.0).
         *
         * @param chance roll probability
         * @return this builder
         */
        public Builder rollChance(double chance) {
            this.rollChance = Math.clamp(chance, 0.0, 1.0);
            return this;
        }

        /**
         * Constructs the configured {@link LootTable}.
         *
         * @return new loot table
         * @throws IllegalStateException if selection strategy was not set
         */
        public LootTable build() {
            if (selectionStrategy == null) {
                throw new IllegalStateException("Selection strategy must be set");
            }
            return new LootTable(entries, globalCondition, selectionStrategy, rollChance);
        }
    }

    /**
     * Selected result pair containing the result outcome and rolled quantity.
     *
     * @param result selected loot result
     * @param amount rolled drop quantity
     */
    public record SelectedLoot(LootResult result, int amount) {
    }
}
