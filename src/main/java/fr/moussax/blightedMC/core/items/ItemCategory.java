package fr.moussax.blightedMC.core.items;

/**
 * Represents a category of custom items to be registered in {@link ItemsRegistry}.
 */
public interface ItemCategory {

  /**
   * Registers all items of this category.
   */
  void registerItems();
}
