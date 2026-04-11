package fr.moussax.blightedMC.shared.loot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public void execute(LootContext context) {
        List<SelectedLoot> selected = roll(context);
        for (SelectedLoot loot : selected) {
            loot.result().execute(context, loot.amount());
        }
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final List<LootEntry> entries = new ArrayList<>();
        private LootCondition globalCondition = LootCondition.alwaysTrue();
        private LootSelectionStrategy selectionStrategy;
        private double rollChance = 1.0;

        public Builder addEntry(LootEntry entry) {
            entries.add(entry);
            return this;
        }

        public Builder addEntries(LootEntry... entries) {
            Collections.addAll(this.entries, entries);
            return this;
        }

        public Builder addEntries(List<LootEntry> entries) {
            this.entries.addAll(entries);
            return this;
        }

        public Builder globalCondition(LootCondition condition) {
            this.globalCondition = condition;
            return this;
        }

        public Builder selectionStrategy(LootSelectionStrategy strategy) {
            this.selectionStrategy = strategy;
            return this;
        }

        public Builder rollChance(double chance) {
            this.rollChance = Math.clamp(chance, 0.0, 1.0);
            return this;
        }

        public LootTable build() {
            if (selectionStrategy == null) {
                throw new IllegalStateException("Selection strategy must be set");
            }
            return new LootTable(entries, globalCondition, selectionStrategy, rollChance);
        }
    }

    public record SelectedLoot(LootResult result, int amount) {
    }
}
