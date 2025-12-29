package fr.moussax.blightedMC.smp.core.shared.ui.menu;

import fr.moussax.blightedMC.smp.core.shared.ui.menu.interaction.MenuAction;
import fr.moussax.blightedMC.smp.core.shared.ui.menu.interaction.MenuElementPreset;
import fr.moussax.blightedMC.smp.core.shared.ui.menu.interaction.MenuItemInteraction;
import fr.moussax.blightedMC.smp.core.shared.ui.menu.system.MenuSystem;
import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Represents a custom interactive inventory menu.
 *
 * <p>Supports placing items, presets, or dynamically generated items via {@link ItemBuilder},
 * handling different click types with {@link MenuItemInteraction}, and executing actions with {@link MenuAction}.</p>
 *
 * <p>Provides methods for opening, closing, refreshing menus, filling slots, and managing submenus.</p>
 */
public abstract class Menu implements InventoryHolder {
    protected final String title;
    protected final int size;
    protected final Map<Integer, MenuSlot> slots = new HashMap<>();
    protected Inventory inventory;
    protected UUID viewerId;
    protected MenuSystem menuSystem;

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
     * Injects the menu system instance.
     *
     * <p>Should be called before opening the menu.</p>
     *
     * @param menuSystem menu system instance
     */
    public void setMenuSystem(@NonNull MenuSystem menuSystem) {
        this.menuSystem = menuSystem;
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
     * registers the menu in the menu system.</p>
     *
     * @param player target player
     */
    public void open(@NonNull Player player) {
        if (menuSystem == null || menuSystem.isShuttingDown()) {
            return;
        }

        this.viewerId = player.getUniqueId();

        slots.clear();
        build(player);

        inventory.clear();
        for (Map.Entry<Integer, MenuSlot> entry : slots.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().item);
        }

        player.openInventory(inventory);
        menuSystem.registerMenu(player, this);
    }

    /**
     * Safely retrieves the player currently viewing this menu.
     *
     * @return the player, or null if they are offline
     */
    @Nullable
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
    public void setItem(int slot, @NonNull ItemStack item, @NonNull MenuItemInteraction interaction, @NonNull MenuAction action) {
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
    public void setItem(int slot, @NonNull MenuElementPreset preset, @NonNull MenuItemInteraction interaction, @NonNull MenuAction action) {
        setItem(slot, preset.getItem(), interaction, action);
    }

    /**
     * Places a dynamically generated item in a slot with an associated action.
     *
     * @param slot        slot index
     * @param builder     consumer that configures the item using {@link ItemBuilder}
     * @param interaction triggering interaction
     * @param action      action to execute when clicked
     */
    public void setItem(int slot, @NonNull Consumer<ItemBuilder> builder,
                        @NonNull MenuItemInteraction interaction,
                        @NonNull MenuAction action) {
        ItemBuilder itemBuilder = new ItemBuilder(Material.STONE);
        builder.accept(itemBuilder);
        setItem(slot, itemBuilder.toItemStack(), interaction, action);
    }

    /**
     * Places a dynamically generated item in a slot using any click.
     *
     * @param slot    slot index
     * @param builder consumer that configures the item using {@link ItemBuilder}
     */
    public void setItem(int slot, @NonNull Consumer<ItemBuilder> builder) {
        setItem(slot, builder, MenuItemInteraction.ANY_CLICK, (player, type) -> {
        });
    }

    /**
     * Fills the specified slots with dynamically generated items using any click.
     *
     * @param slots   slot indices to fill
     * @param builder consumer that configures each item using {@link ItemBuilder}
     */
    public void fillSlots(int[] slots, @NonNull Consumer<ItemBuilder> builder) {
        for (int slot : slots) {
            setItem(slot, builder);
        }
    }

    /**
     * Fills the specified slots with an ItemStack.
     *
     * @param slots       slot indices to fill
     * @param item        item to place
     * @param interaction interaction type for each slot
     */
    public void fillSlots(int[] slots, @NonNull ItemStack item, @NonNull MenuItemInteraction interaction) {
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
    public void fillSlots(int[] slots, @NonNull MenuElementPreset preset, @NonNull MenuItemInteraction interaction) {
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
    public void fillSlots(int[] slots, @NonNull ItemStack item) {
        fillSlots(slots, item, MenuItemInteraction.ANY_CLICK);
    }

    /**
     * Fills the specified slots with a MenuElementPreset using ANY_CLICK.
     *
     * @param slots  slot indices to fill
     * @param preset preset item
     */
    public void fillSlots(int[] slots, @NonNull MenuElementPreset preset) {
        fillSlots(slots, preset.getItem(), MenuItemInteraction.ANY_CLICK);
    }

    /**
     * Fills all empty slots in the menu with dynamically generated items using any click.
     *
     * @param builder consumer that configures each item using {@link ItemBuilder}
     */
    public void fillEmptyWith(@NonNull Consumer<ItemBuilder> builder) {
        for (int slot = 0; slot < size; slot++) {
            if (!slots.containsKey(slot)) {
                setItem(slot, builder);
            }
        }
    }

    /**
     * Fills all empty slots in the menu with a specific ItemStack.
     *
     * @param item item to fill empty slots
     */
    public void fillEmptyWith(@NonNull ItemStack item) {
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
    public void fillEmptyWith(@NonNull MenuElementPreset preset) {
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
    public void openSubMenu(@NonNull Menu submenu) {
        Player player = getPlayer();
        if (player != null && menuSystem != null) {
            submenu.setMenuSystem(menuSystem);
            submenu.open(player);
        }
    }

    /**
     * Returns to the previous menu.
     */
    public void goBack() {
        Player player = getPlayer();
        if (player != null && menuSystem != null) {
            menuSystem.goBack(player);
        }
    }

    public Map<Integer, MenuSlot> getSlots() {
        return slots;
    }

    /**
     * Closes the menu for the current player.
     */
    public void close() {
        Player player = getPlayer();
        if (player != null) {
            player.closeInventory();
        }
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
    protected void refresh(@NonNull Player player) {
        if (menuSystem != null && menuSystem.isShuttingDown()) {
            return;
        }

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
        public MenuSlot(@NonNull ItemStack item, @NonNull MenuItemInteraction interaction, @NonNull MenuAction action) {
            this.item = item;
            actions.put(interaction, action);
        }

        /**
         * Executes the action matching the click type.
         *
         * @param player    clicking player
         * @param clickType type of click
         */
        public void handle(@NonNull Player player, @NonNull ClickType clickType) {
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
