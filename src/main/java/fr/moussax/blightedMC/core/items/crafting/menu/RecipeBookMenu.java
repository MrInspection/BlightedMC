package fr.moussax.blightedMC.core.items.crafting.menu;

import fr.moussax.blightedMC.core.items.ItemTemplate;
import fr.moussax.blightedMC.core.items.crafting.BlightedRecipe;
import fr.moussax.blightedMC.core.items.crafting.BlightedShapedRecipe;
import fr.moussax.blightedMC.core.items.crafting.BlightedShapelessRecipe;
import fr.moussax.blightedMC.core.items.crafting.CraftingObject;
import fr.moussax.blightedMC.core.menus.*;
import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RecipeBookMenu {

    private static final int[] RECIPE_SLOTS = {
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25,
        28, 29, 30, 31, 32, 33, 34,
        37, 38, 40, 41, 42, 43
    };

    private static final int[] FILLER_SLOTS = {
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
        17, 18, 26, 27, 35, 36, 44,
        45, 46, 47, 51, 52, 53
    };

    private static final int[] CRAFTING_GRID_SLOTS = {
        10, 11, 12,
        19, 20, 21,
        28, 29, 30
    };

    private static final int WORKBENCH_SLOT = 23;
    private static final int RESULT_SLOT = 25;
    private static final int BACK_BUTTON_SLOT = 48;
    private static final int CLOSE_BUTTON_SLOT = 49;
    private static final int NEXT_BUTTON_SLOT = 50;

    public static class RecipeListMenu extends PaginatedMenu {
        private final Menu previousMenu;

        public RecipeListMenu(Menu previousMenu) {
            super("§rBlighted Recipe Book", 54);
            this.previousMenu = previousMenu;
        }

        @Override
        protected int getTotalItems(Player player) {
            return BlightedRecipe.REGISTERED_RECIPES.size();
        }

        @Override
        protected int getItemsPerPage() {
            return RECIPE_SLOTS.length;
        }

        @Override
        protected ItemStack getItem(Player player, int index) {
            List<BlightedRecipe> recipes = new ArrayList<>(BlightedRecipe.REGISTERED_RECIPES);
            if (index >= recipes.size()) return new ItemStack(Material.AIR);

            BlightedRecipe recipe = recipes.get(index);
            ItemStack resultItem = recipe.getResult().toItemStack().clone();

            var meta = resultItem.getItemMeta();
            if (meta == null) return resultItem;

            List<String> lore = meta.getLore();
            if (lore == null) lore = new ArrayList<>();
            lore.add("");
            lore.add("§eClick to view recipe!");
            meta.setLore(lore);
            resultItem.setItemMeta(meta);

            return resultItem;
        }

        @Override
        public void build(Player player) {
            totalItems = getTotalItems(player);
            int start = currentPage * getItemsPerPage();
            int end = Math.min(start + getItemsPerPage(), totalItems);

            clearInventory();
            populateRecipeSlots(player, start, end);
            fillSlots(FILLER_SLOTS, MenuElementPreset.EMPTY_SLOT_FILLER);
            setupNavigationButtons(player, end);
        }

        private void populateRecipeSlots(Player player, int start, int end) {
            int recipeIndex = 0;
            for (int i = start; i < end && recipeIndex < RECIPE_SLOTS.length; i++) {
                final int itemIndex = i;
                setItem(RECIPE_SLOTS[recipeIndex], getItem(player, itemIndex), MenuItemInteraction.ANY_CLICK,
                    (p, t) -> onItemClick(p, itemIndex, t));
                recipeIndex++;
            }
        }

        private void setupNavigationButtons(Player player, int end) {
            if (currentPage > 0) {
                setItem(BACK_BUTTON_SLOT, MenuElementPreset.BACK_BUTTON, MenuItemInteraction.ANY_CLICK, (p, t) -> {
                    currentPage--;
                    MenuManager.openMenu(this, p);
                });
            } else {
                setItem(BACK_BUTTON_SLOT, MenuElementPreset.BACK_BUTTON, MenuItemInteraction.ANY_CLICK, (p, t) -> {
                    close();
                    p.openInventory(CraftingTableMenu.createInventory());
                });
            }

            if (end < totalItems) {
                setItem(NEXT_BUTTON_SLOT, MenuElementPreset.NEXT_BUTTON, MenuItemInteraction.ANY_CLICK, (p, t) -> {
                    currentPage++;
                    MenuManager.openMenu(this, p);
                });
            } else {
                setItem(NEXT_BUTTON_SLOT, MenuElementPreset.EMPTY_SLOT_FILLER.getItem(), MenuItemInteraction.ANY_CLICK, (p, t) -> {
                });
            }

            setItem(CLOSE_BUTTON_SLOT, MenuElementPreset.CLOSE_BUTTON, MenuItemInteraction.ANY_CLICK, (p, t) -> close());
        }

        @Override
        protected void onItemClick(Player player, int index, org.bukkit.event.inventory.ClickType clickType) {
            List<BlightedRecipe> recipes = new ArrayList<>(BlightedRecipe.REGISTERED_RECIPES);
            if (index >= recipes.size()) return;

            MenuManager.openMenu(new RecipeDetailMenu(recipes.get(index), this), player);
        }
    }

    public static class RecipeDetailMenu extends Menu {
        private final BlightedRecipe recipe;
        private final Menu previousMenu;

        public RecipeDetailMenu(BlightedRecipe recipe, Menu previousMenu) {
            super(recipe.getResult().getDisplayName().replaceAll("§[0-9A-FK-ORa-fk-or]", "") + " Recipe", 54);
            this.recipe = recipe;
            this.previousMenu = previousMenu;
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
                    if (craftingObject.isCustom()) {
                        BlightedRecipe ingredientRecipe = findRecipeForItem(craftingObject.getManager());
                        if (ingredientRecipe != null) {
                            MenuManager.openMenu(new RecipeDetailMenu(ingredientRecipe, this), p);
                        }
                    }
                });
            }
        }

        private void setupShapelessRecipeGrid(BlightedShapelessRecipe shapelessRecipe) {
            List<CraftingObject> ingredients = shapelessRecipe.getIngredients();

            for (int i = 0; i < ingredients.size() && i < CRAFTING_GRID_SLOTS.length; i++) {
                CraftingObject ingredient = ingredients.get(i);
                ItemStack ingredientItem = createIngredientDisplay(ingredient);

                setItem(CRAFTING_GRID_SLOTS[i], ingredientItem, MenuItemInteraction.ANY_CLICK, (p, t) -> {
                    if (ingredient.isCustom()) {
                        BlightedRecipe ingredientRecipe = findRecipeForItem(ingredient.getManager());
                        if (ingredientRecipe != null) {
                            MenuManager.openMenu(new RecipeDetailMenu(ingredientRecipe, this), p);
                        }
                    }
                });
            }
        }

        private ItemStack createIngredientDisplay(CraftingObject craftingObject) {
            ItemStack ingredientItem = getCraftingObjectItem(craftingObject);
            ingredientItem.setAmount(Math.max(1, craftingObject.getAmount()));
            return ingredientItem;
        }

        private BlightedRecipe findRecipeForItem(ItemTemplate itemTemplate) {
            for (BlightedRecipe recipe : BlightedRecipe.REGISTERED_RECIPES) {
                if (recipe.getResult().equals(itemTemplate)) {
                    return recipe;
                }
            }
            return null;
        }

        private ItemStack getCraftingObjectItem(CraftingObject craftingObject) {
            if (craftingObject.isCustom()) {
                return craftingObject.getManager().toItemStack().clone();
            }
            if (craftingObject.isVanilla()) {
                return craftingObject.getVanillaItem().clone();
            }
            return new ItemStack(Material.AIR);
        }

        private void setupNavigation() {
            setItem(BACK_BUTTON_SLOT, MenuElementPreset.BACK_BUTTON, MenuItemInteraction.ANY_CLICK,
                (p, t) -> MenuManager.openMenu(previousMenu, p));
            setItem(CLOSE_BUTTON_SLOT, MenuElementPreset.CLOSE_BUTTON, MenuItemInteraction.ANY_CLICK,
                (p, t) -> close());
            fillSlots(FILLER_SLOTS, MenuElementPreset.EMPTY_SLOT_FILLER);
        }
    }
}