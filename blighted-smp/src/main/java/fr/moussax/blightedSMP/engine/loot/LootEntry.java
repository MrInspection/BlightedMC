package fr.moussax.blightedSMP.engine.loot;

import fr.moussax.blightedSMP.engine.loot.providers.AmountProvider;

import java.util.Objects;

/**
 * Entry in a {@link LootTable} defining a result, eligibility condition, amount provider, and selection mechanics.
 *
 * <p>Entries are sealed as either {@link Probabilistic} (evaluated by drop chance)
 * or {@link Weighted} (evaluated by relative selection weight).</p>
 */
public sealed interface LootEntry permits LootEntry.Probabilistic, LootEntry.Weighted {

    /**
     * Returns the loot result to execute when selected.
     *
     * @return loot result
     */
    LootResult result();

    /**
     * Returns the condition required for entry eligibility.
     *
     * @return eligibility condition
     */
    LootCondition condition();

    /**
     * Returns the provider for rolling drop quantities.
     *
     * @return amount provider
     */
    AmountProvider amountProvider();

    /**
     * Checks if this entry is eligible for selection under the given context.
     *
     * @param context loot context
     * @return {@code true} if condition passes
     */
    default boolean isValid(LootContext context) {
        return condition().test(context);
    }

    /**
     * Rolls the drop quantity for this entry.
     *
     * @param context loot context
     * @return rolled drop quantity
     */
    default int rollAmount(LootContext context) {
        return amountProvider().roll(context.random());
    }

    /**
     * A probabilistic loot entry evaluated by individual drop chance.
     *
     * @param result         loot result to execute
     * @param condition      condition for eligibility
     * @param amountProvider provider for drop quantity
     * @param probability    chance of selection (0.0 to 1.0)
     */
    record Probabilistic(
            LootResult result,
            LootCondition condition,
            AmountProvider amountProvider,
            double probability
    ) implements LootEntry {
        public Probabilistic {
            Objects.requireNonNull(result, "result cannot be null");
            Objects.requireNonNull(condition, "condition cannot be null");
            Objects.requireNonNull(amountProvider, "amountProvider cannot be null");
        }
    }

    /**
     * A weighted loot entry evaluated relative to other weighted entries.
     *
     * @param result         loot result to execute
     * @param condition      condition for eligibility
     * @param amountProvider provider for drop quantity
     * @param weight         relative selection weight
     */
    record Weighted(
            LootResult result,
            LootCondition condition,
            AmountProvider amountProvider,
            double weight
    ) implements LootEntry {
        public Weighted {
            Objects.requireNonNull(result, "result cannot be null");
            Objects.requireNonNull(condition, "condition cannot be null");
            Objects.requireNonNull(amountProvider, "amountProvider cannot be null");
        }
    }

    /**
     * Creates a probabilistic loot entry.
     *
     * @param result      loot result to execute
     * @param probability selection chance (0.0 to 1.0)
     * @param amount      provider for drop quantity
     * @param condition   condition for eligibility
     * @return new probabilistic entry
     */
    static LootEntry.Probabilistic probabilistic(
            LootResult result,
            double probability,
            AmountProvider amount,
            LootCondition condition
    ) {
        return new Probabilistic(result, condition, amount, probability);
    }

    /**
     * Creates a weighted loot entry.
     *
     * @param result    loot result to execute
     * @param weight    relative selection weight
     * @param amount    provider for drop quantity
     * @param condition condition for eligibility
     * @return new weighted entry
     */
    static LootEntry.Weighted weighted(
            LootResult result,
            double weight,
            AmountProvider amount,
            LootCondition condition
    ) {
        return new Weighted(result, condition, amount, weight);
    }
}
