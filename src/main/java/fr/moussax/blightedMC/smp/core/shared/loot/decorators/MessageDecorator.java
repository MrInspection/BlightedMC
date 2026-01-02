package fr.moussax.blightedMC.smp.core.shared.loot.decorators;

import fr.moussax.blightedMC.smp.core.shared.loot.FeedbackDecorator;
import fr.moussax.blightedMC.smp.core.shared.loot.LootContext;
import fr.moussax.blightedMC.smp.core.shared.loot.LootResult;

/**
 * A {@link FeedbackDecorator} that sends a chat message to the player
 * when the loot is executed, then delegates to the underlying {@link LootResult}.
 */
public final class MessageDecorator implements FeedbackDecorator {
    private final LootResult delegate;
    private final String message;

    /**
     * Constructs a message decorator.
     *
     * @param delegate the underlying loot result to execute
     * @param message the message to send to the player
     */
    public MessageDecorator(LootResult delegate, String message) {
        this.delegate = delegate;
        this.message = message;
    }

    /**
     * Sends the message to the player (if present) and executes the delegated loot.
     *
     * @param context the loot context
     * @param amount the loot amount
     */
    @Override
    public void execute(LootContext context, int amount) {
        if (context.blightedPlayer() != null && context.blightedPlayer().getPlayer() != null) {
            context.blightedPlayer().getPlayer().sendMessage(message);
        }
        delegate.execute(context, amount);
    }

    /** Delegates display name retrieval to the underlying loot result. */
    @Override
    public String displayName(int amount) {
        return delegate.displayName(amount);
    }

    /** Returns the underlying loot result. */
    @Override
    public LootResult delegate() {
        return delegate;
    }
}
