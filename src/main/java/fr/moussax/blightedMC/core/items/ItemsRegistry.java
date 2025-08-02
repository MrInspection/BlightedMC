package fr.moussax.blightedMC.core.items;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.__registry__.items.MaterialsRegistry;
import fr.moussax.blightedMC.core.__registry__.items.SpecialItemRegistry;
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

  public static void initializeItems() {
    clearItems();

    List<ItemCategory> categories = List.of(
        new MaterialsRegistry(),
        new SpecialItemRegistry()
    );
    for(ItemCategory category : categories) {
      category.registerItems();
    }
  }
}
