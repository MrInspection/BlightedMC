package fr.moussax.blightedMC.core.items;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.index.qual.Positive;

import javax.annotation.Nonnull;

public class ItemManager extends ItemBuilder {
  private final String itemId;
  private final ItemRarity itemRarity;
  private final ItemType itemType;

  public ItemManager(@Nonnull String itemId, @Nonnull ItemType type, @Nonnull ItemRarity rarity, @Nonnull Material material) {
    super(material);
    this.itemId = itemId;
    this.itemType = type;
    this.itemRarity = rarity;
  }

  public ItemManager(@Nonnull String itemId, @Nonnull ItemType type, @Nonnull ItemRarity rarity, @Nonnull Material material, @Nonnull String displayName) {
    super(material, displayName);
    this.itemId = itemId;
    this.itemType = type;
    this.itemRarity = rarity;
  }

  public ItemManager(@Nonnull String itemId, @Nonnull ItemType type, @Nonnull ItemRarity rarity, @Nonnull Material material, @Positive int amount) {
    super(material, amount);
    this.itemId = itemId;
    this.itemType = type;
    this.itemRarity = rarity;
  }

  public ItemManager(@Nonnull String itemId, @Nonnull ItemType type, @Nonnull ItemRarity rarity, @Nonnull Material material, @Positive int amount, @Nonnull String displayName) {
    super(material, amount, displayName);
    this.itemId = itemId;
    this.itemType = type;
    this.itemRarity = rarity;
  }

  public ItemManager(@Nonnull String itemId, @Nonnull ItemType type, @Nonnull ItemRarity rarity, ItemStack itemStack) {
    super(itemStack);
    this.itemId = itemId;
    this.itemType = type;
    this.itemRarity = rarity;
  }

  @Override
  public ItemStack toItemStack() {
    ItemStack item = super.toItemStack();

    var meta = item.getItemMeta();
    assert meta != null;
    meta.getPersistentDataContainer().set(
        new NamespacedKey(BlightedMC.getInstance(), "id"),
        PersistentDataType.STRING,
        itemId
    );

    item.setItemMeta(meta);
    return item;
  }

  public String getItemId() {
    return itemId;
  }

  public ItemRarity getItemRarity() {
    return itemRarity;
  }

  public ItemType getItemType() {
    return itemType;
  }
}
