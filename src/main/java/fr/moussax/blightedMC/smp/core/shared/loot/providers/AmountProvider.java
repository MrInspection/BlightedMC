package fr.moussax.blightedMC.smp.core.shared.loot.providers;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Provides the amount of loot to generate for a loot entry.
 *
 * <p>Implementations can provide a fixed value, a range, or a dynamic calculation
 * based on the given {@link ThreadLocalRandom}.</p>
 */
@FunctionalInterface
public interface AmountProvider {

    /**
     * Rolls an amount using the given random generator.
     *
     * @param random the RNG to use
     * @return the rolled amount
     */
    int roll(ThreadLocalRandom random);

    /**
     * Returns a provider that always returns a fixed value.
     *
     * @param value fixed amount
     * @return amount provider
     */
    static AmountProvider fixed(int value) {
        return random -> value;
    }

    /**
     * Returns a provider that generates a random amount in the inclusive range [min, max].
     *
     * @param min minimum value
     * @param max maximum value
     * @return amount provider
     * @throws IllegalArgumentException if min < 0 or max < min
     */
    static AmountProvider range(int min, int max) {
        if (min < 0 || max < min) {
            throw new IllegalArgumentException("Invalid amount range");
        }
        return random -> min + random.nextInt(max - min + 1);
    }
}
