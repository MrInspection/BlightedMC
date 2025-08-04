package fr.moussax.blightedMC.core.items;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.registry.items.MaterialsRegistry;
import fr.moussax.blightedMC.core.registry.armors.SpecialArmorRegistry;
import fr.moussax.blightedMC.core.registry.items.SpecialItems;
import org.bukkit.NamespacedKey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Central registry for all custom items in the BlightedMC plugin.
 * <p>
 * Responsible for:
 * <ul>
 *     <li>Storing all registered custom items</li>
 *     <li>Preventing duplicate item IDs</li>
 *     <li>Initializing items from all defined {@link ItemCategory} categories</li>
 * </ul>
 */
public final class ItemsRegistry {

  /**
   * Map of all registered items by their unique item ID.
   */
  public static final Map<String, ItemManager> BLIGHTED_ITEMS = new HashMap<>();

  /**
   * Namespaced key used to store and retrieve the item ID in {@link org.bukkit.persistence.PersistentDataContainer}.
   */
  public static final NamespacedKey ID_KEY = new NamespacedKey(BlightedMC.getInstance(), "id");

  /**
   * Removes all items from the registry.
   * <p>
   * Typically used during plugin reload or re-initialization.
   */
  public static void clearItems() {
    BLIGHTED_ITEMS.clear();
  }

  /**
   * Registers a new custom item in the registry.
   *
   * @param item The {@link ItemManager} to register
   * @throws IllegalArgumentException if an item with the same ID is already registered
   */
  public static void addItem(ItemManager item) {
    if (BLIGHTED_ITEMS.containsKey(item.getItemId())) {
      throw new IllegalArgumentException("Duplicate item ID: " + item.getItemId());
    }
    BLIGHTED_ITEMS.put(item.getItemId(), item);
  }

  /**
   * Initializes the registry by clearing existing items and registering all
   * items from the predefined {@link ItemCategory} implementations.
   */
  public static void initializeItems() {
    clearItems();

    List<ItemCategory> categories = List.of(
      new MaterialsRegistry(),
      new SpecialArmorRegistry(),
      new SpecialItems()
    );
    for (ItemCategory category : categories) {
      category.registerItems();
    }
  }
}
