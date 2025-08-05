package fr.moussax.blightedMC.core.items.crafting;

import fr.moussax.blightedMC.core.items.ItemManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Represents an ingredient used in a crafting recipe, which can be either
 * a custom item managed by {@link ItemManager} or a vanilla {@link Material}.
 */
public class CraftingObject {
  private final ItemManager manager;
  private final ItemStack vanillaItem;
  private final int amount;
  private final String itemId;

  /**
   * Creates a crafting object representing a custom item.
   *
   * @param manager the {@link ItemManager} managing the custom item
   * @param amount  the required quantity
   */
  public CraftingObject(ItemManager manager, int amount) {
    this.manager = manager;
    this.vanillaItem = null;
    this.amount = amount;
    this.itemId = manager != null ? manager.getItemId() : "";
  }

  /**
   * Creates a crafting object representing a vanilla item.
   *
   * @param material the {@link Material} type
   * @param amount   the required quantity
   */
  public CraftingObject(Material material, int amount) {
    this.manager = null;
    this.vanillaItem = new ItemStack(material, amount);
    this.amount = amount;
    this.itemId = "vanilla:" + material.name();
  }

  /**
   * Returns the {@link ItemManager} if this is a custom item.
   *
   * @return the item manager or {@code null} if this is a vanilla item
   */
  public ItemManager getManager() {
    return manager;
  }

  /**
   * Returns a clone of the vanilla item stack.
   *
   * @return a cloned {@link ItemStack} or {@code null} if this is a custom item
   */
  public ItemStack getVanillaItem() {
    return vanillaItem == null ? null : vanillaItem.clone();
  }

  /**
   * Returns the quantity of this crafting object.
   *
   * @return the required amount
   */
  public int getAmount() {
    return amount;
  }

  /**
   * Checks whether this crafting object represents a custom item.
   *
   * @return {@code true} if custom, {@code false} otherwise
   */
  public boolean isCustom() {
    return manager != null;
  }


  /**
   * Checks whether this crafting object represents a vanilla item.
   *
   * @return {@code true} if vanilla, {@code false} otherwise
   */
  public boolean isVanilla() {
    return vanillaItem != null;
  }

  /**
   * Returns the unique identifier of this crafting object.
   * Custom items use their manager's ID; vanilla items use the format {@code vanilla:MATERIAL_NAME}.
   *
   * @return the item identifier
   */
  public String getId() {
    return itemId;
  }
}
