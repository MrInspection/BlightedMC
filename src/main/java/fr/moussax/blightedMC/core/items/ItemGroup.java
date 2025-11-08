package fr.moussax.blightedMC.core.items;

import java.util.List;

public interface ItemGroup {
  /**
   * Create and return the list of items belonging to this category.
   * Items will be registered centrally by ItemsRegistry.
   */
  List<ItemTemplate> registerItems();
}
