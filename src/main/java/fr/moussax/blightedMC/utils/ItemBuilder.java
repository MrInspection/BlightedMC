package fr.moussax.blightedMC.utils;

import fr.moussax.blightedMC.BlightedMC;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.checkerframework.checker.index.qual.Positive;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * A fluent builder for {@link ItemStack}, providing an easier way to create
 * and customize items with material, amount, display name, lore, enchantments,
 * flags, durability, unbreakable state, banner patterns, armor colors/trims,
 * player heads, custom head textures, and more.
 *
 * <p>Example:</p>
 * <pre>{@code
 * ItemStack customSword = new ItemBuilder(Material.DIAMOND_SWORD)
 *      .setDisplayName("§dSword of El Dorado")
 *      .addEnchantment(Enchantment.SHARPNESS, 7)
 *      .addLore("§7A legendary blade")
 *      .setUnbreakable(true)
 *      .toItemStack();
 * }</pre>
 */
public class ItemBuilder {
  public static final NamespacedKey ATTRIBUTE_KEY =
    new NamespacedKey(BlightedMC.getInstance(), "attribute");

  private final ItemStack item;
  private ItemMeta itemMeta;

  private Map<Enchantment, Integer> enchantments = new HashMap<>();
  private Map<Attribute, Collection<AttributeModifier>> attributes = new HashMap<>();
  private List<Pattern> bannerPatterns;

  private UUID skullOwnerUUID;
  private String base64Texture;
  private Color leatherColor;
  private ArmorTrim armorTrim;

  public ItemBuilder(@Nonnull Material material) {
    this(new ItemStack(material));
  }

  public ItemBuilder(@Nonnull Material material, @Positive int amount) {
    this(new ItemStack(material, validateAmount(material, amount)));
  }

  public ItemBuilder(@Nonnull Material material, @Nonnull String displayName) {
    this(new ItemStack(material));
    this.itemMeta.setDisplayName(displayName);
  }

  public ItemBuilder(Material material, @Positive int amount, @Nonnull String displayName) {
    this(new ItemStack(material, validateAmount(material, amount)));
    this.itemMeta.setDisplayName(displayName);
  }

  public ItemBuilder(ItemStack itemStack) {
    this.item = itemStack.clone();
    ItemMeta meta = this.item.getItemMeta();

    if (meta == null) {
      throw new IllegalStateException("ItemMeta cannot be null for material");
    }

    this.itemMeta = meta;
    this.enchantments.putAll(itemStack.getEnchantments());
  }

  private static int validateAmount(Material material, int amount) {
    if (amount < 1 || amount > material.getMaxStackSize()) {
      throw new IllegalArgumentException("Amount must be between 1 and " + material.getMaxStackSize());
    }
    return amount;
  }

  public ItemBuilder setDisplayName(@Nonnull String displayName) {
    this.itemMeta.setDisplayName(displayName);
    return this;
  }

  public ItemBuilder setAmount(@Positive int amount) {
    item.setAmount(validateAmount(item.getType(), amount));
    return this;
  }

  public ItemBuilder setItemMeta(ItemMeta itemMeta) {
    this.itemMeta = itemMeta;
    return this;
  }

  public ItemBuilder setDurabilityDamage(int damage) {
    if (itemMeta instanceof Damageable damageable) {
      damageable.setDamage(damage);
    }
    return this;
  }

  public ItemBuilder setUnbreakable(boolean unbreakable) {
    itemMeta.setUnbreakable(unbreakable);
    return this;
  }

  public ItemBuilder setSkullOwner(@Nonnull UUID playerId) {
    item.setType(Material.PLAYER_HEAD);
    this.skullOwnerUUID = playerId;
    this.base64Texture = null;
    return this;
  }

  public ItemBuilder setCustomSkullTexture(@Nonnull String base64Texture) {
    item.setType(Material.PLAYER_HEAD);
    this.base64Texture = base64Texture;
    this.skullOwnerUUID = null;
    return this;
  }

  public ItemBuilder setLeatherColor(@Nonnull String hex) {
    this.leatherColor = ColorUtils.fromHex(hex);
    return this;
  }

  public ItemBuilder setArmorTrim(@Nonnull TrimMaterial material, @Nonnull TrimPattern pattern) {
    this.armorTrim = new ArmorTrim(material, pattern);
    return this;
  }

  public ItemBuilder addLore(String line) {
    List<String> lore = itemMeta.getLore() != null ? new ArrayList<>(itemMeta.getLore()) : new ArrayList<>();
    lore.add(line);
    itemMeta.setLore(lore);
    return this;
  }

