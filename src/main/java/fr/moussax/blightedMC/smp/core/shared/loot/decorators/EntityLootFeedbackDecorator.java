package fr.moussax.blightedMC.smp.core.shared.loot.decorators;

import fr.moussax.blightedMC.smp.core.shared.loot.FeedbackDecorator;
import fr.moussax.blightedMC.smp.core.shared.loot.LootContext;
import fr.moussax.blightedMC.smp.core.shared.loot.LootResult;
import org.bukkit.Sound;

/**
 * A {@link FeedbackDecorator} that provides feedback for entity loot drops,
 * including messages and sounds based on the rarity of the item.
 */
public final class EntityLootFeedbackDecorator implements FeedbackDecorator {
    private final LootResult delegate;
    private final EntityLootRarity rarity;

    /**
     * Constructs an entity loot feedback decorator.
     *
     * @param delegate the underlying loot result to execute
     * @param rarity the rarity of the loot that determines feedback
     */
    public EntityLootFeedbackDecorator(LootResult delegate, EntityLootRarity rarity) {
        this.delegate = delegate;
        this.rarity = rarity;
    }

    /**
     * Executes the loot and provides player feedback if the loot rarity is notable.
     *
     * @param context the loot context
     * @param amount the amount of loot
     */
    @Override
    public void execute(LootContext context, int amount) {
        delegate.execute(context, amount);

        if (context.blightedPlayer() == null || context.blightedPlayer().getPlayer() == null) {
            return;
        }

        if (rarity == EntityLootRarity.COMMON || rarity == EntityLootRarity.UNCOMMON) {
            return;
        }

        String prefix = switch (rarity) {
            case INSANE -> " §c§lINSANE DROP! §f| §7You found §f";
            case CRAZY -> " §d§lCRAZY DROP! §f| §7You found §f";
            case VERY_RARE -> " §b§lVERY RARE DROP! §f| §7You found §f";
            case RARE -> " §f§lRARE DROP! §f| §7You found §f";
            default -> null;
        };

        float pitch = switch (rarity) {
            case INSANE -> 0.8f;
            case CRAZY -> 1.2f;
            case VERY_RARE -> 1.5f;
            case RARE -> 1.8f;
            default -> 1.0f;
        };

        if (prefix != null) {
            context.blightedPlayer().getPlayer().sendMessage(prefix + delegate.displayName(amount));
            context.blightedPlayer().getPlayer().playSound(
                context.blightedPlayer().getPlayer().getLocation(),
                Sound.UI_TOAST_CHALLENGE_COMPLETE,
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
     * Represents the rarity levels of entity loot and provides looting modifiers.
     */
    public enum EntityLootRarity {
        COMMON(0.0),
        UNCOMMON(0.0),
        RARE(0.05),
        VERY_RARE(0.075),
        CRAZY(0.1),
        INSANE(0.125);

        private final double lootingModifier;

        EntityLootRarity(double lootingModifier) {
            this.lootingModifier = lootingModifier;
        }

        /**
         * Applies looting enchantment effects to a base chance.
         *
         * @param baseChance the base drop chance
         * @param lootingLevel the looting level on the player's weapon
         * @return adjusted drop chance capped at 1.0
         */
        public double applyLooting(double baseChance, int lootingLevel) {
            return Math.min(1.0, baseChance + (lootingLevel * lootingModifier));
        }
    }
}
