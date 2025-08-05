package fr.moussax.blightedMC.core.menus;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class MenuBuilder {
  private final String title;
  private final int size;
  private final Map<Integer, Menu.MenuSlot> slots = new HashMap<>();

  public MenuBuilder(String title, int size) {
    this.title = title;
    this.size = size;
  }

  public MenuBuilder setItem(int slot, ItemStack item, ItemInteraction interaction, MenuAction action) {
    slots.put(slot, new Menu.MenuSlot(item, interaction, action));
    return this;
  }

  public MenuBuilder setItem(int slot, MenuElementPreset preset, ItemInteraction interaction, MenuAction action) {
    return setItem(slot, preset.getItem(), interaction, action);
  }

  public MenuBuilder fillEmptyWith(MenuElementPreset preset) {
    for (int i = 0; i < size; i++) {
      if (!slots.containsKey(i)) {
        setItem(i, preset.getItem(), ItemInteraction.ANY_CLICK, (p, t) -> {
        });
      }
    }
    return this;
  }

  public Menu build() {
    return new Menu(title, size) {
      @Override
      public void build(Player player) {
        this.slots.putAll(slots);
      }
    };
  }

  public void open(Player player) {
    MenuManager.openMenu(build(), player);
  }
}