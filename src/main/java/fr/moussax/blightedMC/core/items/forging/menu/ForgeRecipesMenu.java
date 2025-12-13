package fr.moussax.blightedMC.core.items.forging.menu;

import fr.moussax.blightedMC.core.items.crafting.CraftingObject;
import fr.moussax.blightedMC.core.items.forging.ForgeRecipe;
import fr.moussax.blightedMC.core.menus.*;
import fr.moussax.blightedMC.utils.formatting.Formatter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    /**
     * Constructs a ForgeRecipesMenu.
     *
     * @param previousMenu the menu to return to when pressing back
     */
    public ForgeRecipesMenu(Menu previousMenu) {
        super("Forge Recipes", 54);
        this.previousMenu = previousMenu;
    }

    @Override
    protected int getTotalItems(Player player) {
        return ForgeRecipe.REGISTERED_RECIPES.size();
    }

    @Override
    protected int getItemsPerPage() {
        return RECIPE_SLOTS.length;
    }

    @Override
    protected ItemStack getItem(Player player, int index) {
        List<ForgeRecipe> recipes = new ArrayList<>(ForgeRecipe.REGISTERED_RECIPES);
        if (index >= recipes.size()) {
            return new ItemStack(Material.AIR);
        }

        ForgeRecipe recipe = recipes.get(index);
        return buildRecipeDisplayItem(recipe);
    }

    @Override
    public void build(Player player) {
        totalItems = getTotalItems(player);
        int start = currentPage * getItemsPerPage();
        int end = Math.min(start + getItemsPerPage(), totalItems);

        clearInventory();
        populateRecipeSlots(player, start, end);
        populateFillerSlots();
        setupNavigationButtons(player, end);
    }

    @Override
    protected void onItemClick(Player player, int index, org.bukkit.event.inventory.ClickType clickType) {
        List<ForgeRecipe> recipes = new ArrayList<>(ForgeRecipe.REGISTERED_RECIPES);
        if (index >= recipes.size()) {
            return;
        }

        ForgeRecipe recipe = recipes.get(index);
        MenuManager.openMenu(new ForgeMenu(recipe, this), player);
    }

    private ItemStack buildRecipeDisplayItem(ForgeRecipe recipe) {
        ItemStack resultItem = recipe.result().toItemStack().clone();
        resultItem.setAmount(recipe.amount());

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

        for (CraftingObject ingredient : recipe.ingredients()) {
            String ingredientName = extractIngredientName(ingredient);
            lore.add(" §8‣ " + ingredientName + " §8x" + ingredient.getAmount());
        }

        lore.add("");
        lore.add(" §8Consumes §6🪣 " + Formatter.formatDecimalWithCommas(recipe.fuelCost()) + " mB ");
        lore.add(" §8of fuel to forge.");
        lore.add("");
        lore.add("§eClick to select!");
    }

    private String extractIngredientName(CraftingObject ingredient) {
        if (ingredient.isCustom()) {
            return Objects.requireNonNull(ingredient.getManager().toItemStack().getItemMeta()).getDisplayName();
        }
        return "§f" + Formatter.formatEnumName(ingredient.getVanillaItem().getType().name());
    }

    private void clearInventory() {
        for (int i = 0; i < size; i++) {
            setItem(i, new ItemStack(Material.AIR), MenuItemInteraction.ANY_CLICK, (_, _) -> {
            });
        }
    }

    private void populateRecipeSlots(Player player, int start, int end) {
        List<ForgeRecipe> recipes = new ArrayList<>(ForgeRecipe.REGISTERED_RECIPES);
        int recipeIndex = 0;

        for (int i = start; i < end && recipeIndex < RECIPE_SLOTS.length; i++) {
            if (i >= recipes.size()) {
                break;
            }

            final int itemIndex = i;
            setItem(RECIPE_SLOTS[recipeIndex], getItem(player, itemIndex), MenuItemInteraction.ANY_CLICK,
                (p, t) -> onItemClick(p, itemIndex, t));
            recipeIndex++;
        }
    }

    private void populateFillerSlots() {
        ItemStack filler = MenuElementPreset.EMPTY_SLOT_FILLER.getItem();
        for (int slot : FILLER_SLOTS) {
            setItem(slot, filler, MenuItemInteraction.ANY_CLICK, (_, _) -> {
            });
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
