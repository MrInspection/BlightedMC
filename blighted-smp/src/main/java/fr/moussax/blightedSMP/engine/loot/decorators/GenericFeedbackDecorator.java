package fr.moussax.blightedSMP.engine.loot.decorators;

import fr.moussax.blightedSMP.engine.loot.LootContext;
import fr.moussax.blightedSMP.engine.loot.LootResult;

import java.util.Objects;
import java.util.function.Function;

/**
 * Generic decorator for {@link LootResult} that dispatches visual or audio feedback based on a tier.
 *
 * @param <T> tier type mapped to a feedback specification
 */
public final class GenericFeedbackDecorator<T> implements LootResult {
    private final LootResult delegate;
    private final T tier;
    private final Function<T, FeedbackSpecification> feedbackSupplier;

    /**
     * Constructs a generic feedback decorator.
     *
     * @param delegate         underlying loot result to execute
     * @param tier             feedback tier
     * @param feedbackSupplier function mapping tier to feedback specification
     */
    public GenericFeedbackDecorator(LootResult delegate, T tier, Function<T, FeedbackSpecification> feedbackSupplier) {
        this.delegate = Objects.requireNonNull(delegate, "delegate cannot be null");
        this.tier = tier;
        this.feedbackSupplier = Objects.requireNonNull(feedbackSupplier, "feedbackSupplier cannot be null");
    }

    /**
     * Executes the underlying loot result and dispatches configured feedback to the player.
     *
     * @param context loot context
     * @param amount  drop quantity
     */
    @Override
    public void execute(LootContext context, int amount) {
        delegate.execute(context, amount);

        if (context.blightedPlayer() == null || tier == null) {
            return;
        }

        FeedbackSpecification specification = feedbackSupplier.apply(tier);
        if (specification == null) {
            return;
        }

        if (specification.customMessage() != null) {
            context.blightedPlayer().getPlayer().sendMessage(specification.customMessage());
        }

        if (specification.messagePrefix() != null) {
            context.blightedPlayer().getPlayer().sendMessage(specification.messagePrefix() + delegate.displayName(amount));
        }

        if (specification.sound() != null) {
            context.blightedPlayer().getPlayer().playSound(
                    context.blightedPlayer().getPlayer().getLocation(),
                    specification.sound(),
                    1.0f,
                    specification.pitch()
            );
        }
    }

    /**
     * Returns the formatted display name of the delegated loot result.
     *
     * @param amount drop quantity
     * @return delegated display name
     */
    @Override
    public String displayName(int amount) {
        return delegate.displayName(amount);
    }
}
