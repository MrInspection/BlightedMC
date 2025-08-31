package fr.moussax.blightedMC.core.items.rules;

import org.bukkit.event.Event;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;

public class PreventProjectileLaunchRule implements ItemRule {
  @Override
  public boolean canUse(Event event, ItemStack itemStack) {
    return !(event instanceof ProjectileLaunchEvent);
  }
}
