package fr.moussax.blightedMC.core.items.crafting.menu;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.items.crafting.BlightedRecipe;
import fr.moussax.blightedMC.core.items.crafting.BlightedShapedRecipe;
import fr.moussax.blightedMC.core.items.crafting.BlightedShapelessRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

public class CraftingTableListener implements Listener {
  private static final int CRAFTING_SLOT = 23;
  private static final int[] CRAFTING_SLOTS = {10, 11, 12, 19, 20, 21, 28, 29, 30};

  @EventHandler
  public void onDrag(InventoryDragEvent event) {
    if (!event.getView().getTitle().equals("§rCraft Items")) return;
    Bukkit.getScheduler().runTaskLater(BlightedMC.getInstance(),
        () -> updateCraftingOutputSlot(event.getView().getTopInventory()), 1);
  }

  @EventHandler
  public void onSwap(InventoryClickEvent event) {
    if (!event.getView().getTitle().equals("§rCraft Items")) return;
    if (event.getClickedInventory() == null) return;

    if (event.getClickedInventory().getType() != InventoryType.PLAYER) {
      int slot = event.getSlot();
      Player p = (Player) event.getWhoClicked();

      if(slot == 49) {
        event.setCancelled(true);
        p.closeInventory();
        return;
      }

      if (!isCraftingGridSlot(slot) && slot != CRAFTING_SLOT) {
        event.setCancelled(true);
        return;
      }

      if (slot == CRAFTING_SLOT) {
        ItemStack outputItem = event.getClickedInventory().getItem(slot);
        if (outputItem != null && outputItem.isSimilar(CraftingTableMenu.RECIPE_REQUIRED())) {
          event.setCancelled(true);
          return;
        }
      }
    }

    Bukkit.getScheduler().runTaskLater(BlightedMC.getInstance(), () -> {
      BlightedRecipe recipe = getMatchingRecipe(event.getView().getTopInventory());
      if (event.getSlot() == CRAFTING_SLOT && recipe != null) {
        event.getView().setCursor(craft(recipe, event.getView().getTopInventory(), (Player) event.getWhoClicked()));
      }
      updateCraftingOutputSlot(event.getView().getTopInventory());
    }, 1);
  }

  private void updateCraftingOutputSlot(Inventory inventory) {
    BlightedRecipe recipe = getMatchingRecipe(inventory);

    if (recipe == null) {
      inventory.setItem(CRAFTING_SLOT, CraftingTableMenu.RECIPE_REQUIRED());
      return;
    }

    inventory.setItem(CRAFTING_SLOT, getItem(recipe, inventory));
  }

  private BlightedRecipe getMatchingRecipe(Inventory inventory) {
    ArrayList<ItemStack> craftingGrid = new ArrayList<>();
    for (int slot : CRAFTING_SLOTS) craftingGrid.add(inventory.getItem(slot));
    Set<BlightedRecipe> recipes = BlightedRecipe.findMatchingRecipes(craftingGrid);
    if (recipes.isEmpty()) return null;
    return recipes.iterator().next();
  }

  private ItemStack craft(BlightedRecipe recipe, Inventory inventory, Player player) {
    if (recipe instanceof BlightedShapedRecipe shaped) return craft(shaped, inventory);
    if (recipe instanceof BlightedShapelessRecipe shapeless) return craft(shapeless);
    throw new IllegalArgumentException("Unsupported recipe type: " + recipe.getClass());
  }

  private ItemStack craft(BlightedShapedRecipe recipe, Inventory inventory) {
    ItemStack item = getItem(recipe, inventory);
    int index = 0;
    for (int slot : CRAFTING_SLOTS) {
      if (recipe.getRecipe().get(index) != null && inventory.getItem(slot) != null) {
        Objects.requireNonNull(inventory.getItem(slot)).setAmount(Objects.requireNonNull(inventory.getItem(slot)).getAmount() - recipe.getRecipe().get(index).amount());
      }
      index++;
    }
    return item;
  }

  private ItemStack craft(BlightedShapelessRecipe recipe) {
    return getItem(recipe, null);
  }

  private ItemStack getItem(BlightedRecipe recipe, Inventory inventory) {
    if (recipe instanceof BlightedShapedRecipe shaped) return getItem(shaped, inventory);
    if (recipe instanceof BlightedShapelessRecipe shapeless) return getItem(shapeless, inventory);
    throw new IllegalArgumentException("Unsupported recipe type: " + recipe.getClass());
  }

  private ItemStack getItem(BlightedShapedRecipe recipe, Inventory inventory) {
    ItemStack item = recipe.getResult().toItemStack();
    item.setAmount(recipe.getAmount());
    return item;
  }

  private ItemStack getItem(BlightedShapelessRecipe recipe, Inventory inventory) {
    ItemStack item = recipe.getResult().toItemStack();
    item.setAmount(recipe.getAmount());
    return item;
  }

  private boolean isCraftingGridSlot(int slot) {
    for (int s : CRAFTING_SLOTS) if (s == slot) return true;
    return false;
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

    for (int slot : CRAFTING_SLOTS) {
      ItemStack item = event.getView().getItem(slot);
      if (item == null || item.getType() == Material.AIR) continue;

      Player player = (Player) event.getPlayer();
      if (player.getInventory().firstEmpty() != -1) {
        player.getInventory().addItem(item);
      } else {
        player.getWorld().spawn(player.getLocation(), Item.class, it -> it.setItemStack(item));
      }
    }
  }
}
