package fr.moussax.blightedMC.smp.core.shared.loot;

/**
 * A decorator for a {@link LootResult} that adds feedback behavior
 * (e.g., messages or sounds) while delegating the core execution.
 *
 * <p>Implementations should call {@link #delegate()} to perform the
 * underlying loot action and then apply additional feedback.</p>
 */
public interface FeedbackDecorator extends LootResult {

    /**
     * Returns the underlying {@link LootResult} being decorated.
     *
     * @return the delegated loot result
     */
    LootResult delegate();
}
