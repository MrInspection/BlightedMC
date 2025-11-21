package fr.moussax.blightedMC.core.entities.loot;

import fr.moussax.blightedMC.core.player.BlightedPlayer;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

/**
 * Represents an item loot that can be dropped or consumed.
 * <p>
 * Stores an {@link ItemStack} along with minimum and maximum
 * amounts that can be generated when dropped.
 */
public class ItemLoot implements DroppableConsumable {
    private final ItemStack itemStack;
    private final int minAmount;
    private final int maxAmount;

    /**
     * Constructs an ItemLoot with a specified item and amount range.
     *
     * @param itemStack the base item stack to drop
     * @param minAmount minimum amount (must be >= 1)
     * @param maxAmount maximum amount (must be >= minAmount)
     * @throws IllegalArgumentException if minAmount < 1 or maxAmount < minAmount
     */
    public ItemLoot(ItemStack itemStack, int minAmount, int maxAmount) {
        if (minAmount < 1 || maxAmount < minAmount) {
            throw new IllegalArgumentException("Invalid min/max amounts");
        }
        this.itemStack = itemStack;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }

    /**
     * Generates a random amount of this loot between minAmount and maxAmount inclusive.
     *
     * @return generated amount
     */
    public int generateAmount() {
        return minAmount + (int) (Math.random() * (maxAmount - minAmount + 1));
    }

    /**
     * Consumes (drops or gives) the loot to the player or at the location.
     *
     * @param killer       player responsible for the loot (nullable)
     * @param dropLocation location to drop the item if not given to the player
     * @param toPlayer     if true, add directly to the player's inventory if possible
     * @param amount       pre-generated amount for consistency
     */
    public void consume(BlightedPlayer killer, Location dropLocation, boolean toPlayer, int amount) {
        ItemStack drop = itemStack.clone();
        drop.setAmount(amount);

        if (toPlayer && killer != null && killer.getPlayer() != null) {
            killer.getPlayer().getInventory().addItem(drop);
        } else {
            Objects.requireNonNull(dropLocation.getWorld()).dropItemNaturally(dropLocation, drop);
        }
    }

    /**
     * Consumes the loot by generating a random amount and dropping/giving it.
     *
     * @param killer       player responsible for the loot (nullable)
     * @param dropLocation location to drop the item if not given to the player
     * @param toPlayer     if true, add directly to the player's inventory if possible
     */
    @Override
    public void consume(BlightedPlayer killer, Location dropLocation, boolean toPlayer) {
        int amount = generateAmount();
        consume(killer, dropLocation, toPlayer, amount);
    }

    /**
     * Returns the display name of the loot item.
     * If the item has a custom display name, it is returned;
     * otherwise, a formatted type name is returned.
     *
     * @return the loot's display name
     */
    @Override
    public String name() {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            return meta.getDisplayName();
        }
        String rawName = itemStack.getType().name().toLowerCase().replace('_', ' ');
        return Character.toUpperCase(rawName.charAt(0)) + rawName.substring(1);
    }
}
