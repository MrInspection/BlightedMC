package fr.moussax.blightedMC.gameplay.recipes;

import fr.moussax.blightedMC.core.items.crafting.CraftingObject;
import fr.moussax.blightedMC.core.items.forging.ForgeRecipe;
import fr.moussax.blightedMC.core.items.registry.ItemDirectory;
import org.bukkit.Material;

public class ForgeRecipes {

    public static void initialize() {
        new ForgeRecipe(ItemDirectory.getItem("ENCHANTED_IRON_INGOT"), 1, 1000,
            new CraftingObject(ItemDirectory.getItem("ENCHANTED_IRON_BLOCK"), 1),
            new CraftingObject(Material.DIAMOND, 64)
        ).register();
    }
}
