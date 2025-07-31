package fr.moussax.blightedMC.core.items.rules;

import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public interface ItemRule {
  default boolean canPlace(BlockPlaceEvent event, ItemStack itemStack) { return true;}
  default boolean canInteract(PlayerInteractEvent event, ItemStack itemStack) { return true;}
  default boolean canUse(Event event, ItemStack itemStack) { return true;}
}
