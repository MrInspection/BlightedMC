package fr.moussax.blightedMC.engine.items.recipes.crafting.menu;


import fr.moussax.blightedMC.engine.items.recipes.CraftingObject;
import fr.moussax.blightedMC.engine.items.recipes.RecipePreviewManager;
import fr.moussax.blightedMC.engine.items.recipes.crafting.BlightedRecipe;
import fr.moussax.blightedMC.engine.items.recipes.crafting.BlightedShapedRecipe;
import fr.moussax.blightedMC.engine.items.recipes.crafting.BlightedShapelessRecipe;
import fr.moussax.blightedMC.shared.ui.menu.Menu;
import fr.moussax.blightedMC.shared.ui.menu.interaction.MenuElementPreset;
import fr.moussax.blightedMC.shared.ui.menu.interaction.MenuItemInteraction;
import fr.moussax.blightedMC.utils.Formatter;
import fr.moussax.blightedMC.utils.ItemBuilder;
import fr.moussax.blightedMC.utils.Utilities;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class CraftingRecipePreviewMenu extends Menu {

    private static final int[] CRAFTING_GRID_SLOTS = {
            10, 11, 12,
            19, 20, 21,
            28, 29, 30
    };

    private static final int WORKBENCH_SLOT = 23;
    private static final int RESULT_SLOT = 25;
    private static final int SUPERCRAFT_SLOT = 32;
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
        setupRecipeVisualization(player);
        setupNavigation();
    }

    private void setupRecipeVisualization(Player player) {
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

        setupSupercraftButton(player);
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

    private void setupSupercraftButton(Player player) {
        Map<String, IngredientInfo> requirements = aggregateRecipeIngredients(recipe);
        Map<String, Integer> inventoryCounts = countInventoryItems(player, requirements.keySet());

        boolean hasAllIngredients = true;
        ItemBuilder builder = new ItemBuilder(Material.GOLDEN_PICKAXE, "§aSupercraft");
        builder.addLore(
                "§7Craft this item instantly from ",
                "§7your inventory materials.",
                "",
                " §7Ingredients required:"
        );

        for (Map.Entry<String, IngredientInfo> entry : requirements.entrySet()) {
            IngredientInfo info = entry.getValue();
            int owned = inventoryCounts.getOrDefault(entry.getKey(), 0);
            boolean hasEnough = owned >= info.amount;

            if (!hasEnough) {
                hasAllIngredients = false;
            }

            String status = hasEnough ? "§a✓" : "§cx";
            String countColor = hasEnough ? "§a" : "§c";
            String name = Utilities.extractIngredientName(info.ingredient);

            builder.addLore(" " + status + " §7" + name + " §8x" + info.amount + " §7(" + countColor + owned + "§7/§a" + info.amount + "§7)");
        }

        final boolean canSupercraft = hasAllIngredients;

        builder.addLore("", canSupercraft ? "§eClick to Supercraft!" : "§cYou don't meet the requirements!")
                .setEnchantmentGlint(canSupercraft);
        builder.addItemFlag(ItemFlag.HIDE_ATTRIBUTES);

        setItem(SUPERCRAFT_SLOT, builder.toItemStack(), MenuItemInteraction.ANY_CLICK, (p, t) -> {
            if (canSupercraft) {
                executeSupercraft(p, requirements);
            } else {
                p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 0.5f);
                Formatter.warn(p, "You don't meet the requirements to supercraft this item!");
            }
        });
    }

    private void executeSupercraft(Player player, Map<String, IngredientInfo> requirements) {
        Map<String, Integer> inventoryCounts = countInventoryItems(player, requirements.keySet());
        boolean verified = requirements.entrySet().stream()
                .allMatch(entry -> inventoryCounts.getOrDefault(entry.getKey(), 0) >= entry.getValue().amount);

        if (!verified) {
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 0.5f);
            Formatter.warn(player, "You don't meet the requirements to supercraft this item!");
            refresh(player);
            return;
        }

        for (IngredientInfo info : requirements.values()) {
            CraftingObject consumeObject = info.ingredient.isCustom()
                    ? new CraftingObject(Objects.requireNonNull(info.ingredient.getManager()), info.amount)
                    : new CraftingObject(Objects.requireNonNull(info.ingredient.getVanillaItem()).getType(), info.amount);
            Utilities.consumeItemsFromInventory(player, consumeObject);
        }

        ItemStack result = recipe.getResult().toItemStack().clone();
        int amount = recipe.getAmount() > 0 ? recipe.getAmount() : 1;
        result.setAmount(amount);

        HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(result);
        for (ItemStack drop : leftover.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), drop);
        }

        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
        Formatter.inform(player, "§aSupercrafted " + recipe.getResult().getDisplayName() + " §ax" + amount + "!");

        refresh(player);
    }

    private Map<String, IngredientInfo> aggregateRecipeIngredients(BlightedRecipe recipe) {
        Map<String, IngredientInfo> map = new LinkedHashMap<>();
        List<CraftingObject> rawList;
        if (recipe instanceof BlightedShapedRecipe shapedRecipe) {
            rawList = shapedRecipe.getRecipe().stream().filter(Objects::nonNull).toList();
        } else if (recipe instanceof BlightedShapelessRecipe shapelessRecipe) {
            rawList = shapelessRecipe.getIngredients().stream().filter(Objects::nonNull).toList();
        } else {
            rawList = List.of();
        }

        for (CraftingObject ing : rawList) {
            String id = ing.getId();
            if (id.isEmpty()) continue;
            if (!map.containsKey(id)) {
                map.put(id, new IngredientInfo(ing, ing.getAmount()));
            } else {
                IngredientInfo info = map.get(id);
                info.amount += ing.getAmount();
            }
        }
        return map;
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
