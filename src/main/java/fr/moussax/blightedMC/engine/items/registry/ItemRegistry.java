package fr.moussax.blightedMC.engine.items.registry;

import fr.moussax.blightedMC.engine.items.BlightedItem;
import fr.moussax.blightedMC.registry.RegistryModule;
import fr.moussax.blightedMC.utils.debug.Log;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Central registry for all {@link BlightedItem} definitions available to the
 * BlightedMC item system.
 *
 * <p>Items are supplied by registered {@link RegistryModule} implementations
 * and indexed by their unique item ID. The registry can also resolve a
 * registered item from an {@link ItemStack} containing its persistent item ID.</p>
 */
public final class ItemRegistry {
    private static final Map<String, BlightedItem> REGISTERED_ITEMS = new HashMap<>();

    private ItemRegistry() {
    }

    /**
     * Initializes the item registry using the provided list of modules.
     *
     * <p>Previously registered items are cleared before all configured item
     * modules are loaded.</p>
     *
     * @param modules the list of registry modules to load
     */
    public static void initialize(List<RegistryModule<Consumer<BlightedItem>>> modules) {
        clear();
        modules.forEach(module -> module.register(ItemRegistry::register));
        Log.success("ItemDirectory", "Registered " + REGISTERED_ITEMS.size() + " custom items.");
    }

    /**
     * Resolves a registered {@link BlightedItem} from an item stack.
     *
     * @param itemStack the item stack to resolve
     * @return the registered item represented by the stack, or {@code null}
     *         if the stack does not represent a registered item
     */
    @Nullable
    public static BlightedItem fromItemStack(@NonNull ItemStack itemStack) {
        return BlightedItem.fromItemStack(itemStack);
    }

    /**
     * Registers a custom item using its item ID as the registry key.
     *
     * @param blightedItem the item to register
     * @throws IllegalArgumentException if an item with the same ID is already registered
     */
    public static void register(@NonNull BlightedItem blightedItem) {
        if (REGISTERED_ITEMS.containsKey(blightedItem.getItemId())) {
            throw new IllegalArgumentException("Duplicate item ID: " + blightedItem.getItemId());
        }
        REGISTERED_ITEMS.put(blightedItem.getItemId(), blightedItem);
    }

    /**
     * Registers multiple custom items.
     *
     * @param items the items to register
     */
    public static void register(BlightedItem... items) {
        for (BlightedItem item : items) {
            register(item);
        }
    }

    /**
     * Returns a registered item by its unique item ID.
     *
     * @param itemId the ID of the item to retrieve
     * @return the registered item
     * @throws IllegalArgumentException if no item is registered with the given ID
     */
    @NonNull
    public static BlightedItem getItem(@NonNull String itemId) {
        BlightedItem item = REGISTERED_ITEMS.get(itemId);
        if (item == null) {
            throw new IllegalArgumentException("Unknown item ID: " + itemId);
        }
        return item;
    }

    /**
     * Returns all currently registered custom items.
     *
     * @return a new immutable list containing all registered items
     */
    public static List<BlightedItem> getAllItems() {
        return List.copyOf(REGISTERED_ITEMS.values());
    }

    /**
     * Removes all items currently registered in the registry.
     */
    public static void clear() {
        REGISTERED_ITEMS.clear();
    }
}
