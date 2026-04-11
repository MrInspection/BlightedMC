package fr.moussax.blightedMC.engine.items;

import org.bukkit.inventory.ItemStack;

@FunctionalInterface
public interface ItemFactory {

    ItemStack createItemStack();
}
