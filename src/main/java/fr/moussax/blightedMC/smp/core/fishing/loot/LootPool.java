package fr.moussax.blightedMC.smp.core.fishing.loot;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class LootPool {
    private final List<LootEntry> entries = new ArrayList<>();
    private LootCondition globalCondition = LootCondition.alwaysTrue();
    private double rollChance = 1.0;
    private double cachedTotalWeight = -1;

    public LootPool add(LootEntry entry) {
        entries.add(entry);
        cachedTotalWeight = -1;
        return this;
    }

    public LootPool add(LootEntry... entries) {
        Collections.addAll(this.entries, entries);
        cachedTotalWeight = -1;
        return this;
    }

    public LootPool add(List<LootEntry> entries) {
        this.entries.addAll(entries);
        cachedTotalWeight = -1;
        return this;
    }

    public LootPool setGlobalCondition(LootCondition condition) {
        this.globalCondition = condition;
        return this;
    }

    public void setRollChance(double chance) {
        this.rollChance = Math.max(0.0, Math.min(1.0, chance));
    }

    public Optional<LootEntry> roll(LootContext context) {
        if (!globalCondition.test(context)) return Optional.empty();
        if (ThreadLocalRandom.current().nextDouble() > rollChance) return Optional.empty();

        List<LootEntry> validEntries = new ArrayList<>();
        for (LootEntry entry : entries) {
            if (entry.meetsCondition(context)) {
                validEntries.add(entry);
            }
        }

        if (validEntries.isEmpty()) return Optional.empty();

        double totalWeight = calculateTotalWeight(validEntries);
        double roll = ThreadLocalRandom.current().nextDouble() * totalWeight;
        double accumulatedWeight = 0;

        for (LootEntry entry : validEntries) {
            accumulatedWeight += entry.getWeight();
            if (roll <= accumulatedWeight) {
                return Optional.of(entry);
            }
        }

        return Optional.of(validEntries.getLast());
    }

    private double calculateTotalWeight(List<LootEntry> validEntries) {
        if (validEntries == entries && cachedTotalWeight >= 0) {
            return cachedTotalWeight;
        }

        double total = 0;
        for (LootEntry entry : validEntries) {
            total += entry.getWeight();
        }

        if (validEntries == entries) {
            cachedTotalWeight = total;
        }

        return total;
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    public int size() {
        return entries.size();
    }
}