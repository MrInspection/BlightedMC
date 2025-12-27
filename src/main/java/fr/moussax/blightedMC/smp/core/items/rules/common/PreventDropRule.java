package fr.moussax.blightedMC.smp.core.items.rules.common;

import fr.moussax.blightedMC.smp.core.items.rules.ItemRule;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public final class PreventDropRule implements ItemRule {
    @Override
    public boolean canUse(Event event, ItemStack itemStack) {
        return event instanceof PlayerDropItemEvent;
    }
}
