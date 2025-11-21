package fr.moussax.blightedMC.core.items.rules;

import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Rule that prevents placing the item entirely and restricts interaction.
 * Placement is always disallowed. Interaction is blocked when right-clicking a block.
 */
public class PreventPlacementRule implements ItemRule {
    @Override
    public boolean canPlace(BlockPlaceEvent event, ItemStack itemStack) {
        return true; // FORBIDDEN
    }

    @Override
    public boolean canInteract(PlayerInteractEvent event, ItemStack itemStack) {
        return event.getAction() != Action.RIGHT_CLICK_BLOCK;
    }
}
