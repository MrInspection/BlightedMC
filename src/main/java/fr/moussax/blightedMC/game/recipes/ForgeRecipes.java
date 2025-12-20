package fr.moussax.blightedMC.game.recipes;

import fr.moussax.blightedMC.core.items.crafting.CraftingObject;
import fr.moussax.blightedMC.core.items.forging.ForgeRecipe;
import fr.moussax.blightedMC.core.items.registry.ItemDirectory;
import org.bukkit.Material;

public class ForgeRecipes {
    public static void initialize() {
        new ForgeRecipe.Builder()
            .forgedItem(ItemDirectory.getItem("ENCHANTED_IRON_INGOT"))
            .forgedAmount(1)
            .fuelCost(100)
            .ingredients(
                new CraftingObject(Material.DIAMOND, 1),
                new CraftingObject(ItemDirectory.getItem("ENCHANTED_IRON_BLOCK"), 1)
            )
            .build();
    }
}
