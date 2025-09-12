package fr.moussax.blightedMC.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.checkerframework.checker.index.qual.Positive;

import javax.annotation.Nonnull;
import java.net.URI;
import java.nio.charset.StandardCharsets;
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
 *      .setDisplayName("§bEpic Sword")
 *      .addEnchantment(Enchantment.SHARPNESS, 7)
 *      .addLore("§7A legendary blade")
 *      .setUnbreakable(true)
 *      .toItemStack();
 * }</pre>
 *
 * <p>All setters return {@code this} for chaining. Input validation is enforced
 * (e.g., stack size, lore indices, color formats, Base64 textures) with exceptions
 * thrown on invalid values.</p>
 */
public class ItemBuilder {
  public static final NamespacedKey ATTRIBUTE_KEY =
    new NamespacedKey(BlightedMC.getInstance(), "attribute");

  private final ItemStack item;
  private ItemMeta itemMeta;
  private Material material;
  private String displayName;
  private int amount = 1;
  private int damage = 0;

  private Map<Enchantment, Integer> enchantments = new HashMap<>();
  private Map<Attribute, Collection<AttributeModifier>> attributes = new HashMap<>();
  private List<ItemFlag> itemFlags = new ArrayList<>();
  private List<String> itemLores = new ArrayList<>();
  private List<Pattern> bannerPatterns;

  private UUID skullOwnerUUID;
  private String base64Texture;
  private Color leatherColor;
  private ArmorTrim armorTrim;

  public ItemBuilder(@Nonnull Material material) {
    this.material = material;
    this.item = new ItemStack(material);
    this.itemMeta = item.getItemMeta();
  }

  public ItemBuilder(@Nonnull Material material, @Nonnull String displayName) {
    this.material = material;
    this.displayName = displayName;
    this.item = new ItemStack(material);
    this.itemMeta = item.getItemMeta();
  }

  public ItemBuilder(@Nonnull Material material, @Positive int amount) {
    this.material = material;
    if (amount > material.getMaxStackSize()) {
      throw new IllegalArgumentException("Amount must be between 1 and " + material.getMaxStackSize());
    }
    this.amount = amount;
    this.item = new ItemStack(this.material, amount);
    this.itemMeta = item.getItemMeta();
  }

  public ItemBuilder(Material material, @Positive int amount, @Nonnull String displayName) {
    this.material = material;
    this.amount = amount;
    this.item = new ItemStack(material, amount);
    this.itemMeta = item.getItemMeta();
    this.displayName = displayName;
  }

  public ItemBuilder(ItemStack itemStack) {
    this.item = itemStack;
    this.material = itemStack.getType();
    this.amount = itemStack.getAmount();
    this.itemMeta = itemStack.getItemMeta();

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

  public ItemBuilder setDurabilityDamage(int damage) {
    this.damage = damage;
    return this;
  }

  public ItemBuilder setUnbreakable(boolean unbreakable) {
    if (itemMeta == null) itemMeta = item.getItemMeta();
    assert itemMeta != null;
    itemMeta.setUnbreakable(unbreakable);
    item.setItemMeta(itemMeta);
    return this;
  }

  public ItemBuilder setSkullOwner(@Nonnull UUID playerId) {
    this.material = Material.PLAYER_HEAD;
    this.skullOwnerUUID = playerId;
    this.base64Texture = null;
    return this;
  }

  public ItemBuilder setCustomSkullTexture(@Nonnull String base64Texture) {
    this.material = Material.PLAYER_HEAD;
    this.base64Texture = base64Texture;
    this.skullOwnerUUID = null;
    return this;
  }

  public ItemBuilder setLeatherColor(@Nonnull String hex) {
    if (!hex.matches("^#?[0-9a-fA-F]{6}$")) {
      throw new IllegalArgumentException("Invalid hex color: " + hex);
    }
    String cleaned = hex.startsWith("#") ? hex.substring(1) : hex;
    int r = Integer.parseInt(cleaned.substring(0, 2), 16);
    int g = Integer.parseInt(cleaned.substring(2, 4), 16);
    int b = Integer.parseInt(cleaned.substring(4, 6), 16);
    this.leatherColor = org.bukkit.Color.fromRGB(r, g, b);
    return this;
  }

  public ItemBuilder setArmorTrim(@Nonnull TrimMaterial material, @Nonnull TrimPattern pattern) {
    this.armorTrim = new ArmorTrim(material, pattern);
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
    if (index < 0 || index > itemLores.size()) {
      throw new IndexOutOfBoundsException("Index out of bounds for lore list: " + index);
    }
    itemLores.add(index, line);
    return this;
  }

  public void setLore(String line, int index) {
    if (index < 0 || index >= itemLores.size()) {
      throw new IndexOutOfBoundsException("Index out of bounds for lore list: " + index);
    }
    itemLores.set(index, line);
  }

  public void addEnchantment(Enchantment enchantment, int level) {
    enchantments.put(enchantment, level);
  }

  public ItemBuilder addEnchantment(Map<Enchantment, Integer> enchantments) {
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
      itemFlags.remove(ItemFlag.HIDE_ENCHANTS);
    }
    return this;
  }