  public ItemBuilder addLore(List<String> lines) {
    List<String> lore = itemMeta.getLore() != null ? new ArrayList<>(itemMeta.getLore()) : new ArrayList<>();
    lore.addAll(lines);
    itemMeta.setLore(lore);
    return this;
  }

  public ItemBuilder addLore(String... lines) {
    return addLore(Arrays.asList(lines));
  }

  @SuppressWarnings("UnusedReturnValue")
  public ItemBuilder setLore(String line, int index) {
    List<String> lore = itemMeta.getLore();
    if (lore == null || index < 0 || index >= lore.size()) {
      throw new IndexOutOfBoundsException("Invalid lore index: " + index);
    }
    lore.set(index, line);
    itemMeta.setLore(lore);
    return this;
  }

  @SuppressWarnings("UnusedReturnValue")
  public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
    enchantments.put(enchantment, level);
    return this;
  }

  public ItemBuilder addEnchantments(Map<Enchantment, Integer> enchantments) {
    this.enchantments.putAll(enchantments);
    return this;
  }

  public ItemBuilder removeEnchantment(Enchantment enchantment) {
    enchantments.remove(enchantment);
    return this;
  }

  public ItemBuilder addEnchantmentGlint(boolean add) {
    if (add) {
      addEnchantment(Enchantment.UNBREAKING, 1);
      addItemFlag(ItemFlag.HIDE_ENCHANTS);
    } else {
      enchantments.remove(Enchantment.UNBREAKING);
      itemMeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
    }
    return this;
  }

  public ItemBuilder addEnchantmentGlint() {
    return addEnchantmentGlint(true);
  }

  public ItemBuilder addItemFlag(ItemFlag flag) {
    itemMeta.addItemFlags(flag);
    return this;
  }

  @SuppressWarnings("UnusedReturnValue")
  public ItemBuilder addItemFlag(List<ItemFlag> flags) {
    itemMeta.addItemFlags(flags.toArray(new ItemFlag[0]));
    return this;
  }

  public ItemBuilder addBannerPatterns(List<Pattern> patterns) {
    this.bannerPatterns = patterns;
    return this;
  }

  public ItemBuilder addAttributeModifier(@Nonnull Attribute attribute, @Nonnull AttributeModifier modifier) {
    attributes.computeIfAbsent(attribute, k -> new ArrayList<>()).add(modifier);
    return this;
  }

  @SuppressWarnings("UnstableApiUsage")
  public ItemBuilder addAttributeModifier(@Nonnull Attribute attribute,
                                          double amount, @Nonnull AttributeModifier.Operation operation,
                                          @Nonnull EquipmentSlotGroup slotGroup) {
    AttributeModifier modifier = new AttributeModifier(ATTRIBUTE_KEY, amount, operation, slotGroup);
    return addAttributeModifier(attribute, modifier);
  }

  public ItemStack toItemStack() {
    if (!enchantments.isEmpty()) {
      enchantments.forEach((enchantment, level) -> itemMeta.addEnchant(enchantment, level, true));
    }

    if (!attributes.isEmpty()) {
      attributes.forEach((attr, mods) ->
        mods.forEach(mod -> itemMeta.addAttributeModifier(attr, mod)));
    }

    if (bannerPatterns != null && itemMeta instanceof BannerMeta bannerMeta) {
      bannerMeta.setPatterns(bannerPatterns);
      item.setItemMeta(bannerMeta);
    }

    if (isLeatherDyeable(item.getType()) && itemMeta instanceof LeatherArmorMeta lam && leatherColor != null) {
      lam.setColor(leatherColor);
    }

    if (armorTrim != null && itemMeta instanceof ArmorMeta armorMeta) {
      armorMeta.setTrim(armorTrim);
    }

    if (item.getType() == Material.PLAYER_HEAD && itemMeta instanceof SkullMeta skullMeta) {
      if (skullOwnerUUID != null) {
        SkullUtils.applyOwner(skullMeta, skullOwnerUUID);
      } else if (base64Texture != null) {
        SkullUtils.applyTexture(skullMeta, base64Texture);
      }
    }

    item.setItemMeta(itemMeta);
    return item;
  }

  public ItemStack getItem() {
    return item.clone();
  }

  public ItemMeta getItemMeta() {
    return itemMeta.clone();
  }

  public String getDisplayName() {
    return itemMeta.getDisplayName();
  }

  public Map<Enchantment, Integer> getEnchantments() {
    return Collections.unmodifiableMap(enchantments);
  }

  public List<Pattern> getBannerPatterns() {
    return Collections.unmodifiableList(bannerPatterns);
  }

  private static boolean isLeatherDyeable(Material material) {
    return switch (material) {
      case LEATHER_HELMET, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, LEATHER_BOOTS, LEATHER_HORSE_ARMOR -> true;
      default -> false;
    };
  }
}
