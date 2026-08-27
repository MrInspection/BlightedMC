package fr.moussax.blightedMC.shared.loot.providers;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Provider determining the quantity of loot items to generate.
 */
@FunctionalInterface
public interface AmountProvider {

    /**
     * Rolls a drop quantity using the specified random generator.
     *
     * @param random random generator
     * @return rolled drop quantity
     */
    int roll(ThreadLocalRandom random);

    /**
     * Returns a provider that always yields a fixed quantity.
     *
     * @param value fixed quantity
     * @return fixed amount provider
     */
    static AmountProvider fixed(int value) {
        return random -> value;
    }

    /**
     * Returns a provider that generates a random quantity in an inclusive range.
     *
     * @param minimum minimum quantity
     * @param maximum maximum quantity
     * @return range amount provider
     * @throws IllegalArgumentException if {@code minimum < 0} or {@code maximum < minimum}
     */
    static AmountProvider range(int minimum, int maximum) {
        if (minimum < 0 || maximum < minimum) {
            throw new IllegalArgumentException("Invalid amount range: minimum=" + minimum + ", maximum=" + maximum);
        }
        return random -> minimum + random.nextInt(maximum - minimum + 1);
    }
}
