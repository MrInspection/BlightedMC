package fr.moussax.blightedMC.core.items;

import java.util.HashMap;
import java.util.List;

public final class ItemsRegistry {
  public static final HashMap<String, ItemManager> BLIGHTED_ITEMS = new HashMap<>();

  public static void clearItems() {
    BLIGHTED_ITEMS.clear();
  }

  public static void addItem(ItemManager item) {
    BLIGHTED_ITEMS.put(item.getItemId(), item);
  }

  public static void initializeItems() {
    clearItems();

    List<ItemCategory> categories = List.of(
        // Init categories to the registry, classes must be from __registry__
    );
    for(ItemCategory category : categories) {
      category.registerItems();
    }
  }
}
