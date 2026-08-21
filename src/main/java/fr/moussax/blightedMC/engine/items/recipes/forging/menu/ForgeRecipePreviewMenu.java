package fr.moussax.blightedMC.engine.items.recipes.forging.menu;

import fr.moussax.blightedMC.engine.items.recipes.CraftingObject;
import fr.moussax.blightedMC.engine.items.recipes.RecipePreviewManager;
import fr.moussax.blightedMC.engine.items.recipes.forging.ForgeRecipe;
import fr.moussax.blightedMC.engine.player.BlightedPlayer;
import fr.moussax.blightedMC.shared.ui.menu.Menu;
import fr.moussax.blightedMC.shared.ui.menu.interaction.MenuElementPreset;
import fr.moussax.blightedMC.shared.ui.menu.interaction.MenuItemInteraction;
import fr.moussax.blightedMC.utils.Formatter;
import fr.moussax.blightedMC.utils.ItemBuilder;
import fr.moussax.blightedMC.utils.Utilities;
import fr.moussax.blightedMC.utils.sound.SoundSequence;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class ForgeRecipePreviewMenu extends Menu {

    private static final int[] GRID_SLOTS = {19, 20, 21, 28, 29, 30, 37, 38, 39};
    private static final int[] REQUIRED_ITEM_INDICATOR_SLOTS = {10, 11, 12, 13};
    private static final int[] FORGED_ITEM_INDICATOR_SLOTS = {15, 16};
    private static final int FORGE_SLOT = 14;
    private static final int ITEM_INDICATOR = 23;
    private static final int RESULT_SLOT = 25;
    private static final int HYPERFORGE_SLOT = 32;
    private static final int FUEL_INFO_SLOT = 34;
    private static final int BACK_BUTTON_SLOT = 48;
    private static final int CLOSE_BUTTON_SLOT = 49;
    private static final int FUEL_GUIDE_SLOT = 50;

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
        setupRecipeVisualization(player);
        setupNavigation();
    }

    private void setupRecipeVisualization(Player player) {
        setupStatusPanes(player);
        setupIngredientGrid();
        setupResultDisplay();
        setupFuelDisplay();
        setupFuelGuide();
        setupHyperforgeButton(player);
    }

    private void setupStatusPanes(Player player) {
        boolean canHyperforge = checkCanHyperforge(player);
        Material indicator = canHyperforge ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE;

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

    private boolean checkCanHyperforge(Player player) {
        BlightedPlayer blightedPlayer = BlightedPlayer.getBlightedPlayer(player);
        int currentFuel = blightedPlayer != null ? blightedPlayer.getForgeFuel() : 0;
        int fuelCost = recipe.getFuelCost();
        boolean hasSufficientFuel = currentFuel >= fuelCost;

        Map<String, IngredientInfo> requirements = aggregateForgeIngredients(recipe);
        Map<String, Integer> inventoryCounts = countInventoryItems(player, requirements.keySet());

        return hasSufficientFuel && requirements.entrySet().stream()
                .allMatch(entry -> inventoryCounts.getOrDefault(
                        entry.getKey(), 0) >= entry.getValue().amount);
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

    private void setupFuelDisplay() {
        ItemStack fuelInfo = new ItemBuilder(Material.CAMPFIRE, "§6Fuel Requirement")
                .addLore(
                        "§7Requires §6🪣 " + Formatter.formatDecimalWithCommas(recipe.getFuelCost()) + "mB §7of thermal fuel",
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

    private void setupHyperforgeButton(Player player) {
        BlightedPlayer blightedPlayer = BlightedPlayer.getBlightedPlayer(player);
        int currentFuel = blightedPlayer != null ? blightedPlayer.getForgeFuel() : 0;
        int fuelCost = recipe.getFuelCost();
        boolean hasSufficientFuel = currentFuel >= fuelCost;

        Map<String, IngredientInfo> requirements = aggregateForgeIngredients(recipe);
        Map<String, Integer> inventoryCounts = countInventoryItems(player, requirements.keySet());

        boolean hasAllIngredients = true;
        ItemBuilder builder = new ItemBuilder(Material.GOLDEN_PICKAXE, "§aHyperforge");
        builder.addLore(
                "§7Forge this item instantly from your",
                "§7inventory materials & thermal fuel.",
                "",
                " §7Requirements:"
        );

        for (Map.Entry<String, IngredientInfo> entry : requirements.entrySet()) {
            String ingredientId = entry.getKey();
            IngredientInfo info = entry.getValue();
            int requiredAmount = info.amount;
            int owned = inventoryCounts.getOrDefault(ingredientId, 0);
            boolean hasEnough = owned >= requiredAmount;

            if (!hasEnough) {
                hasAllIngredients = false;
            }

            String status = hasEnough ? "§a✓" : "§cx";
            String countColor = hasEnough ? "§a" : "§c";
            String name = Utilities.extractIngredientName(info.ingredient);

            builder.addLore(String.format(" %s §7%s §8x%d §7(%s%d§7/§a%d§7)",
                    status, name, requiredAmount, countColor, owned, requiredAmount));
        }

        String fuelStatus = hasSufficientFuel ? "§a✓" : "§cx";
        String fuelCountColor = hasSufficientFuel ? "§a" : "§c";
        builder.addLore(
                " " + fuelStatus + " §7Thermal Fuel §8" + Formatter.formatDecimalWithCommas(fuelCost) + "mB §7(" + fuelCountColor + Formatter.formatDecimalWithCommas(currentFuel) + "§7/§a" + Formatter.formatDecimalWithCommas(fuelCost) + "mB§7)"
        );

        final boolean canHyperforge = hasAllIngredients && hasSufficientFuel;
        builder.addItemFlag(ItemFlag.HIDE_ATTRIBUTES);
        builder.addLore(
                "",
                canHyperforge ? "§eClick to Hyperforge!" : "§cYou don't meet the requirements!"
        ).setEnchantmentGlint(canHyperforge);


        setItem(HYPERFORGE_SLOT, builder.toItemStack(), MenuItemInteraction.ANY_CLICK, (p, _) -> {
            if (canHyperforge) {
                executeHyperforge(p, blightedPlayer, requirements, fuelCost);
            } else {
                Formatter.warn(p, "You don't meet the requirements to hyperforge this item!");
            }
        });
    }

    private void executeHyperforge(Player player, BlightedPlayer blightedPlayer, Map<String, IngredientInfo> requirements, int fuelCost) {
        if (blightedPlayer == null) return;

        Map<String, Integer> inventoryCounts = countInventoryItems(player, requirements.keySet());
        boolean verifiedIngredients = requirements.entrySet().stream()
                .allMatch(entry -> inventoryCounts.getOrDefault(entry.getKey(), 0) >= entry.getValue().amount);
        boolean verifiedFuel = blightedPlayer.getForgeFuel() >= fuelCost;

        if (!verifiedIngredients || !verifiedFuel) {
            Formatter.warn(player, "You don't meet the requirements to hyperforge this item!");
            refresh(player);
            return;
        }

        for (IngredientInfo info : requirements.values()) {
            CraftingObject consumeObject = info.ingredient.isCustom()
                    ? new CraftingObject(Objects.requireNonNull(info.ingredient.getManager()), info.amount)
                    : new CraftingObject(Objects.requireNonNull(info.ingredient.getVanillaItem()).getType(), info.amount);
            Utilities.consumeItemsFromInventory(player, consumeObject);
        }

        blightedPlayer.removeForgeFuel(fuelCost);
        blightedPlayer.saveData();

        ItemStack result = recipe.getForgedItem().toItemStack().clone();
        int amount = Math.max(1, recipe.getForgedAmount());
        result.setAmount(amount);

        HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(result);
        for (ItemStack drop : leftover.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), drop);
        }

        SoundSequence.FORGE_ITEM.play(player.getLocation());
        refresh(player);
    }

    private Map<String, IngredientInfo> aggregateForgeIngredients(ForgeRecipe recipe) {
        Map<String, IngredientInfo> recipeIngredients = new LinkedHashMap<>();
        for (CraftingObject ingredient : recipe.getIngredients()) {
            if (ingredient == null) continue;
            String ingredientId = ingredient.getId();
            if (ingredientId.isEmpty()) continue;
            if (!recipeIngredients.containsKey(ingredientId)) {
                recipeIngredients.put(ingredientId, new IngredientInfo(ingredient, ingredient.getAmount()));
            } else {
                IngredientInfo info = recipeIngredients.get(ingredientId);
                info.amount += ingredient.getAmount();
            }
        }
        return recipeIngredients;
    }

    private Map<String, Integer> countInventoryItems(Player player, Set<String> requiredIds) {
        Map<String, Integer> counts = new HashMap<>();
        for (ItemStack stack : player.getInventory().getContents()) {
            if (stack == null || stack.getType() == Material.AIR) continue;
            for (String reqId : requiredIds) {
                if (Utilities.resolveItemId(stack, reqId).equals(reqId)) {
                    counts.put(reqId, counts.getOrDefault(reqId, 0) + stack.getAmount());
                }
            }
        }
        return counts;
    }

    private static class IngredientInfo {
        final CraftingObject ingredient;
        int amount;

        IngredientInfo(CraftingObject ingredient, int amount) {
            this.ingredient = ingredient;
            this.amount = amount;
        }
    }

    private void setupNavigation() {
        if (previousMenu != null) {
            setBackButton(BACK_BUTTON_SLOT, previousMenu);
        }
        setCloseButton(CLOSE_BUTTON_SLOT);
        fillEmptyWith(MenuElementPreset.EMPTY_SLOT_FILLER);
    }
}
