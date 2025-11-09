package fr.moussax.blightedMC.core.items.registry;

import fr.moussax.blightedMC.core.items.ItemTemplate;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Interface for registering custom items into the {@link ItemDirectory}.
 *
 * <p>Implementations define items via {@link #defineItems()}, which returns a list
 * of {@link ItemTemplate} instances. Items are automatically registered to the central
 * directory using the {@link #add} helper methods.
 *
 * <p>For single items, use {@link #add(ItemTemplate)}:
 * <pre>{@code
 * public class Bonemerang implements ItemRegistry {
 *   @Override
 *   public List<ItemTemplate> defineItems() {
 *     ItemTemplate bonemerang = new ItemTemplate("BONEMERANG", ItemType.BOW, ...);
 *     bonemerang.addLore("...");
 *     bonemerang.addAbility(...);
 *     return ItemRegistry.add(bonemerang);
 *   }
 * }
 * }</pre>
 *
 * <p>For multiple items, use {@link #add(List)}:
 * <pre>{@code
 * public class WeaponSet implements ItemRegistry {
 *   @Override
 *   public List<ItemTemplate> defineItems() {
 *     ItemTemplate sword = new ItemTemplate("FIRE_SWORD", ...);
 *     ItemTemplate axe = new ItemTemplate("ICE_AXE", ...);
 *     return ItemRegistry.add(List.of(sword, axe));
 *   }
 * }
 * }</pre>
 */
public interface ItemRegistry {
  /**
   * Defines and registers items for this registry.
   *
   * @return list of registered item templates
   */
  List<ItemTemplate> defineItems();

  /**
   * Registers a single item to the {@link ItemDirectory}.
   *
   * @param item the item template to register
   * @return singleton list containing the registered item
   */
  static List<ItemTemplate> add(@Nonnull ItemTemplate item) {
    ItemDirectory.addItem(item);
    return List.of(item);
  }

  /**
   * Registers multiple items to the {@link ItemDirectory}.
   *
   * @param items the list of item templates to register
   * @return the same list of registered items
   */
  static List<ItemTemplate> add(@Nonnull List<ItemTemplate> items) {
    items.forEach(ItemDirectory::addItem);
    return items;
  }
}
