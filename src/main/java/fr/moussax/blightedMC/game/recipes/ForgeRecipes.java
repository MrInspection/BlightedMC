package fr.moussax.blightedMC.game.recipes;

import fr.moussax.blightedMC.core.items.crafting.CraftingObject;
import fr.moussax.blightedMC.core.items.forging.ForgeRecipe;
import fr.moussax.blightedMC.core.items.registry.ItemDirectory;
import org.bukkit.Material;

public class ForgeRecipes {
    public static void initialize() {
        new ForgeRecipe.Builder()
            .forgedItem(ItemDirectory.getItem("PLASMA_BUCKET"))
            .forgedAmount(1)
            .fuelCost(10000)
            .ingredients(
                new CraftingObject(ItemDirectory.getItem("MAGMA_BUCKET"), 1),
                new CraftingObject(ItemDirectory.getItem("MAGMA_BUCKET"), 1),
                new CraftingObject(Material.NETHER_STAR, 1)
            )
            .build();
    }
}
