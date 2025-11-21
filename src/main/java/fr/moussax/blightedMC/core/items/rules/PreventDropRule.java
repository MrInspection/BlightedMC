package fr.moussax.blightedMC.core.items.rules;

import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class PreventDropRule implements ItemRule {
    @Override
    public boolean canUse(Event event, ItemStack itemStack) {
        return event instanceof PlayerDropItemEvent;
    }
}
