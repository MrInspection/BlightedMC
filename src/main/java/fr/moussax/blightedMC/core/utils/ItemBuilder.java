package fr.moussax.blightedMC.core.utils;

import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.index.qual.Positive;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A utility class for building {@link ItemStack} instances with a fluent interface.
 * <p>
 * This class simplifies the creation and customization of items by providing methods
 * to set various properties such as material, amount, display name, lore, enchantments,
 * item flags, and more.
 * </p>
 */
public class ItemBuilder {
  private ItemStack item;
  private ItemMeta itemMeta;
  private Material material = Material.STONE;

  private String displayName;
  private int amount = 1;
  private int damage = 0;

  private Map<Enchantment, Integer> enchantments = new HashMap<>();
  private List<String> itemLores = new ArrayList<>();
  private List<ItemFlag> itemFlags = new ArrayList<>();
  private List<Pattern> bannerPatterns;


  public ItemBuilder(Material material) {
    if (material == null) {
      material = Material.AIR;
    }
    this.item = new ItemStack(material);
    this.material = material;
  }

  public ItemBuilder(Material material, @Positive int amount) {
    if (material == null) {
      material = Material.AIR;
    }
    if (amount > material.getMaxStackSize()) {
      throw new IllegalArgumentException("Amount must be between 1 and " + material.getMaxStackSize());
    }
    this.amount = amount;
    this.item = new ItemStack(material, amount);
    this.material = material;
  }

  public ItemBuilder(Material material, @Positive int amount, @Nonnull String displayName) {
    if (material == null) {
      material = Material.AIR;
    }
    if (amount > material.getMaxStackSize()) {
      throw new IllegalArgumentException("Amount must be between 1 and " + material.getMaxStackSize());
    }
    this.material = material;
    this.displayName = displayName;
    this.amount = amount;
    this.item = new ItemStack(material, amount);
  }

  public ItemBuilder(ItemStack itemStack) {
    this.item = itemStack;
    this.material = itemStack.getType();
    this.amount = itemStack.getAmount();

    if (itemStack.hasItemMeta()) {
      this.itemMeta = itemStack.getItemMeta();
      if (itemMeta != null) {
        this.displayName = itemMeta.getDisplayName();

        if (itemMeta instanceof Damageable) {
          this.damage = ((Damageable) itemMeta).getDamage();
        }

        this.itemLores = itemMeta.getLore() != null ? new ArrayList<>(itemMeta.getLore()) : new ArrayList<>();
        this.itemFlags = new ArrayList<>(itemMeta.getItemFlags());
      }
    }

    this.enchantments = new HashMap<>(itemStack.getEnchantments());
  }

  public ItemBuilder setDisplayName(@Nonnull String displayName) {
    this.displayName = displayName;
    return this;
  }

  public ItemBuilder setMaterial(Material material) {
    this.material = material;
    return this;
  }

  public ItemBuilder setAmount(@Positive int amount) {
    if (amount > material.getMaxStackSize()) {
      throw new IllegalArgumentException("Amount must be between 1 and " + material.getMaxStackSize());
    }
    this.amount = amount;
    return this;
  }

  public ItemBuilder setItemMeta(ItemMeta itemMeta) {
    this.itemMeta = itemMeta;
    return this;
  }

  public ItemBuilder setDurability(int damage) {
    this.damage = damage;
    return this;
  }

  public ItemBuilder addLore(String line) {
    itemLores.add(line);
    return this;
  }

  public ItemBuilder addLore(List<String> lines) {
    itemLores.addAll(lines);
    return this;
  }

  public ItemBuilder addLore(String... lines) {
    for (String line : lines) {
      addLore(line);
    }
    return this;
  }

  public ItemBuilder addLore(String line, int index) {
    itemLores.set(index, line);
    return this;
  }

  public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
    enchantments.put(enchantment, level);
    return this;
  }

  public ItemBuilder addEnchantment(Map<Enchantment, Integer> enchantments) {
    this.enchantments.putAll(enchantments);
    return this;
  }

  /**
   * Adds an enchantment glint to the item to look enchanted, typically used for visual effects.
   */
  public ItemBuilder addEnchantmentGlint(boolean add) {
    if (add) {
      addEnchantment(Enchantment.UNBREAKING, 1);
      addItemFlag(ItemFlag.HIDE_ENCHANTS);
    } else {
      enchantments.remove(Enchantment.UNBREAKING);
      itemFlags.remove(ItemFlag.HIDE_ENCHANTS);
    }
    return this;
  }

  public ItemBuilder addEnchantmentGlint() {
    return addEnchantmentGlint(true);
  }

  public ItemBuilder removeEnchantment(Enchantment enchantment) {
    enchantments.remove(enchantment);
    return this;
  }

  public ItemBuilder addItemFlag(ItemFlag flag) {
    itemFlags.add(flag);
    return this;
  }

  public ItemBuilder addItemFlag(List<ItemFlag> flags) {
    itemFlags.addAll(flags);
    return this;
  }

  public ItemBuilder setBannerPatterns(List<Pattern> patterns) {
    this.bannerPatterns = patterns;
    return this;
  }

  public ItemBuilder setUnbreakable(boolean unbreakable) {
    itemMeta.setUnbreakable(unbreakable);
    return this;
  }

  public ItemStack getItem() {
    return item;
  }

  public ItemMeta getItemMeta() {
    return itemMeta;
  }

  public Material getMaterial() {
    return material;
  }

  public String getDisplayName() {
    return displayName;
  }

  public int getAmount() {
    return amount;
  }

  public int getDamage() {
    return damage;
  }

  public Map<Enchantment, Integer> getEnchantments() {
    return enchantments;
  }

  public List<String> getItemLores() {
    return itemLores;
  }

  public List<ItemFlag> getItemFlags() {
    return itemFlags;
  }

  public List<Pattern> getBannerPatterns() {
    return bannerPatterns;
  }

  public ItemStack toItemStack() {
    item.setType(material);
    item.setAmount(amount);
    ItemMeta meta = item.getItemMeta();
    assert meta != null;

    if (meta instanceof Damageable) {
      ((Damageable) meta).setDamage(damage);
    }

    if (displayName != null) {
      meta.setDisplayName(displayName);
    }

    if (!itemLores.isEmpty()) {
      meta.setLore(itemLores);
    }

    if (!enchantments.isEmpty()) {
      for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
        meta.addEnchant(entry.getKey(), entry.getValue(), true);
      }
    }

    if (!itemFlags.isEmpty()) {
      for (ItemFlag flag : itemFlags) {
        meta.addItemFlags(flag);
      }
    }

    if (bannerPatterns != null && item.getItemMeta() instanceof BannerMeta) {
      BannerMeta bm = (BannerMeta) item.getItemMeta();
      bm.setPatterns(bannerPatterns);
      bm.addItemFlags(ItemFlag.HIDE_BANNER_PATTERNS);
      item.setItemMeta(bm);
    }

    item.setItemMeta(meta);
    return item;
  }
}
