package fr.moussax.blightedMC.core.menus;

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

    public MenuBuilder setItem(int slot, ItemStack item, MenuAction action) {
        slots.put(slot, new Menu.MenuSlot(item, action));
        return this;
    }

    public MenuBuilder setItem(int slot, ItemStack item, MenuAction left, MenuAction right) {
        slots.put(slot, new Menu.MenuSlot(item, left, right));
        return this;
    }

    public MenuBuilder setItem(int slot, MenuElementPreset preset, MenuAction action) {
        return setItem(slot, preset.getItem(), action);
    }

    public MenuBuilder setItem(int slot, MenuElementPreset preset, MenuAction left, MenuAction right) {
        return setItem(slot, preset.getItem(), left, right);
    }

    public MenuBuilder fillEmptyWith(MenuElementPreset preset) {
        for (int i = 0; i < size; i++) {
            if (!slots.containsKey(i)) {
                setItem(i, preset.getItem(), (p, t) -> {});
            }
        }
        return this;
    }

    public Menu build() {
        return new Menu(title, size, slots);
    }
}