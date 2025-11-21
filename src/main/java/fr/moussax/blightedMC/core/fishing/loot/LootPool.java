package fr.moussax.blightedMC.core.fishing.loot;

import java.util.*;

/**
 * Represents a collection of loot entries with optional conditions and a roll chance.
 * <p>
 * Provides functionality to add entries, set global conditions, and roll for a loot entry
 * based on a context and random chance.
 */
public class LootPool {
    private final List<LootEntry> entries = new ArrayList<>();
    private LootCondition globalCondition = LootCondition.alwaysTrue();
    private double rollChance = 1.0;

    /**
     * Adds a single loot entry to the pool.
     *
     * @param entry the loot entry to add
     * @return this loot pool
     */
    public LootPool add(LootEntry entry) {
        entries.add(entry);
        return this;
    }

    /**
     * Adds multiple loot entries to the pool.
     *
     * @param entries the loot entries to add
     * @return this loot pool
     */
    public LootPool add(LootEntry... entries) {
        this.entries.addAll(Arrays.asList(entries));
        return this;
    }

    /**
     * Adds a list of loot entries to the pool.
     *
     * @param entries the list of loot entries to add
     * @return this loot pool
     */
    public LootPool add(List<LootEntry> entries) {
        this.entries.addAll(entries);
        return this;
    }

    /**
     * Sets a global condition that must be satisfied for any entry to be rolled.
     *
     * @param condition the condition to set
     * @return this loot pool
     */
    public LootPool setGlobalCondition(LootCondition condition) {
        this.globalCondition = condition;
        return this;
    }

    /**
     * Sets the overall probability that a roll will yield any entry.
     *
     * @param chance the roll chance between 0.0 and 1.0
     * @return this loot pool
     */
    @SuppressWarnings("UnusedReturnValue")
    public LootPool setRollChance(double chance) {
        this.rollChance = Math.max(0.0, Math.min(1.0, chance));
        return this;
    }

    /**
     * Rolls for a loot entry using the given randomizer and context.
     *
     * @param randomizer the random generator
     * @param context    the loot context
     * @return an optional containing the selected loot entry, or empty if none selected
     */
    public Optional<LootEntry> roll(Random randomizer, LootContext context) {
        if (!globalCondition.test(context)) return Optional.empty();
        if (randomizer.nextDouble() > rollChance) return Optional.empty();

        List<LootEntry> validEntries = entries.stream()
                .filter(entry -> entry.meetsCondition(context))
                .toList();

        if (validEntries.isEmpty()) return Optional.empty();

        double totalWeight = validEntries.stream().mapToDouble(LootEntry::getWeight).sum();
        double roll = randomizer.nextDouble() * totalWeight;
        double accumulatedWeight = 0;

        for (LootEntry entry : validEntries) {
            accumulatedWeight += entry.getWeight();
            if (roll <= accumulatedWeight) {
                return Optional.of(entry);
            }
        }

        return Optional.of(validEntries.getLast());
    }

    /**
     * Checks if the pool contains no entries.
     *
     * @return true if the pool is empty, false otherwise
     */
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    /**
     * Returns the number of entries in the pool.
     *
     * @return the size of the loot pool
     */
    public int size() {
        return entries.size();
    }
}
