package fr.moussax.blightedMC.core.items.rules;

import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class PreventConsumeRule implements ItemRule {
  @Override
  public boolean canUse(Event event, ItemStack itemStack) {
    return !(event instanceof PlayerItemConsumeEvent); // block only consume
  }
}
