package fr.moussax.blightedMC.smp.core.shared.menu;

import fr.moussax.blightedMC.smp.core.shared.menu.interaction.MenuAction;
import fr.moussax.blightedMC.smp.core.shared.menu.interaction.MenuElementPreset;
import fr.moussax.blightedMC.smp.core.shared.menu.interaction.MenuItemInteraction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Base class for interactive inventory menus.
 *
 * <p>Menus display items in slots and handle player interactions
 * through {@link MenuItemInteraction} and {@link MenuAction}.
 * Slots can be set individually, in bulk, or filled automatically for empty slots.
 * This provides flexibility for creating complex, interactive inventory layouts.</p>
 *
 * <p>Extend this class and implement {@link #build(Player)} to define
 * the menu layout and behavior for players.</p>
 */
public abstract class Menu implements InventoryHolder {
    protected final String title;
    protected final int size;
    protected final Map<Integer, MenuSlot> slots = new HashMap<>();
    protected Inventory inventory;
    protected UUID viewerId;

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
     * <p>Clears existing slots, rebuilds content, sets items, and
     * registers the menu in {@link MenuRouter}.</p>
     *
     * @param player target player
     */
    public void open(Player player) {
        this.viewerId = player.getUniqueId();

        slots.clear();
        build(player);

        inventory.clear();
        for (Map.Entry<Integer, MenuSlot> entry : slots.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().item);
        }

        player.openInventory(inventory);
        MenuRouter.setCurrentMenu(player, this);
    }

    /**
     * Safely retrieves the player currently viewing this menu.
     *
     * @return the player, or null if they are offline
     */
    protected Player getPlayer() {
        return viewerId != null ? Bukkit.getPlayer(viewerId) : null;
    }

    /**
     * Places an item in a slot with an associated action.
     *
     * @param slot        slot index
     * @param item        displayed item
     * @param interaction triggering interaction
     * @param action      action to execute when clicked
     */
    public void setItem(int slot, ItemStack item, MenuItemInteraction interaction, MenuAction action) {
        slots.put(slot, new MenuSlot(item, interaction, action));
    }

    /**
     * Places a preset item in a slot with an associated action.
     *
     * @param slot        slot index
     * @param preset      displayed preset item
     * @param interaction triggering interaction
     * @param action      action to execute when clicked
     */
    public void setItem(int slot, MenuElementPreset preset, MenuItemInteraction interaction, MenuAction action) {
        setItem(slot, preset.getItem(), interaction, action);
    }

    /**
     * Fills the specified slots with an ItemStack.
     *
     * @param slots       slot indices to fill
     * @param item        item to place
     * @param interaction interaction type for each slot
     */
    public void fillSlots(int[] slots, ItemStack item, MenuItemInteraction interaction) {
        for (int slot : slots) {
            setItem(slot, item, interaction, (player, type) -> {
            });
        }
    }

    /**
     * Fills the specified slots with a MenuElementPreset.
     *
     * @param slots       slot indices to fill
     * @param preset      preset item
     * @param interaction interaction type for each slot
     */
    public void fillSlots(int[] slots, MenuElementPreset preset, MenuItemInteraction interaction) {
        for (int slot : slots) {
            setItem(slot, preset, interaction, (player, type) -> {
            });
        }
    }

    /**
     * Fills the specified slots with an ItemStack using ANY_CLICK.
     *
     * @param slots slot indices to fill
     * @param item  item to place
     */
    public void fillSlots(int[] slots, ItemStack item) {
        fillSlots(slots, item, MenuItemInteraction.ANY_CLICK);
    }

    /**
     * Fills the specified slots with a MenuElementPreset using ANY_CLICK.
     *
     * @param slots  slot indices to fill
     * @param preset preset item
     */
    public void fillSlots(int[] slots, MenuElementPreset preset) {
        fillSlots(slots, preset.getItem(), MenuItemInteraction.ANY_CLICK);
    }

    /**
     * Fills all empty slots in the menu with a specific ItemStack.
     *
     * @param item item to fill empty slots
     */
    public void fillEmptyWith(ItemStack item) {
        for (int slot = 0; slot < size; slot++) {
            if (!slots.containsKey(slot)) {
                setItem(slot, item, MenuItemInteraction.ANY_CLICK, (p, t) -> {
                });
            }
        }
    }

    /**
     * Fills all empty slots in the menu with a preset item.
     *
     * @param preset preset item to fill empty slots
     */
    public void fillEmptyWith(MenuElementPreset preset) {
        fillEmptyWith(preset.getItem());
    }

    /**
     * Clears all items from the visual inventory and resets internal slot mappings.
     */
    public void clearInventory() {
        inventory.clear();
        slots.clear();
    }

    @Override
    @NonNull
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Opens a submenu for the current player.
     *
     * @param submenu submenu to open
     */
    public void openSubMenu(Menu submenu) {
        Player player = getPlayer();
        if (player != null) submenu.open(player);
    }

    /**
     * Returns to the previous menu via {@link MenuRouter}.
     */
    public void goBack() {
        Player player = getPlayer();
        if (player != null) MenuRouter.goBack(player);
    }

    /**
     * Closes the menu for the current player.
     */
    public void close() {
        Player player = getPlayer();
        if (player != null) player.closeInventory();
    }

    /**
     * Refreshes the menu for a specific player without closing the inventory window.
     *
     * <p>This clears existing slots, rebuilds the menu by calling {@link #build(Player)},
     * updates the inventory, and preserves the cursor position.</p>
     *
     * @param player player to refresh
     */
    @SuppressWarnings("UnstableApiUsage")
    protected void refresh(Player player) {
        slots.clear();
        build(player);
        inventory.clear();

        for (Map.Entry<Integer, MenuSlot> entry : slots.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().item);
        }

        player.updateInventory();
    }

    /**
     * Represents a clickable slot in a menu.
     */
    public static class MenuSlot {
        public final ItemStack item;
        private final Map<MenuItemInteraction, MenuAction> actions = new EnumMap<>(MenuItemInteraction.class);

        /**
         * Creates a new menu slot.
         *
         * @param item        displayed item
         * @param interaction triggering interaction
         * @param action      action executed on click
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