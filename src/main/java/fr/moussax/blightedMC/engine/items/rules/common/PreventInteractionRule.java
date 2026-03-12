package fr.moussax.blightedMC.engine.items.rules.common;

import fr.moussax.blightedMC.engine.items.rules.ItemRule;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public final class PreventInteractionRule implements ItemRule {
    @Override
    public boolean canInteract(PlayerInteractEvent event, ItemStack itemStack) {
        return false; // block all interactions
    }
}
