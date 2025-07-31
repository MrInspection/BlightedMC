package fr.moussax.blightedMC.core.items.crafting;

import fr.moussax.blightedMC.core.items.ItemManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CraftingObject {
  private final ItemManager manager;
  private final ItemStack vanillaItem;
  private final int amount;
  private final String itemId;

  public CraftingObject(ItemManager manager, int amount) {
    this.manager = manager;
    this.vanillaItem = null;
    this.amount = amount;
    this.itemId = manager != null ? manager.getItemId() : "";
  }

  public CraftingObject(Material material, int amount) {
    this.manager = null;
    this.vanillaItem = new ItemStack(material, amount);
    this.amount = amount;
    this.itemId = "vanilla:" + material.name();
  }

  public ItemManager getManager() {
    return manager;
  }

  public ItemStack getVanillaItem() {
    return vanillaItem == null ? null : vanillaItem.clone();
  }

  public int getAmount() {
    return amount;
  }

  public boolean isCustom() {
    return manager != null;
  }

  public boolean isVanilla() {
    return vanillaItem != null;
  }

  public String getId() {
    return itemId;
  }
}
