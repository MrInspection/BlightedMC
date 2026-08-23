package fr.moussax.blightedMC.engine.items.recipes;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.engine.items.BlightedItem;
import fr.moussax.blightedMC.engine.items.recipes.crafting.BlightedRecipe;
import fr.moussax.blightedMC.engine.items.recipes.forging.ForgeRecipe;
import fr.moussax.blightedMC.engine.items.recipes.forging.registry.ForgeRegistry;
import fr.moussax.blightedMC.engine.items.recipes.forging.menu.ForgeRecipePreviewMenu;
import fr.moussax.blightedMC.engine.items.recipes.crafting.menu.CraftingRecipePreviewMenu;
import fr.moussax.blightedMC.shared.ui.menu.Menu;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class RecipePreviewManager {

    private RecipePreviewManager() {
    }

    public static boolean openPreview(@NonNull Player player, @NonNull BlightedItem item, @Nullable Menu previousMenu) {
        for (BlightedRecipe recipe : BlightedRecipe.REGISTERED_RECIPES) {
            if (recipe != null && recipe.getResult() != null && recipe.getResult().equals(item)) {
                BlightedMC.menuManager().openMenu(new CraftingRecipePreviewMenu(recipe, previousMenu), player);
                return true;
            }
        }

        for (ForgeRecipe forgeRecipe : ForgeRegistry.RECIPES) {
            if (forgeRecipe != null && forgeRecipe.getForgedItem() != null && forgeRecipe.getForgedItem().equals(item)) {
                BlightedMC.menuManager().openMenu(new ForgeRecipePreviewMenu(forgeRecipe, previousMenu), player);
                return true;
            }
        }

        return false;
    }

    public static boolean openPreview(@NonNull Player player, @NonNull BlightedItem item) {
        return openPreview(player, item, null);
    }
}
