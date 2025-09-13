package fr.moussax.blightedMC.core.items.registry;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.items.ItemManager;
import fr.moussax.blightedMC.core.items.ItemCategory;
import fr.moussax.blightedMC.registry.armors.ArmorRegistry;
import fr.moussax.blightedMC.registry.blocks.BlockItemsRegistry;
import fr.moussax.blightedMC.registry.items.BlightedItems;
import fr.moussax.blightedMC.registry.items.BlightedMaterials;
import fr.moussax.blightedMC.registry.armors.SpecialArmorRegistry;
import fr.moussax.blightedMC.registry.items.SpecialItems;
import org.bukkit.NamespacedKey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ItemsRegistry {
  public static final Map<String, ItemManager> BLIGHTED_ITEMS = new HashMap<>();
  public static final NamespacedKey ID_KEY = new NamespacedKey(BlightedMC.getInstance(), "id");

  public static void clearItems() {
    BLIGHTED_ITEMS.clear();
  }

  public static void addItem(ItemManager item) {
    if (BLIGHTED_ITEMS.containsKey(item.getItemId())) {
      throw new IllegalArgumentException("Duplicate item ID: " + item.getItemId());
    }
    BLIGHTED_ITEMS.put(item.getItemId(), item);
  }

  public static List<ItemManager> getAllItems() {
    return List.copyOf(BLIGHTED_ITEMS.values());
  }

  public static void initializeItems() {
    clearItems();

    List<ItemCategory> categories = List.of(
      new BlightedMaterials(),
      new SpecialArmorRegistry(),
      new ArmorRegistry(),
      new BlockItemsRegistry(),
      new SpecialItems(),
      new BlightedItems()
    );
    for (ItemCategory category : categories) {
      category.registerItems();
    }
  }
}
