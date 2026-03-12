package fr.moussax.blightedMC.engine.items.rules.common;

import fr.moussax.blightedMC.engine.items.rules.ItemRule;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.ItemStack;

public final class PreventBucketInteractionsRule implements ItemRule {
    @Override
    public boolean canUse(Event event, ItemStack itemStack) {
        return event instanceof PlayerBucketEmptyEvent;
    }
}
