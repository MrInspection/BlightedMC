package fr.moussax.blightedSMP.engine.loot.results.gems;

import fr.moussax.blightedSMP.engine.loot.LootContext;
import fr.moussax.blightedSMP.engine.loot.LootResult;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

/**
 * A {@link LootResult} that drops Blighted Gems at the loot origin.
 */
public final class GemsResult implements LootResult {

    /**
     * Drops the specified quantity of gemstones at the loot origin.
     *
     * @param context loot context
     * @param amount  number of gems to drop
     */
    @Override
    public void execute(LootContext context, int amount) {
        ItemStack gemstone = new GemsItem(amount).get();
        if (context.origin().getWorld() == null) return;

        Item droppedItem = context.origin().getWorld().dropItem(context.origin(), gemstone);

        if (context.velocity() != null) {
            droppedItem.setVelocity(context.velocity());
        }
    }

    /**
     * Returns the display name for the gem drop.
     *
     * @param amount number of gems
     * @return formatted display name
     */
    @Override
    public String displayName(int amount) {
        return "§5Blighted Gemstone";
    }
}
