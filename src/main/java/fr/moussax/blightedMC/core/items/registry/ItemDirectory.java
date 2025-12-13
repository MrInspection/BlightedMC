package fr.moussax.blightedMC.core.items.registry;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.items.ItemTemplate;
import fr.moussax.blightedMC.game.armors.HomodeusArmor;
import fr.moussax.blightedMC.game.armors.RocketBoots;
import fr.moussax.blightedMC.game.blocks.BlocksDirectory;
import fr.moussax.blightedMC.game.items.*;
import fr.moussax.blightedMC.utils.debug.Log;
import org.bukkit.NamespacedKey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Central directory for managing registered custom items.
 *
 * <p>This class maintains a registry of all {@link ItemTemplate} instances
 * defined through {@link ItemRegistry} implementations. Items are registered
 * automatically when {@link ItemRegistry#add} methods are invoked.
 *
 * <p>The directory provides item lookup by ID and maintains a persistent
 * {@link NamespacedKey} for item identification in Minecraft's data system.
 */
public final class ItemDirectory {
    private static final Map<String, ItemTemplate> REGISTERED_ITEMS = new HashMap<>();
    public static final NamespacedKey ID_KEY = new NamespacedKey(BlightedMC.getInstance(), "id");

    private static final List<ItemRegistry> REGISTRIES = List.of(
        new BlightedGemstone(),
        new BlightedMaterials(),
        new Bonemerang(),
        new GlimmeringEye(),
        new KnightsSword(),
        new HomodeusArmor(),
        new RocketBoots(),
        new BlocksDirectory(),
        new Hyperion()
    );

    private ItemDirectory() {
    }

    /**
     * Initializes and registers all items defined across the project.
     * This method must be called during plugin startup before any items are referenced.
     */
    public static void initializeItems() {
        clearItems();
        REGISTRIES.forEach(ItemRegistry::defineItems);
        Log.success("ItemDirectory", "Registered " + REGISTERED_ITEMS.size() + " custom items.");
    }

    /**
     * Registers an item template to the directory.
     *
     * @param itemTemplate the item template to register
     * @throws IllegalArgumentException if an item with the same ID is already registered
     */
    static void addItem(ItemTemplate itemTemplate) {
        if (REGISTERED_ITEMS.containsKey(itemTemplate.getItemId())) {
            throw new IllegalArgumentException("Duplicate item ID: " + itemTemplate.getItemId());
        }
        REGISTERED_ITEMS.put(itemTemplate.getItemId(), itemTemplate);
    }

    /**
     * Retrieves an item template by its unique identifier.
     *
     * @param itemId the unique item identifier
     * @return the item template, or {@code null} if not found
     */
    public static ItemTemplate getItem(String itemId) {
        return REGISTERED_ITEMS.get(itemId);
    }

    /**
     * Retrieves all registered item templates.
     *
     * @return immutable copy of all registered items
     */
    public static List<ItemTemplate> getAllItems() {
        return List.copyOf(REGISTERED_ITEMS.values());
    }

    /**
     * Clears all registered items from the directory.
     *
     * <p>This method should be used with caution, typically only during
     * plugin reload or shutdown procedures.
     */
    public static void clearItems() {
        REGISTERED_ITEMS.clear();
    }
}
