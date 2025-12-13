package fr.moussax.blightedMC.game.recipes;

import fr.moussax.blightedMC.core.items.crafting.CraftingObject;
import fr.moussax.blightedMC.core.items.forging.ForgeRecipe;
import fr.moussax.blightedMC.core.items.registry.ItemDirectory;
import fr.moussax.blightedMC.utils.debug.Log;
import org.bukkit.Material;

import java.util.List;

public class ForgeRecipes {
    private static final List<ForgeRecipe> DEFAULT_RECIPES = List.of(
        new ForgeRecipe(ItemDirectory.getItem("ENCHANTED_IRON_INGOT"), 1, 1000,
            new CraftingObject(ItemDirectory.getItem("ENCHANTED_IRON_BLOCK"), 1),
            new CraftingObject(Material.DIAMOND, 4)
        )
    );

    public static void initialize() {
        DEFAULT_RECIPES.forEach(ForgeRecipe::register);
        Log.success("ForgeRecipes", "Registered " + ForgeRecipe.REGISTERED_RECIPES.size() + " forge recipes.");
    }
}
