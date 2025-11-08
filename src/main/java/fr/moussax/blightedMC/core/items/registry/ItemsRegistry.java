package fr.moussax.blightedMC.core.items.registry;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.items.ItemGroup;
import fr.moussax.blightedMC.core.items.ItemTemplate;
import fr.moussax.blightedMC.gameplay.armors.HomodeusArmor;
import fr.moussax.blightedMC.gameplay.armors.RocketBoots;
import fr.moussax.blightedMC.gameplay.blocks.BlockItemsRegistry;
import fr.moussax.blightedMC.gameplay.items.BlightedItems;
import fr.moussax.blightedMC.gameplay.items.BlightedMaterials;
import fr.moussax.blightedMC.gameplay.items.SpecialItems;
import org.bukkit.NamespacedKey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ItemsRegistry {
  public static final Map<String, ItemTemplate> REGISTERED_ITEMS = new HashMap<>();
  public static final NamespacedKey ID_KEY = new NamespacedKey(BlightedMC.getInstance(), "id");

  public static void clearItems() {
    REGISTERED_ITEMS.clear();
  }

  public static void addItem(ItemTemplate itemTemplate) {
    if (REGISTERED_ITEMS.containsKey(itemTemplate.getItemId())) {
      throw new IllegalArgumentException("Duplicate item ID: " + itemTemplate.getItemId());
    }
    REGISTERED_ITEMS.put(itemTemplate.getItemId(), itemTemplate);
  }

  public static List<ItemTemplate> getAllItems() {
    return List.copyOf(REGISTERED_ITEMS.values());
  }

  public static void initializeItems() {
    clearItems();

    List<ItemGroup> categories = List.of(
      new BlightedMaterials(),
      new HomodeusArmor(),
      new RocketBoots(),
      new BlockItemsRegistry(),
      new SpecialItems(),
      new BlightedItems()
    );
    for (ItemGroup category : categories) {
      List<ItemTemplate> items = category.registerItems();
      if (items == null) continue;
      for (ItemTemplate item : items) {
        addItem(item);
      }
    }
  }
}
