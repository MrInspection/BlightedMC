package fr.moussax.blightedMC.core.items;

/**
 * Represents a category of custom items in the game.
 * <p>
 * Each category is responsible for registering its items in the {@link ItemsRegistry}
 * during plugin initialization or reload.
 */
public interface ItemCategory {

  /**
   * Registers all items belonging to this category into the item registry.
   * <p>
   * Implementations should create and register their items here.
   */
  void registerItems();
}
