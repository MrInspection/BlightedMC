package fr.moussax.blightedMC.engine.items.recipes.forging.menu;

import fr.moussax.blightedMC.engine.items.recipes.CraftingObject;
import fr.moussax.blightedMC.engine.items.recipes.forging.ForgeRecipe;
import fr.moussax.blightedMC.engine.items.recipes.RecipePreviewManager;
import fr.moussax.blightedMC.shared.ui.menu.Menu;
import fr.moussax.blightedMC.shared.ui.menu.interaction.MenuElementPreset;
import fr.moussax.blightedMC.shared.ui.menu.interaction.MenuItemInteraction;
import fr.moussax.blightedMC.utils.Formatter;
import fr.moussax.blightedMC.utils.ItemBuilder;
import fr.moussax.blightedMC.utils.Utilities;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public final class ForgeRecipePreviewMenu extends Menu {

    private static final int[] GRID_SLOTS = {19, 20, 21, 28, 29, 30, 37, 38, 39};
    private static final int[] REQUIRED_ITEM_INDICATOR_SLOTS = {10, 11, 12, 13};
    private static final int[] FORGED_ITEM_INDICATOR_SLOTS = {15, 16};
    private static final int FORGE_SLOT = 14;
    private static final int ITEM_INDICATOR = 23;
    private static final int RESULT_SLOT = 25;
    private static final int ANVIL_SLOT = 32;
    private static final int FUEL_INFO_SLOT = 34;
    private static final int BACK_BUTTON_SLOT = 48;
    private static final int CLOSE_BUTTON_SLOT = 49;
    private static final int FUEL_GUIDE_SLOT = 52;

    private final ForgeRecipe recipe;
    private final Menu previousMenu;

    public ForgeRecipePreviewMenu(@NonNull ForgeRecipe recipe, @Nullable Menu previousMenu) {
        super(recipe.getForgedItem().getDisplayName().replaceAll("§[0-9A-FK-ORa-fk-or]", "") + " Recipe", 54);
        this.recipe = recipe;
        this.previousMenu = previousMenu;
    }

    public ForgeRecipePreviewMenu(@NonNull ForgeRecipe recipe) {
        this(recipe, null);
    }

    @Override
    public void build(Player player) {
        setupRecipeVisualization();
        setupNavigation();
    }

    private void setupRecipeVisualization() {
        setupStatusPanes();
        setupIngredientGrid();
        setupResultDisplay();
        setupAnvilDisplay();
        setupFuelDisplay();
        setupFuelGuide();
    }

    private void setupStatusPanes() {
        Material indicator = Material.ORANGE_STAINED_GLASS_PANE;

        ItemStack requiredItemsPane = new ItemBuilder(indicator, "§6Items Required")
                .addLore("§7The materials required to forge", "§7the item are displayed below.")
                .toItemStack();

        ItemStack forgedItemPane = new ItemBuilder(indicator, "§6Item to Forge")
                .addLore("§7The item you will create", "§7with the required materials.")
                .toItemStack();

        fillSlots(REQUIRED_ITEM_INDICATOR_SLOTS, requiredItemsPane);
        fillSlots(FORGED_ITEM_INDICATOR_SLOTS, forgedItemPane);

        setItem(ITEM_INDICATOR, new ItemBuilder(indicator).hideTooltip().toItemStack(), (_, _) -> {
        });

        setItem(FORGE_SLOT, new ItemBuilder(Material.BLAST_FURNACE, "§6Blighted Forge")
                .addLore("§7Forge this recipe using a", "§7blighted forge.")
                .toItemStack(), (_, _) -> {
        });
    }

    private void setupIngredientGrid() {
        ItemBuilder emptySlotBuilder = new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE, "§cUnused Slot")
                .addLore("§7This slot isn't used for", "§7the selected recipe.");
        fillSlots(GRID_SLOTS, emptySlotBuilder.toItemStack());

        List<CraftingObject> ingredients = recipe.getIngredients();
        for (int i = 0; i < ingredients.size() && i < GRID_SLOTS.length; i++) {
            CraftingObject ingredient = ingredients.get(i);
            ItemStack displayItem = createIngredientDisplay(ingredient);

            setItem(GRID_SLOTS[i], displayItem, MenuItemInteraction.ANY_CLICK, (p, _) -> {
                // ponytail: kept
                if (ingredient.isCustom() && ingredient.getManager() != null) {
                    RecipePreviewManager.openPreview(p, ingredient.getManager(), this);
                }
            });
        }
    }

    private ItemStack createIngredientDisplay(CraftingObject ingredient) {
        ItemStack displayItem = ingredient.isCustom()
                ? Objects.requireNonNull(ingredient.getManager()).toItemStack().clone()
                : Objects.requireNonNull(ingredient.getVanillaItem()).clone();
        displayItem.setAmount(Math.max(1, ingredient.getAmount()));
        return displayItem;
    }

    private void setupResultDisplay() {
        ItemStack result = recipe.getForgedItem().toItemStack().clone();
        result.setAmount(Math.max(1, recipe.getForgedAmount()));
        setItem(RESULT_SLOT, result, MenuItemInteraction.ANY_CLICK, (_, _) -> {
        });
    }

    private void setupAnvilDisplay() {
        ItemBuilder builder = new ItemBuilder(Material.ANVIL, "§aForge Requirements");
        builder.addLore("", " §7Items required: ");
        for (CraftingObject ingredient : recipe.getIngredients()) {
            builder.addLore(" §8‣ " + Utilities.extractIngredientName(ingredient) + " §8x" + ingredient.getAmount());
        }
        builder.addLore(
                "",
                " §8Consumes §6🪣 " + Formatter.formatDecimalWithCommas(recipe.getFuelCost()) + " mB §8of fuel to ",
                " §8start the forging process.",
                ""
        );

        setItem(ANVIL_SLOT, builder.toItemStack(), MenuItemInteraction.ANY_CLICK, (_, _) -> {
        });
    }

    private void setupFuelDisplay() {
        ItemStack fuelInfo = new ItemBuilder(Material.CAMPFIRE, "§6Fuel Requirement")
                .addLore(
                        "§7Requires §6🪣 " + Formatter.formatDecimalWithCommas(recipe.getFuelCost()) + " mB §7of fuel",
                        "§7to power the forging process."
                )
                .toItemStack();
        setItem(FUEL_INFO_SLOT, fuelInfo, MenuItemInteraction.ANY_CLICK, (_, _) -> {
        });
    }

    private void setupFuelGuide() {
        ItemStack fuelGuide = new ItemBuilder(Material.BLAZE_POWDER, "§6Thermal Fuel")
                .addLore(
                        "§8Measured in millibuckets (mB)", "",
                        " §7There are various types of §6Thermal Fuel ",
                        " §7that you can use to power your Forge.",
                        " §7Each offers various §6🪣 mB §7of fuel",
                        " §7based on their §c🔥 heat strength§7.",
                        " ",
                        "   §8‣ §fCoal §8- §610mB",
                        "   §8‣ §fMagma Block §8- §640mB",
                        "   §8‣ §fBlaze Rod §8- §6200mB",
                        "   §8‣ §fLava Bucket §8- §61,000mB",
                        "   §8‣ §eEnchanted Coal §8- §63,000mB",
                        "   §8‣ §bEnchanted Lava Bucket §8- §610,000mB ",
                        "   §8‣ §dMagma Bucket §8- §620,000mB",
                        "   §8‣ §cPlasma Bucket §8- §650,000mB",
                        ""
                )
                .addEnchantmentGlint()
                .toItemStack();

        setItem(FUEL_GUIDE_SLOT, fuelGuide, MenuItemInteraction.ANY_CLICK, (_, _) -> {
        });
    }

    private void setupNavigation() {
        if (previousMenu != null) {
            setBackButton(BACK_BUTTON_SLOT, previousMenu);
        }
        setCloseButton(CLOSE_BUTTON_SLOT);

        fillEmptyWith(MenuElementPreset.EMPTY_SLOT_FILLER);
    }
}
