package fr.moussax.blightedMC.engine.items.recipes.crafting.menu;

import fr.moussax.blightedMC.engine.items.recipes.crafting.BlightedRecipe;
import fr.moussax.blightedMC.engine.items.recipes.crafting.BlightedShapedRecipe;
import fr.moussax.blightedMC.engine.items.recipes.crafting.BlightedShapelessRecipe;
import fr.moussax.blightedMC.engine.items.recipes.CraftingObject;
import fr.moussax.blightedMC.engine.items.recipes.RecipePreviewManager;
import fr.moussax.blightedMC.shared.ui.menu.Menu;
import fr.moussax.blightedMC.shared.ui.menu.interaction.MenuElementPreset;
import fr.moussax.blightedMC.shared.ui.menu.interaction.MenuItemInteraction;
import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

public final class CraftingRecipePreviewMenu extends Menu {

    private static final int[] CRAFTING_GRID_SLOTS = {
        10, 11, 12,
        19, 20, 21,
        28, 29, 30
    };

    private static final int WORKBENCH_SLOT = 23;
    private static final int RESULT_SLOT = 25;
    private static final int BACK_BUTTON_SLOT = 48;
    private static final int CLOSE_BUTTON_SLOT = 49;

    private final BlightedRecipe recipe;
    private final Menu previousMenu;

    public CraftingRecipePreviewMenu(@NonNull BlightedRecipe recipe, @Nullable Menu previousMenu) {
        super(recipe.getResult().getDisplayName().replaceAll("§[0-9A-FK-ORa-fk-or]", "") + " Recipe", 54);
        this.recipe = recipe;
        this.previousMenu = previousMenu;
    }

    public CraftingRecipePreviewMenu(@NonNull BlightedRecipe recipe) {
        this(recipe, null);
    }

    @Override
    public void build(Player player) {
        setupRecipeVisualization();
        setupNavigation();
    }

    private void setupRecipeVisualization() {
        if (recipe instanceof BlightedShapedRecipe shapedRecipe) {
            setupShapedRecipeGrid(shapedRecipe);
        } else if (recipe instanceof BlightedShapelessRecipe shapelessRecipe) {
            setupShapelessRecipeGrid(shapelessRecipe);
        }

        setItem(WORKBENCH_SLOT, new ItemBuilder(Material.ENCHANTING_TABLE, "§dBlighted Workbench")
            .addLore("§7Craft this recipe by using a", "§7blighted workbench.")
            .toItemStack(), MenuItemInteraction.ANY_CLICK, (p, t) -> {
        });

        ItemStack resultItem = recipe.getResult().toItemStack().clone();
        int amount = recipe.getAmount() > 0 ? recipe.getAmount() : 1;
        resultItem.setAmount(amount);
        setItem(RESULT_SLOT, resultItem, MenuItemInteraction.ANY_CLICK, (p, t) -> {
        });
    }

    private void setupShapedRecipeGrid(BlightedShapedRecipe shapedRecipe) {
        List<CraftingObject> pattern = shapedRecipe.getRecipe();

        for (int i = 0; i < pattern.size() && i < CRAFTING_GRID_SLOTS.length; i++) {
            CraftingObject craftingObject = pattern.get(i);

            if (craftingObject == null) {
                setItem(CRAFTING_GRID_SLOTS[i], new ItemStack(Material.AIR), MenuItemInteraction.ANY_CLICK, (p, t) -> {
                });
                continue;
            }

            ItemStack ingredientItem = createIngredientDisplay(craftingObject);
            setItem(CRAFTING_GRID_SLOTS[i], ingredientItem, MenuItemInteraction.ANY_CLICK, (p, t) -> {
                // ponytail: kept
                if (craftingObject.isCustom() && craftingObject.getManager() != null) {
                    RecipePreviewManager.openPreview(p, craftingObject.getManager(), this);
                }
            });
        }
    }

    private void setupShapelessRecipeGrid(BlightedShapelessRecipe shapelessRecipe) {
        List<CraftingObject> ingredients = shapelessRecipe.getIngredients();

        for (int i = 0; i < CRAFTING_GRID_SLOTS.length; i++) {
            if (i < ingredients.size()) {
                CraftingObject ingredient = ingredients.get(i);
                ItemStack ingredientItem = createIngredientDisplay(ingredient);

                setItem(CRAFTING_GRID_SLOTS[i], ingredientItem, MenuItemInteraction.ANY_CLICK, (p, t) -> {
                    // ponytail: kept
                    if (ingredient.isCustom() && ingredient.getManager() != null) {
                        RecipePreviewManager.openPreview(p, ingredient.getManager(), this);
                    }
                });
            } else {
                setItem(CRAFTING_GRID_SLOTS[i], new ItemStack(Material.AIR), MenuItemInteraction.ANY_CLICK, (p, t) -> {
                });
            }
        }
    }

    private ItemStack createIngredientDisplay(CraftingObject craftingObject) {
        ItemStack ingredientItem = getCraftingObjectItem(craftingObject);
        ingredientItem.setAmount(Math.max(1, craftingObject.getAmount()));
        return ingredientItem;
    }

    private ItemStack getCraftingObjectItem(CraftingObject craftingObject) {
        if (craftingObject.isCustom() && craftingObject.getManager() != null) {
            return craftingObject.getManager().toItemStack().clone();
        }
        if (craftingObject.isVanilla() && craftingObject.getVanillaItem() != null) {
            return craftingObject.getVanillaItem().clone();
        }
        return new ItemStack(Material.AIR);
    }

    private void setupNavigation() {
        if (previousMenu != null) {
            setBackButton(BACK_BUTTON_SLOT, previousMenu);
        }
        setCloseButton(CLOSE_BUTTON_SLOT);

        fillEmptyWith(MenuElementPreset.EMPTY_SLOT_FILLER);
    }
}
