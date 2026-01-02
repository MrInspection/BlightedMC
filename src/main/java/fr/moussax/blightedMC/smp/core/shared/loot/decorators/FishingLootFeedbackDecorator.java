package fr.moussax.blightedMC.smp.core.shared.loot.decorators;

import fr.moussax.blightedMC.smp.core.shared.loot.FeedbackDecorator;
import fr.moussax.blightedMC.smp.core.shared.loot.LootContext;
import fr.moussax.blightedMC.smp.core.shared.loot.LootResult;
import org.bukkit.Sound;

/**
 * A {@link FeedbackDecorator} that provides fishing-specific feedback
 * when loot is executed, such as messages and sounds based on catch quality.
 */
public final class FishingLootFeedbackDecorator implements FeedbackDecorator {
    private final LootResult delegate;
    private final FishingCatchQuality quality;

    /**
     * Constructs a fishing loot feedback decorator.
     *
     * @param delegate the underlying loot result to execute
     * @param quality the quality of the catch that determines feedback
     */
    public FishingLootFeedbackDecorator(LootResult delegate, FishingCatchQuality quality) {
        this.delegate = delegate;
        this.quality = quality;
    }

    /**
     * Executes the loot and provides player feedback based on the catch quality.
     *
     * @param context the loot context
     * @param amount the loot amount
     */
    @Override
    public void execute(LootContext context, int amount) {
        delegate.execute(context, amount);

        if (context.blightedPlayer() == null || context.blightedPlayer().getPlayer() == null) {
            return;
        }

        if (quality == FishingCatchQuality.COMMON) {
            return;
        }

        String prefix = switch (quality) {
            case OUTSTANDING_CATCH -> " §d§lOUTSTANDING CATCH! §f| §7You found §f";
            case GREAT_CATCH -> " §6§lGREAT CATCH! §f| §7You found §f";
            case GOOD_CATCH -> " §5§lGOOD CATCH! §f| §7You found §f";
            default -> null;
        };

        Sound sound = switch (quality) {
            case OUTSTANDING_CATCH -> Sound.ENTITY_PLAYER_LEVELUP;
            case GREAT_CATCH, GOOD_CATCH -> Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
            default -> null;
        };

        float pitch = switch (quality) {
            case OUTSTANDING_CATCH, GREAT_CATCH -> 1.5f;
            case GOOD_CATCH -> 2.0f;
            default -> 1.0f;
        };

        if (prefix != null && sound != null) {
            context.blightedPlayer().getPlayer().sendMessage(prefix + delegate.displayName(amount));
            context.blightedPlayer().getPlayer().playSound(
                context.blightedPlayer().getPlayer().getLocation(),
                sound,
                1.0f,
                pitch
            );
        }
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

    /**
     * Represents the quality levels of a fishing catch for feedback purposes.
     */
    public enum FishingCatchQuality {
        COMMON,
        GOOD_CATCH,
        GREAT_CATCH,
        OUTSTANDING_CATCH
    }
}
