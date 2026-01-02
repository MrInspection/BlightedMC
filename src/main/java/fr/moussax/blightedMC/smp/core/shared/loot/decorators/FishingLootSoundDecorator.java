package fr.moussax.blightedMC.smp.core.shared.loot.decorators;

import fr.moussax.blightedMC.smp.core.shared.loot.FeedbackDecorator;
import fr.moussax.blightedMC.smp.core.shared.loot.LootContext;
import fr.moussax.blightedMC.smp.core.shared.loot.LootResult;
import org.bukkit.Sound;

public final class FishingLootSoundDecorator implements FeedbackDecorator {
    private final LootResult delegate;
    private final FishingCatchQuality quality;

    public FishingLootSoundDecorator(LootResult delegate, FishingCatchQuality quality) {
        this.delegate = delegate;
        this.quality = quality;
    }

    @Override
    public void execute(LootContext context, int amount) {
        delegate.execute(context, amount);

        if (context.blightedPlayer() == null || context.blightedPlayer().getPlayer() == null) {
            return;
        }

        if (quality == FishingCatchQuality.COMMON) {
            return;
        }

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

        if (sound != null) {
            context.blightedPlayer().getPlayer().playSound(
                context.blightedPlayer().getPlayer().getLocation(),
                sound,
                1.0f,
                pitch
            );
        }
    }

    @Override
    public String displayName(int amount) {
        return delegate.displayName(amount);
    }

    @Override
    public LootResult delegate() {
        return delegate;
    }

    public enum FishingCatchQuality {
        COMMON,
        GOOD_CATCH,
        GREAT_CATCH,
        OUTSTANDING_CATCH
    }
}