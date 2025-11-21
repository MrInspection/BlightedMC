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

/**
 * Base class for interactive inventory menus.
 *
 * <p>Menus display items in slots and handle player interactions
 * through {@link MenuItemInteraction} and {@link MenuAction}.</p>
 *
 * <p>Extend this class and implement {@link #build(Player)} to define the menu layout.</p>
 */
public abstract class Menu implements InventoryHolder {
    protected final String title;
    protected final int size;
    protected final Map<Integer, MenuSlot> slots = new HashMap<>();
    protected Inventory inventory;
    protected Player currentPlayer;

    /**
     * Creates a new menu.
     *
     * @param title menu title
     * @param size  inventory size (multiple of 9)
     */
    public Menu(String title, int size) {
        this.title = title;
        this.size = size;
        this.inventory = Bukkit.createInventory(this, size, title);
    }

    /**
     * Defines the content of the menu for the given player.
     *
     * @param player player viewing the menu
     */
    public abstract void build(Player player);

    /**
     * Opens the menu for a player.
     *
     * <p>Clears existing slots, rebuilds content,
     * sets items, and registers the menu in {@link MenuRouter}.</p>
     *
     * @param player target player
     */
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

    /**
     * Places an item in a slot with an action.
     *
     * @param slot        slot index
     * @param item        displayed item
     * @param interaction triggering interaction
     * @param action      action to execute
     */
    public void setItem(int slot, ItemStack item, MenuItemInteraction interaction, MenuAction action) {
        slots.put(slot, new MenuSlot(item, interaction, action));
    }

    /**
     * Places a preset item in a slot with an action.
     *
     * @param slot        slot index
     * @param preset      displayed preset item
     * @param interaction triggering interaction
     * @param action      action to execute
     */
    public void setItem(int slot, MenuElementPreset preset, MenuItemInteraction interaction, MenuAction action) {
        setItem(slot, preset.getItem(), interaction, action);
    }

    /**
     * Fills empty slots with a preset item.
     *
     * @param preset item preset
     */
    public void fillEmptyWith(MenuElementPreset preset) {
        for (int i = 0; i < size; i++) {
            if (!slots.containsKey(i)) {
                setItem(i, preset.getItem(), MenuItemInteraction.ANY_CLICK, (p, t) -> {
                });
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Opens a submenu for the current player.
     */
    public void openSubMenu(Menu submenu) {
        submenu.open(currentPlayer);
    }

    /**
     * Returns to the previous menu via {@link MenuRouter}.
     */
    public void goBack() {
        MenuRouter.goBack(currentPlayer);
    }

    /**
     * Closes the menu for the current player.
     */
    public void close() {
        currentPlayer.closeInventory();
    }

    /**
     * Represents a clickable slot in a menu.
     */
    public static class MenuSlot {
        public final ItemStack item;
        private final Map<MenuItemInteraction, MenuAction> actions = new HashMap<>();

        /**
         * Creates a new menu slot.
         *
         * @param item        displayed item
         * @param interaction triggering interaction
         * @param action      executed action
         */
        public MenuSlot(ItemStack item, MenuItemInteraction interaction, MenuAction action) {
            this.item = item;
            actions.put(interaction, action);
        }

        /**
         * Executes the action matching the click type.
         *
         * @param player    clicking player
         * @param clickType type of click
         */
        public void handle(Player player, ClickType clickType) {
            if (clickType == ClickType.LEFT && actions.containsKey(MenuItemInteraction.LEFT_CLICK)) {
                actions.get(MenuItemInteraction.LEFT_CLICK).execute(player, clickType);
            } else if (clickType == ClickType.RIGHT && actions.containsKey(MenuItemInteraction.RIGHT_CLICK)) {
                actions.get(MenuItemInteraction.RIGHT_CLICK).execute(player, clickType);
            } else if (actions.containsKey(MenuItemInteraction.ANY_CLICK)) {
                actions.get(MenuItemInteraction.ANY_CLICK).execute(player, clickType);
            }
        }
    }
}