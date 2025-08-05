package fr.moussax.blightedMC.core.menus;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.BiFunction;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class Menu {
    private static final Map<Player, Stack<Menu>> history = new HashMap<>();
    private final String title;
    private final int size;
    private final Inventory inventory;
    private final Map<Integer, MenuSlot> slots;

    public Menu(String title, int size, Map<Integer, MenuSlot> slots) {
        this.title = title;
        this.size = size;
        this.inventory = Bukkit.createInventory(null, size, title);
        this.slots = new HashMap<>(slots);
        for (Map.Entry<Integer, MenuSlot> entry : slots.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().item);
        }
  }

  public void open(Player player) {
        history.computeIfAbsent(player, k -> new Stack<>()).push(this);
        player.openInventory(inventory);
  }

  public void close(Player player) {
    player.closeInventory();
        Stack<Menu> stack = history.get(player);
        if (stack != null) stack.clear();
    }

    public static void goBack(Player player) {
        Stack<Menu> stack = history.get(player);
        if (stack == null || stack.size() <= 1) {
            player.closeInventory();
            if (stack != null) stack.clear();
            return;
        }
        stack.pop();
        stack.peek().open(player);
    }

    public static Menu getCurrent(Player player) {
        Stack<Menu> stack = history.get(player);
        return (stack == null || stack.isEmpty()) ? null : stack.peek();
    }

    public void handleClick(Player player, int slot, ClickType clickType) {
        MenuSlot menuSlot = slots.get(slot);
        if (menuSlot != null) menuSlot.handle(player, clickType);
    }

    public Inventory getInventory() {
        return inventory;
    }

    // --- Pagination Helper ---
    public static Menu paginated(String title, int size, int totalItems, BiFunction<Player, Integer, MenuSlot> itemProvider, int page, Player player) {
        Map<Integer, MenuSlot> slots = new HashMap<>();
        int start = page * (size - 9);
        int end = Math.min(start + (size - 9), totalItems);
        int slotIndex = 0;
        for (int i = start; i < end; i++) {
            slots.put(slotIndex++, itemProvider.apply(player, i));
        }
        // Navigation
        if (page > 0) {
            slots.put(size - 9, new MenuSlot(MenuElementPreset.BACK_BUTTON.getItem(), (p, t) -> openPaginated(title, size, totalItems, itemProvider, page - 1, p)));
        }
        if (end < totalItems) {
            slots.put(size - 1, new MenuSlot(MenuElementPreset.NEXT_BUTTON.getItem(), (p, t) -> openPaginated(title, size, totalItems, itemProvider, page + 1, p)));
        }
        slots.put(size - 5, new MenuSlot(MenuElementPreset.CLOSE_BUTTON.getItem(), (p, t) -> getCurrent(p).close(p)));
        // Fill empty
        for (int i = 0; i < size; i++) {
            if (!slots.containsKey(i)) {
                slots.put(i, new MenuSlot(MenuElementPreset.EMPTY_SLOT_FILLER.getItem(), (p, t) -> {}));
            }
        }
        return new Menu(title + " (Page " + (page + 1) + ")", size, slots);
    }

    public static void openPaginated(String title, int size, int totalItems, BiFunction<Player, Integer, MenuSlot> itemProvider, int page, Player player) {
        Menu menu = paginated(title, size, totalItems, itemProvider, page, player);
        menu.open(player);
    }

    // --- AbstractMenu for easy extension ---
    public static abstract class AbstractMenu {
        protected final String title;
        protected final int size;
        protected final Map<Integer, MenuSlot> slots = new HashMap<>();
        public AbstractMenu(String title, int size) {
            this.title = title;
            this.size = size;
        }
        public abstract void build(Player player);
        public void open(Player player) {
            build(player);
            Menu menu = new Menu(title, size, slots);
            menu.open(player);
        }
        public void setItem(int slot, ItemStack item, MenuAction action) {
            slots.put(slot, new MenuSlot(item, action));
        }
        public void setItem(int slot, ItemStack item, MenuAction left, MenuAction right) {
            slots.put(slot, new MenuSlot(item, left, right));
        }
        public void setItem(int slot, MenuElementPreset preset, MenuAction action) {
            setItem(slot, preset.getItem(), action);
        }
        public void setItem(int slot, MenuElementPreset preset, MenuAction left, MenuAction right) {
            setItem(slot, preset.getItem(), left, right);
        }
        public void fillEmptyWith(MenuElementPreset preset) {
            for (int i = 0; i < size; i++) {
                if (!slots.containsKey(i)) {
                    setItem(i, preset.getItem(), (p, t) -> {});
                }
            }
        }
    }

    public static class MenuSlot {
        private final ItemStack item;
        private final MenuAction left;
        private final MenuAction right;
        private final MenuAction any;

        public MenuSlot(ItemStack item, MenuAction any) {
            this(item, any, null);
        }
        public MenuSlot(ItemStack item, MenuAction left, MenuAction right) {
            this.item = item;
            this.left = left;
            this.right = right;
            this.any = null;
        }
        public void handle(Player player, ClickType clickType) {
            if (clickType == ClickType.LEFT && left != null) left.execute(player, clickType);
            else if (clickType == ClickType.RIGHT && right != null) right.execute(player, clickType);
            else if (any != null) any.execute(player, clickType);
    }
  }
}
