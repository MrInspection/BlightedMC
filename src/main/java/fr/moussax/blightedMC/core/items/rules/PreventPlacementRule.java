package fr.moussax.blightedMC.core.items.rules;

import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class PreventPlacementRule implements ItemRule {
  @Override
  public boolean canPlace(BlockPlaceEvent event, ItemStack itemStack) {
    return false; // FORBIDDEN
  }
}
