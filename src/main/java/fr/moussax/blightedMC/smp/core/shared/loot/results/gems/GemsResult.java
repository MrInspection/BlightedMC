package fr.moussax.blightedMC.smp.core.shared.loot.results.gems;

import fr.moussax.blightedMC.smp.core.shared.loot.LootContext;
import fr.moussax.blightedMC.smp.core.shared.loot.LootResult;

import java.util.Objects;

/**
 * A {@link LootResult} that grants Blighted Gems to the player
 * or drops them at a location if no player is present.
 */
public final class GemsResult implements LootResult {

    /**
     * Grants the specified number of gems to the player or drops them
     * at the loot origin if the player is null.
     *
     * @param context the loot context
     * @param amount the number of gems to grant
     */
    @Override
    public void execute(LootContext context, int amount) {
        if (context.player() != null) {
            context.player().addGems(amount);
        } else {
            Objects.requireNonNull(context.origin().getWorld())
                .dropItemNaturally(context.origin(), new GemsItem(amount).createItemStack());
        }
    }

    /**
     * Returns the display name for this loot.
     *
     * @param amount the number of gems
     * @return the display name string
     */
    @Override
    public String displayName(int amount) {
        return "§5Blighted Gemstone";
    }
}
