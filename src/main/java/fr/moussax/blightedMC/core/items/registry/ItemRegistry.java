package fr.moussax.blightedMC.core.items.registry;

import fr.moussax.blightedMC.core.items.ItemTemplate;

import java.util.List;

/**
 * Defines a contract for registering custom {@link ItemTemplate} instances into the global {@link ItemDirectory}.
 * <p>
 * Implementations are responsible for creating and returning their items via {@link #defineItems()}.
 * Items can be added individually or in batches using the provided {@link #add(ItemTemplate)} and {@link #add(List)} helpers.
 * <p>
 * Example for a single item:
 * <pre>{@code
 * public class BonemerangRegistry implements ItemRegistry {
 *   @Override
 *   public List<ItemTemplate> defineItems() {
 *     ItemTemplate bonemerang = new ItemTemplate("BONEMERANG", ItemType.BOW, ItemRarity.LEGENDARY, Material.BOW);
 *     bonemerang.addAbility(new BonemerangAbility());
 *     return ItemRegistry.add(bonemerang);
 *   }
 * }
 * }</pre>
 * Example for multiple items:
 * <pre>{@code
 * public class WeaponSetRegistry implements ItemRegistry {
 *   @Override
 *   public List<ItemTemplate> defineItems() {
 *     ItemTemplate sword = new ItemTemplate("FIRE_SWORD", ItemType.SWORD, ItemRarity.EPIC, Material.DIAMOND_SWORD);
 *     ItemTemplate axe = new ItemTemplate("ICE_AXE", ItemType.AXE, ItemRarity.EPIC, Material.DIAMOND_AXE);
 *     return ItemRegistry.add(List.of(sword, axe));
 *   }
 * }
 * }</pre>
 */
@FunctionalInterface
public interface ItemRegistry {
    /**
     * Defines and registers all custom items for this registry implementation.
     *
     * @return a list containing the registered item templates
     */
    List<ItemTemplate> defineItems();

    /**
     * Registers a single {@link ItemTemplate} into the {@link ItemDirectory}.
     *
     * @param item the item template to register
     * @return a singleton list containing the registered item
     */
    static List<ItemTemplate> add(ItemTemplate item) {
        ItemDirectory.addItem(item);
        return List.of(item);
    }

    /**
     * Registers multiple {@link ItemTemplate} instances into the {@link ItemDirectory}.
     *
     * @param items list of item templates to register
     * @return the same list of registered items
     */
    static List<ItemTemplate> add(List<ItemTemplate> items) {
        items.forEach(ItemDirectory::addItem);
        return items;
    }
}
