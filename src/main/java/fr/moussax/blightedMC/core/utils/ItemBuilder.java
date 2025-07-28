package fr.moussax.blightedMC.core.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
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
  private Material material;

  private String displayName;
  private int amount = 1;
  private int damage = 0;

  private Map<Enchantment, Integer> enchantments = new HashMap<>();
  private List<String> itemLores = new ArrayList<>();
  private List<ItemFlag> itemFlags = new ArrayList<>();
  private List<Pattern> bannerPatterns;

  private UUID skullOwnerUUID;
  private String base64Texture;
  private Color leatherColor;
  private ArmorTrim armorTrim;

  // Constructors ------------------------------------------------------------------------

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
    this.item = new ItemStack(material);
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

  // Setters ------------------------------------------------------------------------

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

  // Getters ------------------------------------------------------------------------

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

  // Other methods ------------------------------------------------------------------------

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

  public ItemBuilder addBannerPatterns(List<Pattern> patterns) {
    this.bannerPatterns = patterns;
    return this;
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
      bannerMeta.addItemFlags(ItemFlag.HIDE_BANNER_PATTERNS);
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

    item.setItemMeta(meta);
    return item;
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
