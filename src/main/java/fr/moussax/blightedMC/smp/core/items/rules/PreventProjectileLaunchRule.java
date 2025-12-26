package fr.moussax.blightedMC.smp.core.items.rules;

import org.bukkit.event.Event;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;

public final class PreventProjectileLaunchRule implements ItemRule {
    @Override
    public boolean canUse(Event event, ItemStack itemStack) {
        return event instanceof ProjectileLaunchEvent;
    }
}
