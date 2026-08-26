package fr.moussax.blightedMC.engine.items.recipes.forging.menu;

import fr.moussax.blightedMC.engine.items.recipes.CraftingObject;
import fr.moussax.blightedMC.engine.items.recipes.forging.ForgeRecipe;
import fr.moussax.blightedMC.engine.items.recipes.forging.registry.ForgeRegistry;
import fr.moussax.blightedMC.shared.text.Formatter;
import fr.moussax.blightedMC.shared.ui.menu.Menu;
import fr.moussax.blightedMC.shared.ui.menu.types.PaginatedMenu;
import fr.moussax.blightedMC.shared.ui.menu.interaction.MenuElementPreset;
import fr.moussax.blightedMC.utils.ItemBuilder;
import fr.moussax.blightedMC.utils.Utilities;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class ForgeRecipesMenu extends PaginatedMenu {

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

    private static final int BACK_BUTTON_SLOT = 48;
    private static final int CLOSE_BUTTON_SLOT = 49;
    private static final int NEXT_BUTTON_SLOT = 50;

    private final Menu previousMenu;
    private final List<ForgeRecipe> cachedRecipes;

    public ForgeRecipesMenu(Menu previousMenu) {
        super("Forge Recipes", 54);
        this.previousMenu = previousMenu;
        this.cachedRecipes = new ArrayList<>(ForgeRegistry.RECIPES);
        this.cachedRecipes.sort(Comparator.comparing(
                recipe -> recipe.getForgedItem().getDisplayName() != null
                        ? recipe.getForgedItem().getDisplayName()
                        : ""
        ));
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
        if (index >= cachedRecipes.size()) {
            return new ItemStack(Material.AIR);
        }

        ForgeRecipe recipe = cachedRecipes.get(index);
        return buildRecipeDisplayItem(recipe);
    }

    @Override
    public void build(@NonNull Player player) {
        totalItems = Math.max(0, getTotalItems(player));
        int itemsPerPage = getItemsPerPage();
        int maxPage = Math.max(0, (totalItems - 1) / itemsPerPage);
        currentPage = Math.min(currentPage, maxPage);

        int start = currentPage * itemsPerPage;
        int end = Math.min(start + itemsPerPage, totalItems);

        populateRecipeSlots(player, start, end);
        fillSlots(FILLER_SLOTS, MenuElementPreset.EMPTY_SLOT_FILLER);
        setupNavigationButtons(end);
    }

    @Override
    protected void onItemClick(@NonNull Player player, int index, @NonNull ClickType clickType) {
        if (index >= cachedRecipes.size()) {
            return;
        }

        ForgeRecipe recipe = cachedRecipes.get(index);
        openSubMenu(new ForgeMenu(recipe, this));
    }

    private ItemStack buildRecipeDisplayItem(ForgeRecipe recipe) {
        ItemBuilder builder = new ItemBuilder(recipe.getForgedItem().toItemStack().clone());
        builder.setAmount(recipe.getForgedAmount());

        builder.addLore("", " §7Items required:");
        for (CraftingObject ingredient : recipe.getIngredients()) {
            builder.addLore(" §8‣ " + Utilities.extractIngredientName(ingredient) + " §8x" + ingredient.getAmount());
        }

        builder.addLore(
                "",
                " §8Consumes §6🪣 " + Formatter.formatDecimalWithCommas(recipe.getFuelCost()) + "mB §8of ",
                " §8thermal fuel to forge.",
                "",
                "§eClick to select!"
        );

        return builder.toItemStack();
    }

    private void populateRecipeSlots(Player player, int start, int end) {
        int recipeIndex = 0;
        for (int i = start; i < end && recipeIndex < RECIPE_SLOTS.length; i++) {
            final int itemIndex = i;
            setItem(RECIPE_SLOTS[recipeIndex], getItem(player, itemIndex),
                    (p, click) -> onItemClick(p, itemIndex, click));
            recipeIndex++;
        }
    }

    private void setupNavigationButtons(int end) {
        if (currentPage > 0) {
            setBackButton(BACK_BUTTON_SLOT, (p, _) -> {
                currentPage--;
                refresh(p);
            });
        } else if (previousMenu != null) {
            setBackButton(BACK_BUTTON_SLOT, previousMenu);
        }

        if (end < totalItems) {
            setItem(NEXT_BUTTON_SLOT, MenuElementPreset.NEXT_BUTTON, (p, _) -> {
                currentPage++;
                refresh(p);
            });
        } else {
            setItem(NEXT_BUTTON_SLOT, MenuElementPreset.EMPTY_SLOT_FILLER.getItem(), (_, _) -> {
            });
        }

        setCloseButton(CLOSE_BUTTON_SLOT);
    }
}
