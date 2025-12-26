package fr.moussax.blightedMC.smp.core.items.crafting.menu;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.smp.core.items.crafting.BlightedRecipe;
import fr.moussax.blightedMC.smp.core.items.crafting.BlightedShapedRecipe;
import fr.moussax.blightedMC.smp.core.items.crafting.BlightedShapelessRecipe;
import fr.moussax.blightedMC.smp.core.items.crafting.CraftingObject;
import fr.moussax.blightedMC.smp.core.menus.Menu;
import fr.moussax.blightedMC.smp.core.menus.MenuManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CraftingTableListener implements Listener {

    private static final String CRAFTING_MENU_TITLE = "§rCraft Items";
    private static final int OUTPUT_SLOT = 23;
    private static final int RECIPE_BOOK_SLOT = 25;
    private static final int CLOSE_BUTTON_SLOT = 49;
    private static final int[] INPUT_SLOTS = {10, 11, 12, 19, 20, 21, 28, 29, 30};
    private static final int[] INDICATOR_SLOTS_LEFT = {45, 46, 47, 48};
    private static final int[] INDICATOR_SLOTS_RIGHT = {50, 51, 52, 53};
    private static final int SCHEDULED_UPDATE_DELAY = 1;

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (isCraftingMenu(event.getView().getTitle())) return;
        scheduleOutputUpdate(event.getView().getTopInventory());
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (isCraftingMenu(event.getView().getTitle())) return;

        Inventory topInventory = event.getView().getTopInventory();
        Player player = (Player) event.getWhoClicked();
        int rawSlot = event.getRawSlot();

        if (rawSlot < topInventory.getSize()) {
            if (handleGuiButtonClick(event, player, rawSlot)) {
                return;
            }
        }

        if (event.getClickedInventory() != null && event.getClickedInventory().equals(topInventory)) {
            handleCraftingInventoryClick(event, topInventory, player);
        }

        scheduleOutputUpdate(topInventory);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType() != Material.CRAFTING_TABLE) return;

        event.getPlayer().openInventory(CraftingTableMenu.createInventory());
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (isCraftingMenu(event.getView().getTitle())) return;
        returnItemsToPlayer(event);
    }

    private boolean handleGuiButtonClick(InventoryClickEvent event, Player player, int slot) {
        if (slot == CLOSE_BUTTON_SLOT) {
            event.setCancelled(true);
            player.closeInventory();
            return true;
        }

        if (slot == RECIPE_BOOK_SLOT) {
            event.setCancelled(true);
            player.closeInventory();
            openRecipeBook(player);
            return true;
        }

        return false;
    }

    private void handleCraftingInventoryClick(InventoryClickEvent event, Inventory topInventory, Player player) {
        int slot = event.getSlot();

        if (!isInputSlot(slot) && slot != OUTPUT_SLOT) {
            event.setCancelled(true);
            return;
        }

        if (slot == OUTPUT_SLOT) {
            handleOutputSlotClick(event, topInventory, player);
        }
    }

    private void handleOutputSlotClick(InventoryClickEvent event, Inventory inventory, Player player) {
        BlightedRecipe recipe = getMatchingRecipe(inventory);
        if (recipe == null) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);

        if (isShiftClick(event.getClick())) {
            craftMaximum(recipe, inventory, player);
        } else {
            craftOnce(recipe, inventory, player);
        }
    }

    private void craftOnce(BlightedRecipe recipe, Inventory inventory, Player player) {
        ItemStack resultItem = getResultItem(recipe);
        ItemStack cursorItem = player.getItemOnCursor();

        if (!canAddToCursor(cursorItem, resultItem)) return;

        if (cursorItem.getType() != Material.AIR) {
            cursorItem.setAmount(cursorItem.getAmount() + resultItem.getAmount());
            player.setItemOnCursor(cursorItem);
        } else {
            player.setItemOnCursor(resultItem);
        }

        consumeIngredients(recipe, inventory, 1);
    }

    private void craftMaximum(BlightedRecipe recipe, Inventory inventory, Player player) {
        int maximumCraftCount = getMaxCraftCount(recipe, inventory);
        if (maximumCraftCount <= 0) return;

        ItemStack resultItem = getResultItem(recipe);
        resultItem.setAmount(resultItem.getAmount() * maximumCraftCount);

        Map<Integer, ItemStack> leftoverItems = player.getInventory().addItem(resultItem);
        dropLeftoverItems(player, leftoverItems);

        consumeIngredients(recipe, inventory, maximumCraftCount);
    }

    private void consumeIngredients(BlightedRecipe recipe, Inventory inventory, int times) {
        if (recipe instanceof BlightedShapedRecipe shapedRecipe) {
            consumeShapedIngredients(shapedRecipe, inventory, times);
        } else if (recipe instanceof BlightedShapelessRecipe shapelessRecipe) {
            consumeShapelessIngredients(shapelessRecipe, inventory, times);
        }
    }

    private void consumeShapedIngredients(BlightedShapedRecipe recipe, Inventory inventory, int times) {
        List<CraftingObject> recipePattern = recipe.getRecipe();

        for (int i = 0; i < INPUT_SLOTS.length; i++) {
            CraftingObject craftingObject = recipePattern.get(i);
            if (craftingObject == null) continue;

            ItemStack stack = inventory.getItem(INPUT_SLOTS[i]);
            if (stack == null || stack.getType() == Material.AIR) continue;

            int newAmount = stack.getAmount() - (craftingObject.getAmount() * times);
            if (newAmount <= 0) {
                inventory.setItem(INPUT_SLOTS[i], null);
            } else {
                stack.setAmount(newAmount);
            }
        }
    }

    private void consumeShapelessIngredients(BlightedShapelessRecipe recipe, Inventory inventory, int times) {
        Map<String, Integer> remainingRequired = new HashMap<>(recipe.getIngredientCountMap());

        remainingRequired.replaceAll((i, v) -> remainingRequired.get(i) * times);

        for (int slot : INPUT_SLOTS) {
            ItemStack stack = inventory.getItem(slot);
            if (stack == null || stack.getType() == Material.AIR) continue;

            String itemIdentifier = getItemId(stack);
            if (!remainingRequired.containsKey(itemIdentifier)) continue;

            int toRemove = Math.min(remainingRequired.get(itemIdentifier), stack.getAmount());
            int newAmount = stack.getAmount() - toRemove;

            if (newAmount <= 0) {
                inventory.setItem(slot, null);
            } else {
                stack.setAmount(newAmount);
            }

            remainingRequired.put(itemIdentifier, remainingRequired.get(itemIdentifier) - toRemove);
        }
    }

    private int getMaxCraftCount(BlightedRecipe recipe, Inventory inventory) {
        if (recipe instanceof BlightedShapedRecipe shapedRecipe) {
            return getMaxCraftCountForShaped(shapedRecipe, inventory);
        }

        if (recipe instanceof BlightedShapelessRecipe shapelessRecipe) {
            return getMaxCraftCountForShapeless(shapelessRecipe, inventory);
        }

        return 0;
    }

    private int getMaxCraftCountForShaped(BlightedShapedRecipe recipe, Inventory inventory) {
        int maxCrafts = Integer.MAX_VALUE;
        List<CraftingObject> recipePattern = recipe.getRecipe();

        for (int i = 0; i < INPUT_SLOTS.length; i++) {
            CraftingObject craftingObject = recipePattern.get(i);
            if (craftingObject == null) continue;

            ItemStack stack = inventory.getItem(INPUT_SLOTS[i]);
            if (stack == null || stack.getType() == Material.AIR) return 0;

            maxCrafts = Math.min(maxCrafts, stack.getAmount() / craftingObject.getAmount());
        }

        return maxCrafts == Integer.MAX_VALUE ? 0 : maxCrafts;
    }

    private int getMaxCraftCountForShapeless(BlightedShapelessRecipe recipe, Inventory inventory) {
        Map<String, Integer> requiredItems = recipe.getIngredientCountMap();
        Map<String, Integer> availableItems = collectAvailableItems(inventory);

        int maxCrafts = Integer.MAX_VALUE;

        for (Map.Entry<String, Integer> entry : requiredItems.entrySet()) {
            int available = availableItems.getOrDefault(entry.getKey(), 0);
            if (available < entry.getValue()) return 0;

            maxCrafts = Math.min(maxCrafts, available / entry.getValue());
        }

        return maxCrafts == Integer.MAX_VALUE ? 0 : maxCrafts;
    }

    private Map<String, Integer> collectAvailableItems(Inventory inventory) {
        Map<String, Integer> availableItems = new HashMap<>();

        for (int slot : INPUT_SLOTS) {
            ItemStack stack = inventory.getItem(slot);
            if (stack == null || stack.getType() == Material.AIR) continue;

            String itemIdentifier = getItemId(stack);
            availableItems.merge(itemIdentifier, stack.getAmount(), Integer::sum);
        }

        return availableItems;
    }

    private void updateOutput(Inventory inventory) {
        BlightedRecipe recipe = getMatchingRecipe(inventory);
        updateOutputSlot(inventory, recipe);
        updateRecipeIndicators(inventory, recipe);
    }

    private void updateOutputSlot(Inventory inventory, BlightedRecipe recipe) {
        if (recipe == null) {
            inventory.setItem(OUTPUT_SLOT, CraftingTableMenu.RECIPE_REQUIRED());
            return;
        }

        ItemStack previewItem = getResultItem(recipe);
        addPreviewLore(previewItem);
        inventory.setItem(OUTPUT_SLOT, previewItem);
    }

    private void addPreviewLore(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return;

        List<String> lore = meta.getLore();
        if (lore == null) lore = new ArrayList<>();

        lore.add("§8§m----------------");
        lore.add("§7This is the item you are crafting.");
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    private void updateRecipeIndicators(Inventory inventory, BlightedRecipe recipe) {
        ItemStack indicator = CraftingTableMenu.VALID_RECIPE_INDICATOR(recipe != null);

        for (int slot : INDICATOR_SLOTS_LEFT) {
            inventory.setItem(slot, indicator);
        }
        for (int slot : INDICATOR_SLOTS_RIGHT) {
            inventory.setItem(slot, indicator);
        }
    }

    private void returnItemsToPlayer(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        for (int slot : INPUT_SLOTS) {
            ItemStack item = event.getView().getItem(slot);
            if (item == null || item.getType() == Material.AIR) continue;

            Map<Integer, ItemStack> leftover = player.getInventory().addItem(item);
            dropLeftoverItems(player, leftover);
        }
    }

    private BlightedRecipe getMatchingRecipe(Inventory inventory) {
        List<ItemStack> craftingGrid = new ArrayList<>();
        for (int slot : INPUT_SLOTS) {
            craftingGrid.add(inventory.getItem(slot));
        }

        Set<BlightedRecipe> matchingRecipes = BlightedRecipe.findMatchingRecipes(craftingGrid);
        return matchingRecipes.isEmpty() ? null : matchingRecipes.iterator().next();
    }

    private ItemStack getResultItem(BlightedRecipe recipe) {
        ItemStack item = recipe.getResult().toItemStack().clone();
        int amount = recipe.getAmount() > 0 ? recipe.getAmount() : 1;
        item.setAmount(amount);
        return item;
    }

    private boolean isInputSlot(int slot) {
        for (int inputSlot : INPUT_SLOTS) {
            if (inputSlot == slot) return true;
        }
        return false;
    }

    private String getItemId(ItemStack stack) {
        return "vanilla:" + stack.getType().name().toLowerCase();
    }

    private boolean canAddToCursor(ItemStack cursorItem, ItemStack resultItem) {
        if (cursorItem == null || cursorItem.getType() == Material.AIR) return true;

        return cursorItem.isSimilar(resultItem)
            && cursorItem.getAmount() + resultItem.getAmount() <= cursorItem.getMaxStackSize();
    }

    private boolean isShiftClick(ClickType clickType) {
        return clickType == ClickType.SHIFT_LEFT || clickType == ClickType.SHIFT_RIGHT;
    }

    private void dropLeftoverItems(Player player, Map<Integer, ItemStack> leftoverItems) {
        leftoverItems.values().forEach(item ->
            player.getWorld().dropItemNaturally(player.getLocation(), item)
        );
    }

    private void openRecipeBook(Player player) {
        MenuManager.openMenu(
            new RecipeBookMenu.RecipeListMenu(
                new Menu("Crafting Table", 54) {
                    @Override
                    public void build(Player p) {
                    }
                }
            ), player
        );
    }

    private void scheduleOutputUpdate(Inventory inventory) {
        Bukkit.getScheduler().runTaskLater(
            BlightedMC.getInstance(),
            () -> updateOutput(inventory),
            SCHEDULED_UPDATE_DELAY
        );
    }

    private boolean isCraftingMenu(String title) {
        return !CRAFTING_MENU_TITLE.equals(title);
    }
}