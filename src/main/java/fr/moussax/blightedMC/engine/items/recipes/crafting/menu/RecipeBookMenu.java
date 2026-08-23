package fr.moussax.blightedMC.engine.items.recipes.crafting.menu;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.engine.items.recipes.crafting.BlightedRecipe;
import fr.moussax.blightedMC.shared.ui.menu.Menu;
import fr.moussax.blightedMC.shared.ui.menu.PaginatedMenu;
import fr.moussax.blightedMC.shared.ui.menu.interaction.MenuElementPreset;
import fr.moussax.blightedMC.shared.ui.menu.interaction.MenuItemInteraction;
import fr.moussax.blightedMC.shared.ui.menu.system.MenuManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public final class RecipeBookMenu {
    private static final int[] RECIPE_SLOTS = {
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25,
        28, 29, 30, 31, 32, 33, 34,
        37, 38, 39, 40, 41, 42, 43
    };

    private static final int[] FILLER_SLOTS = {
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
        17, 18, 26, 27, 35, 36, 44,
        45, 46, 47, 51, 52, 53
    };

    private static final int BACK_BUTTON_SLOT = 48;
    private static final int CLOSE_BUTTON_SLOT = 49;
    private static final int NEXT_BUTTON_SLOT = 50;

    private static final MenuManager manager = BlightedMC.menuManager();

    public static class RecipeListMenu extends PaginatedMenu {
        private final Menu previousMenu;
        private final List<BlightedRecipe> cachedRecipes;

        public RecipeListMenu(Menu previousMenu) {
            super("Blighted Recipe Book", 54);
            this.previousMenu = previousMenu;
            this.cachedRecipes = new ArrayList<>(BlightedRecipe.REGISTERED_RECIPES);
            this.cachedRecipes.sort((r1, r2) -> {
                String name1 = r1.getResult().getDisplayName();
                String name2 = r2.getResult().getDisplayName();
                return (name1 != null ? name1 : "").compareTo(name2 != null ? name2 : "");
            });
        }

        @Override
        protected int getTotalItems(@NonNull Player player) {
            return cachedRecipes.size();
        }

        @Override
        protected int getItemsPerPage() {
            return RECIPE_SLOTS.length;
        }

        @Override
        protected ItemStack getItem(@NonNull Player player, int index) {
            if (index >= cachedRecipes.size()) return new ItemStack(Material.AIR);

            BlightedRecipe recipe = cachedRecipes.get(index);
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
        public void build(@NonNull Player player) {
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
                setBackButton(BACK_BUTTON_SLOT, (p, t) -> {
                    currentPage--;
                    manager.openMenu(this, p);
                });
            } else {
                setBackButton(BACK_BUTTON_SLOT, new CraftingTableMenu());
            }

            if (end < totalItems) {
                setItem(NEXT_BUTTON_SLOT, MenuElementPreset.NEXT_BUTTON, (p, t) -> {
                    currentPage++;
                    manager.openMenu(this, p);
                });
            } else {
                setItem(NEXT_BUTTON_SLOT, MenuElementPreset.EMPTY_SLOT_FILLER.getItem(), (p, t) -> {
                });
            }

            setCloseButton(CLOSE_BUTTON_SLOT);
        }

        @Override
        protected void onItemClick(@NonNull Player player, int index, @NonNull ClickType clickType) {
            if (index >= cachedRecipes.size()) return;

            manager.openMenu(new CraftingRecipePreviewMenu(cachedRecipes.get(index), this), player);
        }
    }
}
