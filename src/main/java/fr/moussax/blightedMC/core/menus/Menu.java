package fr.moussax.blightedMC.core.menus;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public abstract class Menu implements InventoryHolder {
  protected final String title;
  protected final int size;
  protected final Map<Integer, MenuSlot> slots = new HashMap<>();
  protected Inventory inventory;
  protected Player currentPlayer;

  public Menu(String title, int size) {
    this.title = title;
    this.size = size;
    this.inventory = Bukkit.createInventory(this, size, title);
  }

  public abstract void build(Player player);

  public void open(Player player) {
    this.currentPlayer = player;
    slots.clear();
    build(player);
    for (int i = 0; i < size; i++) {
      MenuSlot slot = slots.get(i);
      inventory.setItem(i, slot != null ? slot.item : null);
    }
    player.openInventory(inventory);
    MenuRouter.setCurrentMenu(player, this);
  }

  public void setItem(int slot, ItemStack item, ItemInteraction interaction, MenuAction action) {
    slots.put(slot, new MenuSlot(item, interaction, action));
  }

  public void setItem(int slot, MenuElementPreset preset, ItemInteraction interaction, MenuAction action) {
    setItem(slot, preset.getItem(), interaction, action);
  }

  public void fillEmptyWith(MenuElementPreset preset) {
    for (int i = 0; i < size; i++) {
      if (!slots.containsKey(i)) {
        setItem(i, preset.getItem(), ItemInteraction.ANY_CLICK, (p, t) -> {
        });
      }
    }
  }

  @Nonnull
  @Override
  public Inventory getInventory() {
    return inventory;
  }

  // Routing helpers
  public void openSubMenu(Menu submenu) {
    submenu.open(currentPlayer);
  }

  public void goBack() {
    MenuRouter.goBack(currentPlayer);
  }

  public void close() {
    currentPlayer.closeInventory();
  }

  // Slot logic
  public static class MenuSlot {
    public final ItemStack item;
    private final Map<ItemInteraction, MenuAction> actions = new HashMap<>();

    public MenuSlot(ItemStack item, ItemInteraction interaction, MenuAction action) {
      this.item = item;
      actions.put(interaction, action);
    }

    public void handle(Player player, ClickType clickType) {
      if (clickType == ClickType.LEFT && actions.containsKey(ItemInteraction.LEFT_CLICK)) {
        actions.get(ItemInteraction.LEFT_CLICK).execute(player, clickType);
      } else if (clickType == ClickType.RIGHT && actions.containsKey(ItemInteraction.RIGHT_CLICK)) {
        actions.get(ItemInteraction.RIGHT_CLICK).execute(player, clickType);
      } else if (actions.containsKey(ItemInteraction.ANY_CLICK)) {
        actions.get(ItemInteraction.ANY_CLICK).execute(player, clickType);
      }
    }
  }
}