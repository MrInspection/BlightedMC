package fr.moussax.blightedMC.utils;

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
 * A utility class for constructing and customizing {@link ItemStack} instances
 * in a fluent, chainable manner.
 *
 * <p>This builder simplifies the creation of items with various properties, including:</p>
 * <ul>
 *   <li>Material and stack amount</li>
 *   <li>Display name and lore</li>
 *   <li>Enchantments and item flags</li>
 *   <li>Durability damage and unbreakable state</li>
 *   <li>Banner patterns, leather armor colors, and armor trims</li>
 *   <li>Player head owners or custom Base64 textures</li>
 * </ul>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * ItemStack customSword = new ItemBuilder(Material.DIAMOND_SWORD)
 *      .setDisplayName("§bEpic Sword")
 *      .addEnchantment(Enchantment.SHARPNESS, 5)
 *      .addLore("§7A legendary blade")
 *      .setUnbreakable(true)
 *      .toItemStack();
 * }</pre>
 *
 * <p>All setter methods return the same {@code ItemBuilder} instance, allowing
 * for method chaining.</p>
 *
 * <p><strong>Note:</strong> Validation is performed for stack size, lore indices,
 * color formats, and Base64 skull textures. Exceptions are thrown for invalid inputs.</p>
 */
public class ItemBuilder {

  private final ItemStack item;
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


  /**
   * Creates a new ItemBuilder with the given material.
   *
   * @param material the material of the item (cannot be null)
   */
  public ItemBuilder(@Nonnull Material material) {
    this.material = material;
    this.item = new ItemStack(material);
    this.itemMeta = item.getItemMeta();
  }

  /**
   * Creates a new ItemBuilder with a material and display name.
   *
   * @param material    the material of the item
   * @param displayName the display name for the item
   */
  public ItemBuilder(@Nonnull Material material, @Nonnull String displayName) {
    this.material = material;
    this.displayName = displayName;
    this.item = new ItemStack(material);
    this.itemMeta = item.getItemMeta();
  }

  /**
   * Creates a new ItemBuilder with a material and stack amount.
   *
   * @param material the material of the item
   * @param amount   the stack amount (1 to material max stack size)
   * @throws IllegalArgumentException if the amount exceeds the max stack size
   */
  public ItemBuilder(@Nonnull Material material, @Positive int amount) {
    this.material = material;
    if (amount > material.getMaxStackSize()) {
      throw new IllegalArgumentException("Amount must be between 1 and " + material.getMaxStackSize());
    }
    this.amount = amount;
    this.item = new ItemStack(this.material, amount);
    this.itemMeta = item.getItemMeta();
  }

  /**
   * Creates a new ItemBuilder with material, amount, and display name.
   *
   * @param material    the material of the item
   * @param amount      the stack amount
   * @param displayName the display name
   */
  public ItemBuilder(Material material, @Positive int amount, @Nonnull String displayName) {
    this.material = material;
    this.amount = amount;
    this.item = new ItemStack(material, amount);
    this.itemMeta = item.getItemMeta();
    this.displayName = displayName;
  }

  /**
   * Creates a new ItemBuilder from an existing {@link ItemStack}.
   * Copies metadata, enchantments, lore, flags, and damage.
   *
   * @param itemStack the source item stack
   */
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

  /**
   * Sets the display name of the item.
   *
   * @param displayName the new display name
   * @return this builder
   */
  public ItemBuilder setDisplayName(@Nonnull String displayName) {
    this.displayName = displayName;
    return this;
  }

  /**
   * Sets the material of the item.
   *
   * @param material the new material
   * @return this builder
   */
  public ItemBuilder setMaterial(Material material) {
    this.material = material;
    return this;
  }

  /**
   * Sets the stack amount.
   *
   * @param amount new stack amount
   * @return this builder
   * @throws IllegalArgumentException if amount exceeds the max stack size
   */
  public ItemBuilder setAmount(@Positive int amount) {
    if (amount > material.getMaxStackSize()) {
      throw new IllegalArgumentException("Amount must be between 1 and " + material.getMaxStackSize());
    }
    this.amount = amount;
    return this;
  }

  /**
   * Sets the item meta directly.
   *
   * @param itemMeta the meta to set
   * @return this builder
   */
  public ItemBuilder setItemMeta(ItemMeta itemMeta) {
    this.itemMeta = itemMeta;
    return this;
  }

  /**
   * Sets durability damage for damageable items.
   *
   * @param damage the damage value
   * @return this builder
   */
  public ItemBuilder setDurabilityDamage(int damage) {
    this.damage = damage;
    return this;
  }

  /**
   * Marks the item as unbreakable or not.
   *
   * @param unbreakable true to make unbreakable
   * @return this builder
   */
  public ItemBuilder setUnbreakable(boolean unbreakable) {
    if (itemMeta == null) itemMeta = item.getItemMeta();
    assert itemMeta != null;
    itemMeta.setUnbreakable(unbreakable);
    item.setItemMeta(itemMeta);
    return this;
  }

  /**
   * Makes this item a player head belonging to the given UUID.
   *
   * @param playerId the UUID of the player
   * @return this builder
   */
  public ItemBuilder setSkullOwner(@Nonnull UUID playerId) {
    this.material = Material.PLAYER_HEAD;
    this.skullOwnerUUID = playerId;
    this.base64Texture = null;
    return this;
  }

