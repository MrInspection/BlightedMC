package fr.moussax.blightedMC.smp.core.shared.loot;

import fr.moussax.blightedMC.smp.core.shared.loot.providers.AmountProvider;

/**
 * Represents a single loot entry with a result, condition, amount, and selection method.
 *
 * <p>An entry can be either probabilistic (dropped based on a chance) or
 * weighted (selected based on relative weight). The entry is only valid if
 * its {@link LootCondition} passes for the given {@link LootContext}.</p>
 */
public final class LootEntry {
    private final LootResult result;
    private final LootCondition condition;
    private final AmountProvider amountProvider;
    private final Double probability;
    private final Double weight;

    private LootEntry(
        LootResult result,
        LootCondition condition,
        AmountProvider amountProvider,
        Double probability,
        Double weight
    ) {
        this.result = result;
        this.condition = condition;
        this.amountProvider = amountProvider;
        this.probability = probability;
        this.weight = weight;
    }

    /**
     * Creates a probabilistic loot entry.
     *
     * @param result the loot result to execute
     * @param probability chance of this loot being selected (0.0-1.0)
     * @param amount the amount provider for this loot
     * @param condition condition that must pass for this entry to be valid
     * @return a new probabilistic loot entry
     */
    public static LootEntry probabilistic(
        LootResult result,
        double probability,
        AmountProvider amount,
        LootCondition condition
    ) {
        return new LootEntry(result, condition, amount, probability, null);
    }

    /**
     * Creates a weighted loot entry.
     *
     * @param result the loot result to execute
     * @param weight relative weight for selection
     * @param amount the amount provider for this loot
     * @param condition condition that must pass for this entry to be valid
     * @return a new weighted loot entry
     */
    public static LootEntry weighted(
        LootResult result,
        double weight,
        AmountProvider amount,
        LootCondition condition
    ) {
        return new LootEntry(result, condition, amount, null, weight);
    }

    /**
     * Checks if this entry is valid under the given context.
     *
     * @param context the loot context
     * @return {@code true} if the condition passes
     */
    public boolean isValid(LootContext context) {
        return condition.test(context);
    }

    /**
     * Rolls the amount for this entry based on the context's RNG.
     *
     * @param context the loot context
     * @return the rolled amount
     */
    public int rollAmount(LootContext context) {
        return amountProvider.roll(context.random());
    }

    /** @return the loot result */
    public LootResult result() {
        return result;
    }

    /** @return the probability if this entry is probabilistic */
    public double probability() {
        if (probability == null) {
            throw new IllegalStateException("Entry is not probabilistic");
        }
        return probability;
    }

    /** @return the weight if this entry is weighted */
    public double weight() {
        if (weight == null) {
            throw new IllegalStateException("Entry is not weighted");
        }
        return weight;
    }

    /** @return true if this entry is probabilistic */
    public boolean isProbabilistic() {
        return probability != null;
    }

    /** @return true if this entry is weighted */
    public boolean isWeighted() {
        return weight != null;
    }
}
