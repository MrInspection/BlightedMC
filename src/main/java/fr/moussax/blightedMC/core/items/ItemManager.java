package fr.moussax.blightedMC.core.items;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.items.abilities.Ability;
import fr.moussax.blightedMC.core.items.abilities.AbilityLore;
import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.index.qual.Positive;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ItemManager extends ItemBuilder {
  private final String itemId;
  private final ItemRarity itemRarity;
  private final ItemType itemType;

  private final List<Ability> abilities = new ArrayList<>();

  public ItemManager(@Nonnull String itemId, @Nonnull ItemType type, @Nonnull ItemRarity rarity, @Nonnull Material material) {
    super(material);
    this.itemId = itemId;
    this.itemType = type;
    this.itemRarity = rarity;
  }

  public ItemManager(@Nonnull String itemId, @Nonnull ItemType type, @Nonnull ItemRarity rarity, @Nonnull Material material, @Nonnull String displayName) {
    super(material, rarity.getColorPrefix() + displayName);
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
    super(material, amount, rarity.getColorPrefix() + displayName);
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

  public ItemManager addAbility(Ability ability) {
    abilities.add(ability);
    return this;
  }

  public List<Ability> getAbilities() {
    return abilities;
  }

  @Override
  public ItemManager setDisplayName(@Nonnull String displayName) {
    String coloredName = itemRarity.getColorPrefix() + displayName;
    super.setDisplayName(coloredName);
    return this;
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

    meta.getPersistentDataContainer().set(
        new NamespacedKey(BlightedMC.getInstance(), "rarity"),
        PersistentDataType.STRING,
        itemRarity.name()
    );

    List<String> combinedLore = new ArrayList<>(getItemLores());
    for (Ability ability : abilities) {
      AbilityLore lore = ability.getAbilityLore();
      if (lore != null) {
        combinedLore.addAll(lore.makeLore(null, item));
      }
    }

    if (!combinedLore.isEmpty()) {
      meta.setLore(combinedLore);
    }

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