  /**
   * Sets a custom Base64 skull texture for a player head.
   *
   * @param base64Texture Base64-encoded texture data
   * @return this builder
   */
  public ItemBuilder setCustomSkullTexture(@Nonnull String base64Texture) {
    this.material = Material.PLAYER_HEAD;
    this.base64Texture = base64Texture;
    this.skullOwnerUUID = null;
    return this;
  }

  /**
   * Sets the leather armor color using a hex string (#RRGGBB).
   *
   * @param hex a valid 6-digit hex color, with or without #
   * @return this builder
   * @throws IllegalArgumentException if the color format is invalid
   */
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

  /**
   * Sets armor trim for applicable armor pieces.
   *
   * @param material the trim material
   * @param pattern  the trim pattern
   * @return this builder
   */
  public ItemBuilder setArmorTrim(@Nonnull TrimMaterial material, @Nonnull TrimPattern pattern) {
    this.armorTrim = new ArmorTrim(material, pattern);
    return this;
  }

  /**
   * Adds a single line to the lore.
   *
   * @param line the lore line
   * @return this builder
   */
  public ItemBuilder addLore(String line) {
    itemLores.add(line);
    return this;
  }

  /**
   * Adds multiple lines to the lore.
   *
   * @param lines the lore lines
   * @return this builder
   */
  public ItemBuilder addLore(List<String> lines) {
    itemLores.addAll(lines);
    return this;
  }

  /**
   * Adds multiple lines to the lore (varargs).
   *
   * @param lines the lore lines
   * @return this builder
   */
  public ItemBuilder addLore(String... lines) {
    for (String line : lines) {
      addLore(line);
    }
    return this;
  }

  /**
   * Inserts a lore line at a specific index.
   *
   * @param line  the lore line
   * @param index the insertion index
   * @return this builder
   * @throws IndexOutOfBoundsException if the index is invalid
   */
  public ItemBuilder addLore(String line, int index) {
    if (index < 0 || index > itemLores.size()) {
      throw new IndexOutOfBoundsException("Index out of bounds for lore list: " + index);
    }
    itemLores.add(index, line);
    return this;
  }

  /**
   * Replaces a lore line at a specific index.
   *
   * @param line  the new lore line
   * @param index the index to replace
   * @return this builder
   * @throws IndexOutOfBoundsException if the index is invalid
   */
  public ItemBuilder setLore(String line, int index) {
    if (index < 0 || index >= itemLores.size()) {
      throw new IndexOutOfBoundsException("Index out of bounds for lore list: " + index);
    }
    itemLores.set(index, line);
    return this;
  }

  /**
   * Adds or updates an enchantment.
   *
   * @param enchantment the enchantment
   * @param level       the level
   * @return this builder
   */
  public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
    enchantments.put(enchantment, level);
    return this;
  }

  /**
   * Adds multiple enchantments.
   *
   * @param enchantments map of enchantments to levels
   * @return this builder
   */
  public ItemBuilder addEnchantment(Map<Enchantment, Integer> enchantments) {
    this.enchantments.putAll(enchantments);
    return this;
  }

  /**
   * Removes an enchantment.
   *
   * @param enchantment the enchantment to remove
   * @return this builder
   */
  public ItemBuilder removeEnchantment(Enchantment enchantment) {
    enchantments.remove(enchantment);
    return this;
  }

  /**
   * Adds a visual enchantment glint, optionally hiding the enchant.
   *
   * @param add true to add glint, false to remove
   * @return this builder
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

  /**
   * Adds an enchantment glint.
   *
   * @return this builder
   */
  public ItemBuilder addEnchantmentGlint() {
    return addEnchantmentGlint(true);
  }

  /**
   * Adds an item flag.
   *
   * @param flag the flag to add
   * @return this builder
   */
  public ItemBuilder addItemFlag(ItemFlag flag) {
    itemFlags.add(flag);
    return this;
  }

  /**
   * Adds multiple item flags.
   *
   * @param flags the flags to add
   * @return this builder
   */
  public ItemBuilder addItemFlag(List<ItemFlag> flags) {
    itemFlags.addAll(flags);
    return this;
  }

  /**
   * Hides or shows the tooltip.
   *
   * @param hide true to hide, false to show
   * @return this builder
   */
  public ItemBuilder hideTooltip(boolean hide) {
    if (itemMeta == null) {
      itemMeta = item.getItemMeta();
    }
    assert itemMeta != null;

    if (hide) {
      itemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
      if (!itemFlags.contains(ItemFlag.HIDE_ADDITIONAL_TOOLTIP)) {
        itemFlags.add(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
      }
    } else {
      itemMeta.removeItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
      itemFlags.remove(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
    }

    item.setItemMeta(itemMeta);
    return this;
  }

  /**
   * Adds banner patterns to the item if it's a banner.
   *
   * @param patterns list of banner patterns
   * @return this builder
   */
  public ItemBuilder addBannerPatterns(List<Pattern> patterns) {
    this.bannerPatterns = patterns;
    return this;
  }

  /**
   * Builds and returns the final {@link ItemStack} with all properties applied.
   *
   * @return the constructed ItemStack
   */
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