  public ItemBuilder addEnchantmentGlint() {
    return addEnchantmentGlint(true);
  }

  public ItemBuilder addItemFlag(ItemFlag flag) {
    itemFlags.add(flag);
    return this;
  }

  public void addItemFlag(List<ItemFlag> flags) {
    itemFlags.addAll(flags);
  }

  public ItemBuilder addBannerPatterns(List<Pattern> patterns) {
    this.bannerPatterns = patterns;
    return this;
  }

  public ItemBuilder addAttributeModifier(@Nonnull Attribute attribute, @Nonnull AttributeModifier modifier) {
    attributes.computeIfAbsent(attribute, k -> new ArrayList<>()).add(modifier);
    return this;
  }

  public ItemBuilder addAttributeModifier(@Nonnull Attribute attribute, double amount, @Nonnull AttributeModifier.Operation operation, @Nonnull EquipmentSlotGroup slotGroup) {
    AttributeModifier modifier = new AttributeModifier(ATTRIBUTE_KEY, amount, operation, slotGroup);
    return addAttributeModifier(attribute, modifier);
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

    if (bannerPatterns != null && meta instanceof BannerMeta bannerMeta) {
      bannerMeta.setPatterns(bannerPatterns);
      item.setItemMeta(bannerMeta);
    }

    if(isLeatherDyeable(material) && meta instanceof LeatherArmorMeta lam && leatherColor != null) {
      lam.setColor(leatherColor);
      item.setItemMeta(lam);
    }

    if (armorTrim != null && meta instanceof ArmorMeta armorMeta) {
      armorMeta.setTrim(armorTrim);
      item.setItemMeta(armorMeta);
    }

    if(material == Material.PLAYER_HEAD && meta instanceof SkullMeta skullMeta) {
      if(skullOwnerUUID != null) {
        applySkullOwner(skullMeta);
      } else if(base64Texture != null) {
        applySkullTexture(skullMeta);
      }
    }

    // Apply stored attribute modifiers, if any
    if (!attributes.isEmpty()) {
      for (Map.Entry<Attribute, Collection<AttributeModifier>> entry : attributes.entrySet()) {
        for (AttributeModifier modifier : entry.getValue()) {
          meta.addAttributeModifier(entry.getKey(), modifier);
        }
      }
    }

    item.setItemMeta(meta);
    return item;
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

  private static boolean isLeatherDyeable(Material material){
    return switch(material) {
      case LEATHER_HELMET, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, LEATHER_BOOTS, LEATHER_HORSE_ARMOR -> true;
      default -> false;
    };
  }

  private void applySkullOwner(SkullMeta meta){
    OfflinePlayer target = Bukkit.getOfflinePlayer(skullOwnerUUID);
    meta.setOwningPlayer(target);
  }

  private void applySkullTexture(SkullMeta meta){
    String json = new String(Base64.getDecoder().decode(base64Texture), StandardCharsets.UTF_8);
    JsonObject skinObj = JsonParser.parseString(json)
        .getAsJsonObject().getAsJsonObject("textures").getAsJsonObject("SKIN");

    String url = skinObj.get("url").getAsString();

    UUID id = UUID.randomUUID();
    PlayerProfile profile = Bukkit.createPlayerProfile(id, id.toString().substring(0, 16));
    PlayerTextures textures = profile.getTextures();

    try {
      textures.setSkin(new URI(url).toURL());
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid texture URL: " + url, e);
    }
    profile.setTextures(textures);

    meta.setOwnerProfile(profile);
  }
}
