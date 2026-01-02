package fr.moussax.blightedMC.smp.core.items.registry;

import fr.moussax.blightedMC.smp.core.items.BlightedItem;
import fr.moussax.blightedMC.smp.features.items.armors.FishingArmors;
import fr.moussax.blightedMC.smp.features.items.armors.HomodeusArmor;
import fr.moussax.blightedMC.smp.features.items.armors.RocketBoots;
import fr.moussax.blightedMC.smp.features.items.blocks.BlightedBlockItems;
import fr.moussax.blightedMC.smp.features.items.*;
import fr.moussax.blightedMC.smp.features.items.materials.BlightedMaterials;
import fr.moussax.blightedMC.smp.features.items.materials.EndMaterials;
import fr.moussax.blightedMC.smp.features.items.materials.NetherMaterials;
import fr.moussax.blightedMC.utils.debug.Log;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The central registry for all custom {@link BlightedItem}.
 * <p>
 * This class is responsible for initializing {@link ItemProvider} modules
 * and maintaining the global lookup map.
 */
public final class ItemRegistry {
    private static final Map<String, BlightedItem> REGISTERED_ITEMS = new HashMap<>();

    private static final List<ItemProvider> MODULES = List.of(
        new BlightedGemstone(),
        new BlightedMaterials(),
        new Bonemerang(),
        new GlimmeringEye(),
        new KnightsSword(),
        new HomodeusArmor(),
        new RocketBoots(),
        new BlightedBlockItems(),
        new Hyperion(),
        new ThermalFuels(),
        new BlightedTools(),
        new NetherMaterials(),
        new EndMaterials(),
        new FishingArmors()
    );

    private ItemRegistry() {
    }

    /**
     * Initializes and registers all items defined across the project.
     * This method must be called during plugin startup before any items are referenced.
     */
    public static void initialize() {
        clear();
        MODULES.forEach(ItemProvider::register);
        Log.success("ItemDirectory", "Registered " + REGISTERED_ITEMS.size() + " custom items.");
    }

    /**
     * Retrieves a BlightedItem template from a physical ItemStack.
     * Uses the centralized key defined in BlightedItem.
     *
     * @param itemStack the item to check
     * @return the template, or null if not a custom item
     */
    @Nullable
    public static BlightedItem fromItemStack(@NonNull ItemStack itemStack) {
        return BlightedItem.fromItemStack(itemStack);
    }

    /**
     * Registers an item template to the directory.
     *
     * @param blightedItem the item template to register
     * @throws IllegalArgumentException if an item with the same ID is already registered
     */
    static void addItem(@NonNull BlightedItem blightedItem) {
        if (REGISTERED_ITEMS.containsKey(blightedItem.getItemId())) {
            throw new IllegalArgumentException("Duplicate item ID: " + blightedItem.getItemId());
        }
        REGISTERED_ITEMS.put(blightedItem.getItemId(), blightedItem);
    }

    /**
     * Retrieves an item template by its unique identifier.
     *
     * @param itemId the unique item identifier
     * @return the item template
     * @throws IllegalArgumentException if no item is found with the given ID
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
     * Retrieves all registered item templates.
     *
     * @return immutable copy of all registered items
     */
    public static List<BlightedItem> getAllItems() {
        return List.copyOf(REGISTERED_ITEMS.values());
    }

    /**
     * Clears all registered items from the directory.
     *
     * <p>This method should be used with caution, typically only during
     * plugin reload or shutdown procedures.
     */
    public static void clear() {
        REGISTERED_ITEMS.clear();
    }
}
