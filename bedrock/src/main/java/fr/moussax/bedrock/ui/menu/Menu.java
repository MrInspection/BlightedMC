package fr.moussax.bedrock.ui.menu;

import fr.moussax.bedrock.ui.menu.interaction.MenuAction;
import fr.moussax.bedrock.ui.menu.interaction.MenuElementPreset;
import fr.moussax.bedrock.ui.menu.interaction.MenuItemInteraction;
import fr.moussax.bedrock.ui.menu.system.MenuSystem;
import fr.moussax.bedrock.ui.menu.types.InteractiveMenu;
import fr.moussax.bedrock.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Base class for interactive inventory menus.
 *
 * <p>Menus define their contents through {@link #build(Player)} and can bind
 * actions to individual slots for different click interactions.</p>
 *
 * <p>Supports presets, dynamically built items, slot filling, submenus,
 * navigation, and in-place refreshes.</p>
 */
public abstract class Menu implements InventoryHolder {

    @Getter
    protected String title;
    protected final int size;

    @Getter
    protected final Map<Integer, MenuSlot> slots = new HashMap<>();

    protected Inventory inventory;
    protected UUID viewerId;
    protected MenuSystem menuSystem;

    /**
     * Creates a menu with the specified title and inventory size.
     *
     * @param title menu title
     * @param size  inventory size, in multiples of 9
     */
    public Menu(String title, int size) {
        this.title = title;
        this.size = size;
        this.inventory = Bukkit.createInventory(this, size, title);
    }

    /**
     * Sets the menu title and updates the active viewer's inventory view title if open.
     *
     * @param title new menu title
     */
    public void setTitle(@NonNull String title) {
        this.title = title;
        Player player = getPlayer();
        if (player != null && player.getOpenInventory().getTopInventory().getHolder() == this) {
            player.getOpenInventory().setTitle(title);
        }
    }

    /**
     * Assigns the menu system used to manage this menu.
     *
     * @param menuSystem menu system instance
     */
    public void setMenuSystem(@NonNull MenuSystem menuSystem) {
        this.menuSystem = menuSystem;
    }

    /**
     * Builds the menu contents for the specified player.
     *
     * @param player player viewing the menu
     */
    public abstract void build(Player player);

    /**
     * Opens this menu for the specified player.
     *
     * <p>The menu is rebuilt before opening and registered with the menu system.</p>
     *
     * @param player player for whom the menu is opened
     */
    public void open(@NonNull Player player) {
        if (menuSystem == null && MenuSystem.getInstance() != null) {
            menuSystem = MenuSystem.getInstance();
        }

        if (menuSystem == null || menuSystem.isShuttingDown()) {
            return;
        }

        this.viewerId = player.getUniqueId();

        slots.clear();
        build(player);
        this.inventory = Bukkit.createInventory(this, size, this.title);

        for (Map.Entry<Integer, MenuSlot> entry : slots.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().item);
        }

        menuSystem.registerMenu(player, this);
        player.openInventory(inventory);
    }

    /**
     * Returns the player currently viewing this menu, if online.
     *
     * @return current viewer, or {@code null} if unavailable
     */
    @Nullable
    protected Player getPlayer() {
        return viewerId != null ? Bukkit.getPlayer(viewerId) : null;
    }

    /**
     * Places an item in a slot with an interaction-specific action.
     *
     * <p>If the slot already exists, its displayed item and action are updated
     * without removing its other registered actions.</p>
     *
     * @param slot        inventory slot index
     * @param item        item displayed in the slot
     * @param interaction interaction that triggers the action
     * @param action      action executed when triggered
     */
    public void setItem(int slot, @NonNull ItemStack item, @NonNull MenuItemInteraction interaction, @NonNull MenuAction action) {
        MenuSlot existing = slots.get(slot);
        if (existing != null) {
            existing.item = item;
            existing.addAction(interaction, action);
        } else {
            slots.put(slot, new MenuSlot(item, interaction, action));
        }
    }

    /**
     * Places an item with separate left- and right-click actions.
     *
     * @param slot    inventory slot index
     * @param item    item displayed in the slot
     * @param onLeft  action executed on left-click
     * @param onRight action executed on right-click
     */
    public void setItem(int slot, @NonNull ItemStack item, @NonNull MenuAction onLeft, @NonNull MenuAction onRight) {
        setItem(slot, item, MenuItemInteraction.LEFT_CLICK, onLeft);
        MenuSlot menuSlot = slots.get(slot);
        if (menuSlot != null) {
            menuSlot.addAction(MenuItemInteraction.RIGHT_CLICK, onRight);
        }
    }

    /**
     * Places an item with multiple interaction-specific actions.
     *
     * @param slot    inventory slot index
     * @param item    item displayed in the slot
     * @param actions interaction-to-action mappings
     */
    public void setItem(int slot, @NonNull ItemStack item, @NonNull Map<MenuItemInteraction, MenuAction> actions) {
        slots.put(slot, new MenuSlot(item, actions));
    }

    /**
     * Adds an action to an existing slot.
     *
     * @param slot        inventory slot index
     * @param interaction interaction that triggers the action
     * @param action      action executed when triggered
     */
    public void addAction(int slot, @NonNull MenuItemInteraction interaction, @NonNull MenuAction action) {
        MenuSlot existing = slots.get(slot);
        if (existing != null) {
            existing.addAction(interaction, action);
        }
    }

    /**
     * Places a preset item with an interaction-specific action.
     *
     * @param slot        inventory slot index
     * @param preset      item preset
     * @param interaction interaction that triggers the action
     * @param action      action executed when triggered
     */
    public void setItem(int slot, @NonNull MenuElementPreset preset, @NonNull MenuItemInteraction interaction, @NonNull MenuAction action) {
        setItem(slot, preset.getItem(), interaction, action);
    }

    /**
     * Places a static item without an action.
     *
     * @param slot inventory slot index
     * @param item item displayed in the slot
     */
    public void setItem(int slot, @NonNull ItemStack item) {
        setItem(slot, item, MenuItemInteraction.ANY_CLICK, (player, type) -> {
        });
    }

    /**
     * Places a preset item without an action.
     *
     * @param slot   inventory slot index
     * @param preset item preset
     */
    public void setItem(int slot, @NonNull MenuElementPreset preset) {
        setItem(slot, preset.getItem());
    }

    /**
     * Places an item with an {@link MenuItemInteraction#ANY_CLICK} action.
     *
     * @param slot   inventory slot index
     * @param item   item displayed in the slot
     * @param action action executed on click
     */
    public void setItem(int slot, @NonNull ItemStack item, @NonNull MenuAction action) {
        setItem(slot, item, MenuItemInteraction.ANY_CLICK, action);
    }

    /**
     * Places a preset item with an {@link MenuItemInteraction#ANY_CLICK} action.
     *
     * @param slot   inventory slot index
     * @param preset item preset
     * @param action action executed on click
     */
    public void setItem(int slot, @NonNull MenuElementPreset preset, @NonNull MenuAction action) {
        setItem(slot, preset.getItem(), MenuItemInteraction.ANY_CLICK, action);
    }

    /**
     * Places the standard close button in the specified slot.
     *
     * @param slot inventory slot index
     */
    public void setCloseButton(int slot) {
        setItem(slot, MenuElementPreset.CLOSE_BUTTON, MenuItemInteraction.ANY_CLICK, (player, type) -> close());
    }

    /**
     * Places the standard back button with the specified action.
     *
     * @param slot   inventory slot index
     * @param action action executed when clicked
     */
    public void setBackButton(int slot, @NonNull MenuAction action) {
        setItem(slot, MenuElementPreset.BACK_BUTTON, MenuItemInteraction.ANY_CLICK, action);
    }

    /**
     * Places a back button that opens the specified previous menu.
     *
     * @param slot         inventory slot index
     * @param previousMenu menu to open after closing this menu
     */
    public void setBackButton(int slot, @Nullable Menu previousMenu) {
        setBackButton(slot, (player, _) -> {
            if (previousMenu == null) {
                close();
                return;
            }
            previousMenu.open(player);
        });
    }

    /**
     * Builds and places an item with separate left- and right-click actions.
     *
     * @param slot     inventory slot index
     * @param material base material
     * @param builder  item configuration callback
     * @param onLeft   action executed on left-click
     * @param onRight  action executed on right-click
     */
    public void setItem(
            int slot,
            @NonNull Material material,
            @NonNull Consumer<ItemBuilder> builder,
            @NonNull MenuAction onLeft,
            @NonNull MenuAction onRight
    ) {
        ItemBuilder itemBuilder = new ItemBuilder(material);
        builder.accept(itemBuilder);
        setItem(slot, itemBuilder.toItemStack(), onLeft, onRight);
    }

    /**
     * Builds and places an item with an interaction-specific action.
     *
     * @param slot        inventory slot index
     * @param material    base material
     * @param builder     item configuration callback
     * @param interaction interaction that triggers the action
     * @param action      action executed when triggered
     */
    public void setItem(
            int slot,
            @NonNull Material material,
            @NonNull Consumer<ItemBuilder> builder,
            @NonNull MenuItemInteraction interaction,
            @NonNull MenuAction action
    ) {
        ItemBuilder itemBuilder = new ItemBuilder(material);
        builder.accept(itemBuilder);
        setItem(slot, itemBuilder.toItemStack(), interaction, action);
    }

    /**
     * Builds and places an item with an {@link MenuItemInteraction#ANY_CLICK} action.
     *
     * @param slot     inventory slot index
     * @param material base material
     * @param builder  item configuration callback
     * @param action   action executed on click
     */
    public void setItem(
            int slot,
            @NonNull Material material,
            @NonNull Consumer<ItemBuilder> builder,
            @NonNull MenuAction action
    ) {
        setItem(slot, material, builder, MenuItemInteraction.ANY_CLICK, action);
    }

    /**
     * Builds and places a decorative item without an action.
     *
     * @param slot     inventory slot index
     * @param material base material
     * @param builder  item configuration callback
     */
    public void setItem(int slot, @NonNull Material material, @NonNull Consumer<ItemBuilder> builder) {
        setItem(slot, material, builder, (player, type) -> {
        });
    }

    /**
     * Fills the specified slots with independently configured items.
     *
     * @param slots    inventory slot indices
     * @param material base material
     * @param builder  item configuration callback
     */
    public void fillSlots(int[] slots, @NonNull Material material, @NonNull Consumer<ItemBuilder> builder) {
        for (int slot : slots) {
            setItem(slot, material, builder);
        }
    }

    /**
     * Fills the specified slots with an item and interaction type.
     *
     * @param slots       inventory slot indices
     * @param item        item displayed in each slot
     * @param interaction interaction that triggers each slot
     */
    public void fillSlots(int[] slots, @NonNull ItemStack item, @NonNull MenuItemInteraction interaction) {
        for (int slot : slots) {
            setItem(slot, item, interaction, (player, type) -> {
            });
        }
    }

    /**
     * Fills the specified slots with a preset and interaction type.
     *
     * @param slots       inventory slot indices
     * @param preset      item preset
     * @param interaction interaction that triggers each slot
     */
    public void fillSlots(int[] slots, @NonNull MenuElementPreset preset, @NonNull MenuItemInteraction interaction) {
        for (int slot : slots) {
            setItem(slot, preset, interaction, (player, type) -> {
            });
        }
    }

    /**
     * Fills the specified slots with an item using {@link MenuItemInteraction#ANY_CLICK}.
     *
     * @param slots inventory slot indices
     * @param item  item displayed in each slot
     */
    public void fillSlots(int[] slots, @NonNull ItemStack item) {
        fillSlots(slots, item, MenuItemInteraction.ANY_CLICK);
    }

    /**
     * Fills the specified slots with a preset using {@link MenuItemInteraction#ANY_CLICK}.
     *
     * @param slots  inventory slot indices
     * @param preset item preset
     */
    public void fillSlots(int[] slots, @NonNull MenuElementPreset preset) {
        fillSlots(slots, preset.getItem(), MenuItemInteraction.ANY_CLICK);
    }

    /**
     * Fills every currently empty slot with a configured item.
     *
     * @param material base material
     * @param builder  item configuration callback
     */
    public void fillEmptyWith(@NonNull Material material, @NonNull Consumer<ItemBuilder> builder) {
        for (int slot = 0; slot < size; slot++) {
            if (!slots.containsKey(slot)) {
                setItem(slot, material, builder);
            }
        }
    }

    /**
     * Fills every currently empty slot with the specified item.
     *
     * @param item item displayed in empty slots
     */
    public void fillEmptyWith(@NonNull ItemStack item) {
        for (int slot = 0; slot < size; slot++) {
            if (!slots.containsKey(slot)) {
                setItem(slot, item, MenuItemInteraction.ANY_CLICK, (_, _) -> {
                });
            }
        }
    }

    /**
     * Fills every currently empty slot with the specified preset.
     *
     * @param preset item preset
     */
    public void fillEmptyWith(@NonNull MenuElementPreset preset) {
        fillEmptyWith(preset.getItem());
    }

    /**
     * Clears both the displayed inventory and the menu's slot definitions.
     */
    public void clearInventory() {
        inventory.clear();
        slots.clear();
    }

    /**
     * Returns the Bukkit inventory represented by this menu.
     *
     * @return menu inventory
     */
    @Override
    @NonNull
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Opens a submenu for the player currently viewing this menu.
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
     * Returns the current viewer to the previous menu.
     */
    public void goBack() {
        Player player = getPlayer();
        if (player != null && menuSystem != null) {
            menuSystem.goBack(player);
        }
    }

    /**
     * Closes the menu for its current viewer.
     */
    public void close() {
        Player player = getPlayer();
        if (player != null) {
            player.closeInventory();
        }
    }

    /**
     * Converts row and column coordinates into an inventory slot index.
     *
     * @param row    zero-based row index
     * @param column zero-based column index
     * @return corresponding inventory slot index
     */
    public static int getSlot(int row, int column) {
        return row * 9 + column;
    }

    /**
     * Places an item at the specified row and column.
     *
     * @param row         zero-based row index
     * @param column      zero-based column index
     * @param item        item displayed in the slot
     * @param interaction interaction that triggers the action
     * @param action      action executed when triggered
     */
    public void setItem(int row, int column, @NonNull ItemStack item, @NonNull MenuItemInteraction interaction, @NonNull MenuAction action) {
        setItem(getSlot(row, column), item, interaction, action);
    }

    /**
     * Places an item at the specified row and column with separate left- and right-click actions.
     *
     * @param row     zero-based row index
     * @param column  zero-based column index
     * @param item    item displayed in the slot
     * @param onLeft  action executed on left-click
     * @param onRight action executed on right-click
     */
    public void setItem(int row, int column, @NonNull ItemStack item, @NonNull MenuAction onLeft, @NonNull MenuAction onRight) {
        setItem(getSlot(row, column), item, onLeft, onRight);
    }

    /**
     * Places an item at the specified row and column with an ANY_CLICK action.
     *
     * @param row    zero-based row index
     * @param column zero-based column index
     * @param item   item displayed in the slot
     * @param action action executed on click
     */
    public void setItem(int row, int column, @NonNull ItemStack item, @NonNull MenuAction action) {
        setItem(getSlot(row, column), item, action);
    }

    /**
     * Places a static item at the specified row and column.
     *
     * @param row    zero-based row index
     * @param column zero-based column index
     * @param item   item displayed in the slot
     */
    public void setItem(int row, int column, @NonNull ItemStack item) {
        setItem(getSlot(row, column), item, (player, type) -> {
        });
    }

    /**
     * Builds and places an item at the specified row and column.
     *
     * @param row      zero-based row index
     * @param column   zero-based column index
     * @param material base material
     * @param builder  item configuration callback
     * @param action   action executed on click
     */
    public void setItem(int row, int column, @NonNull Material material, @NonNull Consumer<ItemBuilder> builder, @NonNull MenuAction action) {
        setItem(getSlot(row, column), material, builder, action);
    }

    /**
     * Builds and places a decorative item at the specified row and column.
     *
     * @param row      zero-based row index
     * @param column   zero-based column index
     * @param material base material
     * @param builder  item configuration callback
     */
    public void setItem(int row, int column, @NonNull Material material, @NonNull Consumer<ItemBuilder> builder) {
        setItem(getSlot(row, column), material, builder);
    }

    /**
     * Places a preset item at the specified row and column with an ANY_CLICK action.
     *
     * @param row    zero-based row index
     * @param column zero-based column index
     * @param preset item preset
     * @param action action executed on click
     */
    public void setItem(int row, int column, @NonNull MenuElementPreset preset, @NonNull MenuAction action) {
        setItem(getSlot(row, column), preset, action);
    }

    /**
     * Places a preset item at the specified row and column.
     *
     * @param row    zero-based row index
     * @param column zero-based column index
     * @param preset item preset
     */
    public void setItem(int row, int column, @NonNull MenuElementPreset preset) {
        setItem(getSlot(row, column), preset.getItem());
    }

    /**
     * Fills the outermost row and column slots with an item.
     *
     * @param item item displayed on the border
     */
    public void fillBorder(@NonNull ItemStack item) {
        int rows = size / 9;
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < 9; column++) {
                if (row == 0 || row == rows - 1 || column == 0 || column == 8) {
                    setItem(getSlot(row, column), item);
                }
            }
        }
    }

    /**
     * Fills the outermost row and column slots with a preset.
     *
     * @param preset item preset displayed on the border
     */
    public void fillBorder(@NonNull MenuElementPreset preset) {
        fillBorder(preset.getItem());
    }

    /**
     * Fills slots matching a symbol in a two-dimensional pattern.
     *
     * <p>Spaces are ignored, allowing patterns to be formatted for readability.</p>
     *
     * @param pattern pattern rows
     * @param symbol  character identifying slots to fill
     * @param item    item displayed in matching slots
     */
    public void fillPattern(String[] pattern, char symbol, @NonNull ItemStack item) {
        for (int row = 0; row < pattern.length && row < size / 9; row++) {
            String replaced = pattern[row].replace(" ", "");
            for (int column = 0; column < replaced.length() && column < 9; column++) {
                if (replaced.charAt(column) == symbol) {
                    setItem(getSlot(row, column), item);
                }
            }
        }
    }

    /**
     * Fills slots matching a symbol in a two-dimensional pattern with a preset.
     *
     * @param pattern pattern rows
     * @param symbol  character identifying slots to fill
     * @param preset  item preset displayed in matching slots
     */
    public void fillPattern(String[] pattern, char symbol, @NonNull MenuElementPreset preset) {
        fillPattern(pattern, symbol, preset.getItem());
    }

    /**
     * Determines whether a slot permits player inventory interaction.
     *
     * <p>Menus deny interaction by default. Subclasses such as
     * {@link InteractiveMenu} may override this behavior.</p>
     *
     * @param slot inventory slot index
     * @return {@code true} if the slot is interactable
     */
    public boolean isInteractable(int slot) {
        return false;
    }

    /**
     * Called after a player modifies an interactable slot.
     *
     * <p>The default implementation performs no action.</p>
     *
     * @param player player who modified the menu
     */
    public void onUpdate(@NonNull Player player) {
    }

    /**
     * Called when the menu is closed by a player.
     *
     * <p>The default implementation performs no action.</p>
     *
     * @param player player who closed the menu
     */
    public void onClose(Player player) {
    }

    /**
     * Refreshes the menu contents without closing the inventory.
     *
     * <p>The menu is rebuilt through {@link #build(Player)} and registered
     * slots are updated while interactable slots are preserved.</p>
     *
     * @param player player viewing the menu
     */
    @SuppressWarnings("UnstableApiUsage")
    public void refresh(@NonNull Player player) {
        if (menuSystem != null && menuSystem.isShuttingDown()) {
            return;
        }

        slots.clear();
        build(player);

        InventoryView openInventory = player.getOpenInventory();
        if (openInventory.getTopInventory().getHolder() == this && !openInventory.getTitle().equals(this.title)) {
            openInventory.setTitle(this.title);
        }

        boolean changed = false;
        for (int slot = 0; slot < size; slot++) {
            MenuSlot menuSlot = slots.get(slot);
            if (menuSlot == null) {
                continue;
            }

            ItemStack newItem = menuSlot.item;
            ItemStack currentItem = inventory.getItem(slot);

            if (!isSameItem(currentItem, newItem)) {
                inventory.setItem(slot, newItem);
                changed = true;
            }
        }

        if (changed) {
            player.updateInventory();
        }
    }

    private boolean isSameItem(@Nullable ItemStack first, @Nullable ItemStack second) {
        if (first == null && second == null) return true;
        if (first == null || second == null) return false;
        return first.getAmount() == second.getAmount() && first.isSimilar(second);
    }

    /**
     * Updates a single slot without rebuilding the menu.
     *
     * <p>All other slots and the player's cursor remain unchanged.</p>
     *
     * @param slot inventory slot index
     * @param item new item for the slot
     */
    protected void setSlotItem(int slot, @Nullable ItemStack item) {
        if (item != null && item.getType() != Material.AIR) {
            MenuSlot existing = slots.get(slot);
            if (existing != null) {
                existing.item = item;
            } else {
                slots.put(slot, new MenuSlot(item, MenuItemInteraction.ANY_CLICK, (player, type) -> {}));
            }
            inventory.setItem(slot, item);
        } else {
            slots.remove(slot);
            inventory.setItem(slot, null);
        }
    }

    /**
     * Represents an inventory slot and its registered click actions.
     */
    public static class MenuSlot {

        public ItemStack item;
        private final Map<MenuItemInteraction, MenuAction> actions = new EnumMap<>(MenuItemInteraction.class);

        /**
         * Creates a slot with a single interaction action.
         *
         * @param item        item displayed in the slot
         * @param interaction interaction that triggers the action
         * @param action      action executed when triggered
         */
        public MenuSlot(@NonNull ItemStack item, @NonNull MenuItemInteraction interaction, @NonNull MenuAction action) {
            this.item = item;
            actions.put(interaction, action);
        }

        /**
         * Creates a slot with multiple interaction actions.
         *
         * @param item    item displayed in the slot
         * @param actions interaction-to-action mappings
         */
        public MenuSlot(@NonNull ItemStack item, @NonNull Map<MenuItemInteraction, MenuAction> actions) {
            this.item = item;
            this.actions.putAll(actions);
        }

        /**
         * Registers or replaces an action for an interaction.
         *
         * @param interaction interaction that triggers the action
         * @param action      action executed when triggered
         * @return this slot
         */
        public MenuSlot addAction(@NonNull MenuItemInteraction interaction, @NonNull MenuAction action) {
            actions.put(interaction, action);
            return this;
        }

        /**
         * Executes the action matching the specified click type.
         *
         * <p>Specific left, right, or middle-click actions take precedence
         * over {@link MenuItemInteraction#ANY_CLICK}.</p>
         *
         * @param player    player who clicked the slot
         * @param clickType click type received
         */
        public void handle(@NonNull Player player, @NonNull ClickType clickType) {
            boolean handled = false;

            if (clickType.isLeftClick() && actions.containsKey(MenuItemInteraction.LEFT_CLICK)) {
                actions.get(MenuItemInteraction.LEFT_CLICK).execute(player, clickType);
                handled = true;
            } else if (clickType.isRightClick() && actions.containsKey(MenuItemInteraction.RIGHT_CLICK)) {
                actions.get(MenuItemInteraction.RIGHT_CLICK).execute(player, clickType);
                handled = true;
            } else if (clickType == ClickType.MIDDLE && actions.containsKey(MenuItemInteraction.MIDDLE_CLICK)) {
                actions.get(MenuItemInteraction.MIDDLE_CLICK).execute(player, clickType);
                handled = true;
            }

            if (!handled && actions.containsKey(MenuItemInteraction.ANY_CLICK)) {
                actions.get(MenuItemInteraction.ANY_CLICK).execute(player, clickType);
            }
        }
    }
}
