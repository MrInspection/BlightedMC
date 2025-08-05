package fr.moussax.blightedMC.core.items.crafting.menu;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.items.crafting.*;
import fr.moussax.blightedMC.core.menus.Menu;
import fr.moussax.blightedMC.core.menus.MenuManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
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

  private static final int OUTPUT_SLOT = 23;
  private static final int[] INPUT_SLOTS = {10, 11, 12, 19, 20, 21, 28, 29, 30};

  @EventHandler
  public void onDrag(InventoryDragEvent event) {
    if (!event.getView().getTitle().equals("§rCraft Items")) return;
    Bukkit.getScheduler().runTaskLater(BlightedMC.getInstance(),
      () -> updateOutput(event.getView().getTopInventory()), 1);
  }

  @EventHandler
  public void onClick(InventoryClickEvent event) {
    if (!event.getView().getTitle().equals("§rCraft Items")) return;
    Inventory topInventory = event.getView().getTopInventory();
    Player player = (Player) event.getWhoClicked();

    // Close button
    if (event.getSlot() == 49) {
      event.setCancelled(true);
      player.closeInventory();
      return;
    }

    // Recipe Book button (slot 25)
    if (event.getSlot() == 25) {
      event.setCancelled(true);
      player.closeInventory();
      MenuManager.openMenu(
        new RecipeBookMenu.RecipeListMenu(
          new Menu("Crafting Table", 54) {
            @Override
            public void build(Player p) {
            }
          }
        ), player);
      return;
    }

    // Only handle clicks in our custom crafting inventory
    if (event.getClickedInventory() != null && event.getClickedInventory().equals(topInventory)) {
      int slot = event.getSlot();

      // Prevent taking decorative slots
      if (!isInputSlot(slot) && slot != OUTPUT_SLOT) {
        event.setCancelled(true);
        return;
      }

      // Handle crafting output click
      if (slot == OUTPUT_SLOT) {
        BlightedRecipe recipe = getMatchingRecipe(topInventory);
        if (recipe == null) {
          event.setCancelled(true);
          return;
        }

        // Handle shift-click (craft max)
        if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
          event.setCancelled(true);
          craftMaximum(recipe, topInventory, player);
        } else {
          // Single click behaves like vanilla
          event.setCancelled(true);
          craftOnce(recipe, topInventory, player);
        }
      }
    }

    // Schedule output update after the click
    Bukkit.getScheduler().runTaskLater(BlightedMC.getInstance(),
      () -> updateOutput(topInventory), 1);
  }

  private void craftOnce(BlightedRecipe recipe, Inventory inventory, Player player) {
    ItemStack resultItem = getResultItem(recipe, inventory);
    ItemStack cursorItem = player.getItemOnCursor();

    // Combine with cursor if same type
    if (cursorItem != null && cursorItem.getType() != Material.AIR) {
      if (!cursorItem.isSimilar(resultItem) || cursorItem.getAmount() + resultItem.getAmount() > cursorItem.getMaxStackSize())
        return;
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

    ItemStack resultItem = getResultItem(recipe, inventory);
    resultItem.setAmount(resultItem.getAmount() * maximumCraftCount);

    HashMap<Integer, ItemStack> leftoverItems = player.getInventory().addItem(resultItem);
    leftoverItems.values().forEach(item -> player.getWorld().dropItemNaturally(player.getLocation(), item));

    consumeIngredients(recipe, inventory, maximumCraftCount);
  }

  private void consumeIngredients(BlightedRecipe recipe, Inventory inventory, int times) {
    if (recipe instanceof BlightedShapedRecipe shapedRecipe) {
      List<CraftingObject> recipePattern = shapedRecipe.getRecipe();
      for (int i = 0; i < INPUT_SLOTS.length; i++) {
        CraftingObject craftingObject = recipePattern.get(i);
        if (craftingObject == null) continue;
        ItemStack stack = inventory.getItem(INPUT_SLOTS[i]);
        if (stack == null || stack.getType() == Material.AIR) continue;
        stack.setAmount(stack.getAmount() - craftingObject.getAmount() * times);
        if (stack.getAmount() <= 0) inventory.setItem(INPUT_SLOTS[i], null);
      }
    } else if (recipe instanceof BlightedShapelessRecipe shapelessRecipe) {
      Map<String, Integer> requiredCounts = shapelessRecipe.getIngredientCountMap();
      for (int slot : INPUT_SLOTS) {
        ItemStack stack = inventory.getItem(slot);
        if (stack == null || stack.getType() == Material.AIR) continue;
        String itemIdentifier = getItemId(stack);
        if (!requiredCounts.containsKey(itemIdentifier)) continue;
        int totalToRemove = requiredCounts.get(itemIdentifier) * times;
        int removedNow = Math.min(totalToRemove, stack.getAmount());
        stack.setAmount(stack.getAmount() - removedNow);
        if (stack.getAmount() <= 0) inventory.setItem(slot, null);
        requiredCounts.put(itemIdentifier, totalToRemove - removedNow);
      }
    }
  }

  private int getMaxCraftCount(BlightedRecipe recipe, Inventory inventory) {
    if (recipe instanceof BlightedShapedRecipe shapedRecipe) {
      int maxCrafts = Integer.MAX_VALUE;
      List<CraftingObject> recipePattern = shapedRecipe.getRecipe();
      for (int i = 0; i < INPUT_SLOTS.length; i++) {
        CraftingObject craftingObject = recipePattern.get(i);
        if (craftingObject == null) continue;
        ItemStack stack = inventory.getItem(INPUT_SLOTS[i]);
        if (stack == null || stack.getType() == Material.AIR) return 0;
        maxCrafts = Math.min(maxCrafts, stack.getAmount() / craftingObject.getAmount());
      }
      return maxCrafts == Integer.MAX_VALUE ? 0 : maxCrafts;
    }

    if (recipe instanceof BlightedShapelessRecipe shapelessRecipe) {
      Map<String, Integer> requiredItems = shapelessRecipe.getIngredientCountMap();
      Map<String, Integer> availableItems = new HashMap<>();
      for (int slot : INPUT_SLOTS) {
        ItemStack stack = inventory.getItem(slot);
        if (stack == null || stack.getType() == Material.AIR) continue;
        String itemIdentifier = getItemId(stack);
        availableItems.merge(itemIdentifier, stack.getAmount(), Integer::sum);
      }

      int maxCrafts = Integer.MAX_VALUE;
      for (var entry : requiredItems.entrySet()) {
        int available = availableItems.getOrDefault(entry.getKey(), 0);
        if (available < entry.getValue()) return 0;
        maxCrafts = Math.min(maxCrafts, available / entry.getValue());
      }
      return maxCrafts == Integer.MAX_VALUE ? 0 : maxCrafts;
    }
    return 0;
  }

  private void updateOutput(Inventory inventory) {
    BlightedRecipe recipe = getMatchingRecipe(inventory);

    // Update output slot
    if (recipe == null) {
      inventory.setItem(OUTPUT_SLOT, CraftingTableMenu.RECIPE_REQUIRED());
    } else {
      ItemStack previewItem = getResultItem(recipe, inventory);

      // Add the custom preview lore
      List<String> lore = Objects.requireNonNull(previewItem.getItemMeta()).getLore();
      if (lore == null) lore = new ArrayList<>();
      lore.add("§8§m----------------");
      lore.add("§7This is the item you are crafting.");
      var meta = previewItem.getItemMeta();
      meta.setLore(lore);
      previewItem.setItemMeta(meta);

      inventory.setItem(OUTPUT_SLOT, previewItem);
    }

    ItemStack indicator = (recipe == null)
      ? CraftingTableMenu.VALID_RECIPE_INDICATOR(false)
      : CraftingTableMenu.VALID_RECIPE_INDICATOR(true);

    for (int slot = 45; slot < 49; slot++) {
      inventory.setItem(slot, indicator);
    }
    for (int slot = 50; slot < 54; slot++) {
      inventory.setItem(slot, indicator);
    }
  }

  private BlightedRecipe getMatchingRecipe(Inventory inventory) {
    List<ItemStack> craftingGrid = new ArrayList<>();
    for (int slot : INPUT_SLOTS) craftingGrid.add(inventory.getItem(slot));
    Set<BlightedRecipe> matchingRecipes = BlightedRecipe.findMatchingRecipes(craftingGrid);
    return matchingRecipes.isEmpty() ? null : matchingRecipes.iterator().next();
  }

  private ItemStack getResultItem(BlightedRecipe recipe, Inventory inventory) {
    ItemStack item = recipe.getResult().toItemStack();
    int amount = recipe.getAmount() > 0 ? recipe.getAmount() : 1;
    item.setAmount(amount);
    return item;
  }

  private boolean isInputSlot(int slot) {
    for (int inputSlot : INPUT_SLOTS) if (inputSlot == slot) return true;
    return false;
  }

  private String getItemId(ItemStack stack) {
    return "vanilla:" + stack.getType().name().toLowerCase();
  }

  @EventHandler
  public void onInteract(PlayerInteractEvent event) {
    if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
    if (event.getClickedBlock() == null || event.getClickedBlock().getType() != Material.CRAFTING_TABLE) return;
    event.getPlayer().openInventory(CraftingTableMenu.createInventory());
  }

  @EventHandler
  public void onClose(InventoryCloseEvent event) {
    if (!event.getView().getTitle().equals("§rCraft Items")) return;

    for (int slot : INPUT_SLOTS) {
      ItemStack item = event.getView().getItem(slot);
      if (item == null || item.getType() == Material.AIR) continue;

      Player player = (Player) event.getPlayer();
      if (player.getInventory().firstEmpty() != -1) {
        player.getInventory().addItem(item);
      } else {
        player.getWorld().spawn(player.getLocation(), Item.class, worldItem -> worldItem.setItemStack(item));
      }
    }
  }
}
