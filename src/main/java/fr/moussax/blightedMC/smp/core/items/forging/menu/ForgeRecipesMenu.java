package fr.moussax.blightedMC.smp.core.items.forging.menu;

import fr.moussax.blightedMC.smp.core.items.crafting.CraftingObject;
import fr.moussax.blightedMC.smp.core.items.forging.ForgeRecipe;
import fr.moussax.blightedMC.smp.core.items.forging.registry.ForgeRegistry;
import fr.moussax.blightedMC.smp.core.shared.menu.*;
import fr.moussax.blightedMC.smp.core.shared.menu.interaction.MenuElementPreset;
import fr.moussax.blightedMC.smp.core.shared.menu.interaction.MenuItemInteraction;
import fr.moussax.blightedMC.utils.Utilities;
import fr.moussax.blightedMC.utils.formatting.Formatter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Paginated menu displaying all registered forge recipes.
 * <p>
 * Allows players to browse available forge recipes, view required ingredients
 * and fuel cost, and select a recipe to open the {@link ForgeMenu} for forging.
 * Navigation buttons for paging, closing, and returning to a previous menu are included.
 * </p>
 */
public class ForgeRecipesMenu extends PaginatedMenu {
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

    /**
     * Constructs a ForgeRecipesMenu.
     *
     * @param previousMenu the menu to return to when pressing back
     */
    public ForgeRecipesMenu(Menu previousMenu) {
        super("Forge Recipes", 54);
        this.previousMenu = previousMenu;
        this.cachedRecipes = new ArrayList<>(ForgeRegistry.RECIPES);
        this.cachedRecipes.sort((r1, r2) -> {
            String name1 = r1.getForgedItem().getDisplayName();
            String name2 = r2.getForgedItem().getDisplayName();
            return (name1 != null ? name1 : "").compareTo(name2 != null ? name2 : "");
        });
    }

    @Override
    protected int getTotalItems(Player player) {
        return cachedRecipes.size();
    }

    @Override
    protected int getItemsPerPage() {
        return RECIPE_SLOTS.length;
    }

    @Override
    protected ItemStack getItem(Player player, int index) {
        if (index >= cachedRecipes.size()) {
            return new ItemStack(Material.AIR);
        }

        ForgeRecipe recipe = cachedRecipes.get(index);
        return buildRecipeDisplayItem(recipe);
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

    @Override
    protected void onItemClick(Player player, int index, ClickType clickType) {
        if (index >= cachedRecipes.size()) {
            return;
        }

        ForgeRecipe recipe = cachedRecipes.get(index);
        MenuManager.openMenu(new ForgeMenu(recipe, this), player);
    }

    private ItemStack buildRecipeDisplayItem(ForgeRecipe recipe) {
        ItemStack resultItem = recipe.getForgedItem().toItemStack().clone();
        resultItem.setAmount(recipe.getForgedAmount());

        ItemMeta meta = resultItem.getItemMeta();
        if (meta == null) {
            return resultItem;
        }

        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }

        appendRecipeLore(lore, recipe);
        meta.setLore(lore);
        resultItem.setItemMeta(meta);

        return resultItem;
    }

    private void appendRecipeLore(List<String> lore, ForgeRecipe recipe) {
        lore.add("");
        lore.add(" §7Items required:");

        for (CraftingObject ingredient : recipe.getIngredients()) {
            String ingredientName = Utilities.extractIngredientName(ingredient);
            lore.add(" §8‣ " + ingredientName + " §8x" + ingredient.getAmount());
        }

        lore.add("");
        lore.add(" §8Consumes §6🪣 " + Formatter.formatDecimalWithCommas(recipe.getFuelCost()) + " mB §8of ");
        lore.add(" §8thermal fuel to forge.");
        lore.add("");
        lore.add("§eClick to select!");
    }

    private void populateRecipeSlots(Player player, int start, int end) {
        int recipeIndex = 0;

        for (int i = start; i < end && recipeIndex < RECIPE_SLOTS.length; i++) {
            if (i >= cachedRecipes.size()) {
                break;
            }

            final int itemIndex = i;
            setItem(RECIPE_SLOTS[recipeIndex], getItem(player, itemIndex), MenuItemInteraction.ANY_CLICK,
                (p, t) -> onItemClick(p, itemIndex, t));
            recipeIndex++;
        }
    }

    private void setupNavigationButtons(Player player, int end) {
        setupBackButton();
        setupNextButton(end);
        setupCloseButton();
    }

    private void setupBackButton() {
        if (currentPage > 0) {
            setItem(BACK_BUTTON_SLOT, MenuElementPreset.BACK_BUTTON, MenuItemInteraction.ANY_CLICK, (p, _) -> {
                currentPage--;
                MenuManager.openMenu(this, p);
            });
            return;
        }

        if (previousMenu != null) {
            setItem(BACK_BUTTON_SLOT, MenuElementPreset.BACK_BUTTON, MenuItemInteraction.ANY_CLICK, (p, _) -> {
                close();
                MenuManager.openMenu(previousMenu, p);
            });
        }
    }

    private void setupNextButton(int end) {
        if (end < totalItems) {
            setItem(NEXT_BUTTON_SLOT, MenuElementPreset.NEXT_BUTTON, MenuItemInteraction.ANY_CLICK, (p, _) -> {
                currentPage++;
                MenuManager.openMenu(this, p);
            });
        } else {
            setItem(NEXT_BUTTON_SLOT, MenuElementPreset.EMPTY_SLOT_FILLER.getItem(), MenuItemInteraction.ANY_CLICK, (_, _) -> {
            });
        }
    }

    private void setupCloseButton() {
        setItem(CLOSE_BUTTON_SLOT, MenuElementPreset.CLOSE_BUTTON, MenuItemInteraction.ANY_CLICK, (_, _) -> close());
    }
}
