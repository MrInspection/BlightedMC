package fr.moussax.blightedMC.smp.core.items.rules;

import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.ItemStack;

public final class PreventBucketInteractionsRule implements ItemRule {
    @Override
    public boolean canUse(Event event, ItemStack itemStack) {
        return event instanceof PlayerBucketEmptyEvent;
    }
}
