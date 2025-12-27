package fr.moussax.blightedMC.smp.core.items.blocks;

import fr.moussax.blightedMC.smp.core.items.BlightedItem;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

/**
 * Base class for custom BlightedMC blocks.
 * <p>
 * Wraps a {@link Material} and a {@link BlightedItem}, providing hooks
 * for block events such as placement, interaction, and breaking.
 * Subclasses can override event methods to implement custom behavior.
 */
public abstract class BlightedBlock {

    protected final Material material;
    protected final BlightedItem blightedItem;

    /**
     * Constructs a new BlightedBlock with the given material and associated custom item.
     *
     * @param material     the block material
     * @param blightedItem the custom item representing this block
     */
    public BlightedBlock(@NonNull Material material, @NonNull BlightedItem blightedItem) {
        this.material = material;
        this.blightedItem = blightedItem;
    }

    /**
     * Returns the unique ID of this block's associated item.
     *
     * @return item ID
     */
    public String getId() {
        return blightedItem.getItemId();
    }

    /**
     * Returns the custom item representing this block.
     *
     * @return the associated BlightedItem
     */
    public BlightedItem getBlightedItem() {
        return blightedItem;
    }

    /**
     * Returns the material type of this block.
     *
     * @return block material
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Called when the block is placed in the world.
     * <p>
     * Override to add custom placement behavior.
     *
     * @param event the block placement event
     */
    public void onPlace(BlockPlaceEvent event) {
        // Override to add placement logic
    }

    /**
     * Called when a player interacts with this block.
     * <p>
     * Override to add custom interaction behavior.
     *
     * @param event the player interaction event
     */
    public void onInteract(PlayerInteractEvent event) {
        // Override to add interaction logic
    }

    /**
     * Called when the block is broken.
     * <p>
     * Override to modify or replace the dropped item.
     *
     * @param event       the block break event
     * @param droppedItem the default item to drop (can be modified or null)
     * @return the item to drop, or null for no drop
     */
    public ItemStack onBreak(BlockBreakEvent event, ItemStack droppedItem) {
        return droppedItem;
    }

    /**
     * Called when the block is broken without a player interaction event.
     * <p>
     * This method is used in situations like explosions where a BlockBreakEvent is not available.
     * Override to modify or replace the dropped item.
     *
     * @param droppedItem the default item to drop (can be modified or null)
     * @return the item to drop, or null for no drop
     */
    public ItemStack onBreak(ItemStack droppedItem) {
        return droppedItem;
    }
}
