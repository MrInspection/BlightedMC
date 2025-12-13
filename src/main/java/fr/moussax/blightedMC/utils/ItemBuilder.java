package fr.moussax.blightedMC.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.moussax.blightedMC.BlightedMC;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.components.EquippableComponent;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.inventory.meta.components.JukeboxPlayableComponent;
import org.bukkit.inventory.meta.components.ToolComponent;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.jspecify.annotations.NonNull;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

/**
 * Fluent builder for creating and customizing {@link ItemStack} instances.
 *
 * <p>Supports display name, lore, durability, enchantments, item flags,
 * unbreakable state, glint, leather colors, armor trims, banner patterns,
 * attributes, and player skulls with custom textures.</p>
 *
 * <p>All methods return {@code this} for fluent chaining.</p>
 *
 * <p><b>Example:</b></p>
 * <pre>{@code
 * ItemStack sword = new ItemBuilder(Material.DIAMOND_SWORD)
 *     .setDisplayName("§dTechnoblade's Sword")
 *     .addEnchantment(Enchantment.SHARPNESS, 10)
 *     .addLore("§7Forged by the ancients")
 *     .setUnbreakable(true)
 *     .toItemStack();
 * }</pre>
 */
public class ItemBuilder {
    private final ItemStack item;
    private ItemMeta itemMeta;

    private Map<Enchantment, Integer> enchantments = new HashMap<>();
    private Map<Attribute, Collection<AttributeModifier>> attributes = new HashMap<>();
    private List<Pattern> bannerPatterns;

    private UUID skullOwnerUUID;
    private String base64Texture;
    private Color leatherColor;
    private ArmorTrim armorTrim;

    /**
     * Constructs an ItemBuilder with the specified material.
     *
     * @param material the material type
     * @throws IllegalStateException if the material has no ItemMeta
     */
    public ItemBuilder(@NonNull Material material) {
        this(new ItemStack(material));
    }

    /**
     * Constructs an ItemBuilder with the specified material and amount.
     *
     * @param material the material type
     * @param amount   the stack size
     * @throws IllegalArgumentException if amount is outside valid range
     * @throws IllegalStateException    if the material has no ItemMeta
     */
    public ItemBuilder(Material material, int amount) {
        this(new ItemStack(material, validateAmount(material, amount)));
    }

    /**
     * Constructs an ItemBuilder with the specified material and display name.
     *
     * @param material    the material type
     * @param displayName the display name
     * @throws IllegalStateException if the material has no ItemMeta
     */
    public ItemBuilder(@NonNull Material material, @NonNull String displayName) {
        this(new ItemStack(material));
        this.itemMeta.setDisplayName(displayName);
    }

    /**
     * Constructs an ItemBuilder with the specified material, amount, and display name.
     *
     * @param material    the material type
     * @param amount      the stack size
     * @param displayName the display name
     * @throws IllegalArgumentException if amount is outside valid range
     * @throws IllegalStateException    if the material has no ItemMeta
     */
    public ItemBuilder(@NonNull Material material, int amount, @NonNull String displayName) {
        this(new ItemStack(material, validateAmount(material, amount)));
        this.itemMeta.setDisplayName(displayName);
    }

    /**
     * Constructs an ItemBuilder from an existing ItemStack.
     * Creates a clone of the provided item and preserves existing enchantments.
     *
     * @param itemStack the source item stack
     * @throws IllegalStateException if the item has no ItemMeta
     */
    public ItemBuilder(@NonNull ItemStack itemStack) {
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

    /**
     * Sets the display name of the item.
     *
     * @param displayName the display name supports color codes
     * @return this builder
     */
    public ItemBuilder setDisplayName(@NonNull String displayName) {
        this.itemMeta.setDisplayName(displayName);
        return this;
    }

    /**
     * Sets the stack size of the item.
     * Checks against the custom max stack size component if present,
     * otherwise falls back to the material's default.
     *
     * @param amount the stack size
     * @return this builder
     * @throws IllegalArgumentException if the amount is outside the effective range
     */
    public ItemBuilder setAmount(int amount) {
        int effectiveMax = itemMeta.hasMaxStackSize()
            ? itemMeta.getMaxStackSize()
            : item.getType().getMaxStackSize();

        if (amount < 1 || amount > effectiveMax) {
            throw new IllegalArgumentException("Amount must be between 1 and " + effectiveMax);
        }

        item.setAmount(amount);
        return this;
    }

    /**
     * Sets the maximum stack size for this item.
     *
     * @param size the maximum number of items that can be stacked together
     * @return this builder
     */
    public ItemBuilder setMaxStackSize(int size) {
        itemMeta.setMaxStackSize(size);
        return this;
    }

    /**
     * Replaces the current ItemMeta with the provided one.
     *
     * @param itemMeta the new item meta
     * @return this builder
     */
    public ItemBuilder setItemMeta(ItemMeta itemMeta) {
        this.itemMeta = itemMeta;
        return this;
    }

    /**
     * Sets the durability damage to the item.
     * Only applies if the item is damageable.
     *
     * @param damage the damage value
     * @return this builder
     */
    public ItemBuilder setDurabilityDamage(int damage) {
        if (itemMeta instanceof Damageable damageable) {
            damageable.setDamage(damage);
        }
        return this;
    }

    /**
     * Sets the rarity of this item.
     *
     * @param rarity the {@link ItemRarity} to assign to the item
     * @return this builder
     */
    public ItemBuilder setRarity(ItemRarity rarity) {
        itemMeta.setRarity(rarity);
        return this;
    }

    /**
     * Sets whether the item is unbreakable.
     *
     * @param unbreakable true to make unbreakable
     * @return this builder
     */
    public ItemBuilder setUnbreakable(boolean unbreakable) {
        itemMeta.setUnbreakable(unbreakable);
        return this;
    }

    /**
     * Enables or disables fire resistance for this item.
     *
     * @param fireResistant true to make the item fire-resistant, false otherwise
     * @return this builder
     */
    @SuppressWarnings("deprecation")
    public ItemBuilder setFireResistant(boolean fireResistant) {
        itemMeta.setFireResistant(fireResistant);
        return this;
    }

    /**
     * Adds or removes the glider capability (elytra) from this item.
     *
     * @param glider true to add the glider capability, false to remove it
     * @return this builder
     */
    public ItemBuilder setGlider(boolean glider) {
        itemMeta.setGlider(glider);
        return this;
    }

    /**
     * Sets whether to hide the entire item tooltip (name, lore, etc.).
     *
     * @param hideTooltip true to hide the tooltip completely
     * @return this builder
     */
    public ItemBuilder setHideTooltip(boolean hideTooltip) {
        itemMeta.setHideTooltip(hideTooltip);
        return this;
    }

    /**
     * Hide the entire item tooltip (name, lore, etc.).
     *
     * @return this builder
     */
    public ItemBuilder setHideTooltip() {
        return setHideTooltip(true);
    }

    /**
     * Appends a line to the item's lore.
     *
     * @param line the lore line supports color codes
     * @return this builder
     */
    public ItemBuilder addLore(String line) {
        List<String> lore = itemMeta.getLore() != null ? new ArrayList<>(itemMeta.getLore()) : new ArrayList<>();
        lore.add(line);
        itemMeta.setLore(lore);
        return this;
    }

    /**
     * Appends multiple lines to the item's lore.
     *
     * @param lines the lore lines
     * @return this builder
     */
    public ItemBuilder addLore(List<String> lines) {
        List<String> lore = itemMeta.getLore() != null ? new ArrayList<>(itemMeta.getLore()) : new ArrayList<>();
        lore.addAll(lines);
        itemMeta.setLore(lore);
        return this;
    }

    /**
     * Appends multiple lines to the item's lore.
     *
     * @param lines the lore lines
     * @return this builder
     */
    public ItemBuilder addLore(String... lines) {
        return addLore(Arrays.asList(lines));
    }

    /**
     * Replaces a specific lore line at the given index.
     *
     * @param line  the new lore line
     * @param index the index to replace
     * @return this builder
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    @SuppressWarnings("UnusedReturnValue")
    public ItemBuilder setLore(int index, String line) {
        List<String> lore = itemMeta.getLore();
        if (lore == null || index < 0 || index >= lore.size()) {
            throw new IndexOutOfBoundsException("Invalid lore index: " + index);
        }
        lore.set(index, line);
        itemMeta.setLore(lore);
        return this;
    }

    /**
     * Adds an enchantment with the specified level.
     * Unsafe enchantments are allowed.
     *
     * @param enchantment the enchantment type
     * @param level       the enchantment level
     * @return this builder
     */
    @SuppressWarnings("UnusedReturnValue")
    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        enchantments.put(enchantment, level);
        return this;
    }

    /**
     * Adds multiple enchantments.
     *
     * @param enchantments the enchantments map
     * @return this builder
     */
    public ItemBuilder addEnchantments(Map<Enchantment, Integer> enchantments) {
        this.enchantments.putAll(enchantments);
        return this;
    }

    /**
     * Removes an enchantment from the item.
     *
     * @param enchantment the enchantment to remove
     * @return this builder
     */
    public ItemBuilder removeEnchantment(Enchantment enchantment) {
        enchantments.remove(enchantment);
        return this;
    }

    /**
     * Adds or removes the enchantment glint visual effect.
     *
     * @param glint true to add the glint effect, false to remove it
     * @return this builder
     */
    public ItemBuilder setEnchantmentGlint(boolean glint) {
        itemMeta.setEnchantmentGlintOverride(glint);
        return this;
    }

    /**
     * Adds the enchantment glint visual effect.
     * Convenience method equivalent to {@code setEnchantmentGlint(true)}.
     *
     * @return this builder
     */
    public ItemBuilder addEnchantmentGlint() {
        return setEnchantmentGlint(true);
    }

    /**
     * Adds an item flag.
     *
     * @param flag the item flag
     * @return this builder
     */
    @SuppressWarnings("UnusedReturnValue")
    public ItemBuilder addItemFlag(ItemFlag flag) {
        itemMeta.addItemFlags(flag);
        return this;
    }

    /**
     * Adds multiple item flags.
     *
     * @param flags the item flags
     * @return this builder
     */
    @SuppressWarnings("UnusedReturnValue")
    public ItemBuilder addItemFlag(List<ItemFlag> flags) {
        itemMeta.addItemFlags(flags.toArray(new ItemFlag[0]));
        return this;
    }

    /**
     * Adds or replaces an attribute modifier for the specified attribute.
     * Removes existing modifiers for the attribute before adding the new one.
     *
     * @param attribute the attribute type
     * @param modifier  the attribute modifier
     * @return this builder
     */
    public ItemBuilder addAttributeModifier(Attribute attribute, AttributeModifier modifier) {
        if (itemMeta.getAttributeModifiers() != null) {
            Collection<AttributeModifier> existingAttributeModifier = itemMeta.getAttributeModifiers().get(attribute);
            for (AttributeModifier oldAttributeModifier : new ArrayList<>(existingAttributeModifier)) {
                itemMeta.removeAttributeModifier(attribute, oldAttributeModifier);
            }
        }
        itemMeta.addAttributeModifier(attribute, modifier);
        return this;
    }

    /**
     * Adds an attribute modifier with the specified parameters.
     * Automatically generates a unique NamespacedKey.
     *
     * @param attribute the attribute type
     * @param amount    the modifier amount
     * @param operation the modifier operation
     * @param slotGroup the equipment slot group
     * @return this builder
     */
    @SuppressWarnings({"UnstableApiUsage", "UnusedReturnValue"})
    public ItemBuilder addAttributeModifier(Attribute attribute,
                                            double amount, AttributeModifier.Operation operation,
                                            EquipmentSlotGroup slotGroup) {

        NamespacedKey attributeKey = new NamespacedKey(BlightedMC.getInstance(), UUID.randomUUID().toString());
        AttributeModifier modifier = new AttributeModifier(attributeKey, amount, operation, slotGroup);
        return addAttributeModifier(attribute, modifier);
    }

    /**
     * Sets the leather armor color from a hex string.
     * Only applies to leather armor pieces and horse armor.
     *
     * @param hex the hex color code with or without "#"
     * @return this builder
     * @throws IllegalArgumentException if a hex format is invalid
     */
    public ItemBuilder setLeatherColor(@NonNull String hex) {
        this.leatherColor = fromHex(hex);
        return this;
    }

    /**
     * Sets the armor trim for the item.
     * Only applies to armor pieces.
     *
     * @param material the trim material
     * @param pattern  the trim pattern
     * @return this builder
     */
    public ItemBuilder setArmorTrim(@NonNull TrimMaterial material, @NonNull TrimPattern pattern) {
        this.armorTrim = new ArmorTrim(material, pattern);
        return this;
    }

    /**
     * Sets the skull owner by player UUID.
     * Automatically converts the item to PLAYER_HEAD.
     *
     * @param playerId the player's UUID
     * @return this builder
     */
    public ItemBuilder setSkullOwner(@NonNull UUID playerId) {
        item.setType(Material.PLAYER_HEAD);
        this.skullOwnerUUID = playerId;
        this.base64Texture = null;
        return this;
    }

    /**
     * Sets a custom skull texture using base64-encoded texture data.
     * Automatically converts the item to PLAYER_HEAD.
     *
     * @param base64Texture the base64-encoded texture JSON
     * @return this builder
     * @see <a href="https://minecraft-heads.com/custom-heads">Minecraft Heads – Custom Textures</a>
     */
    public ItemBuilder setCustomSkullTexture(@NonNull String base64Texture) {
        item.setType(Material.PLAYER_HEAD);
        this.base64Texture = base64Texture;
        this.skullOwnerUUID = null;
        return this;
    }

    /**
     * Sets the banner patterns for the item.
     * Only applies to banners.
     *
     * @param patterns the banner patterns
     * @return this builder
     */
    public ItemBuilder addBannerPatterns(List<Pattern> patterns) {
        this.bannerPatterns = patterns;
        return this;
    }

    /**
     * Configures the {@link EquippableComponent} of this item.
     * Allows direct modification of equippable properties such as slot, dispensability,
     * or equip sound via the provided consumer.
     *
     * @param consumer modifier applied to the equippable component
     * @return this builder
     */
    @SuppressWarnings("UnstableApiUsage")
    public ItemBuilder editEquippable(Consumer<EquippableComponent> consumer) {
        EquippableComponent equippable = itemMeta.getEquippable();
        consumer.accept(equippable);
        itemMeta.setEquippable(equippable);
        return this;
    }

    /**
     * Adds a non-equippable configuration to this item.
     * This prevents the item from being equipped in armor slots or via dispensers.
     *
     * @return this builder
     */
    @SuppressWarnings("UnstableApiUsage")
    public ItemBuilder addNotEquippable() {
        return editEquippable(equippable -> {
            equippable.setSlot(EquipmentSlot.HAND);
            equippable.setDispensable(false);
        });
    }

    /**
     * Configures the {@link JukeboxPlayableComponent} of this item.
     * Allows making the item playable in a jukebox and customizing its playback
     * behavior via the provided consumer.
     *
     * @param consumer modifier applied to the jukebox-playable component
     * @return this builder
     */
    @SuppressWarnings("UnstableApiUsage")
    public ItemBuilder editJukeboxPlayable(Consumer<JukeboxPlayableComponent> consumer) {
        JukeboxPlayableComponent jukebox = itemMeta.getJukeboxPlayable();
        consumer.accept(jukebox);
        itemMeta.setJukeboxPlayable(jukebox);
        return this;
    }

    /**
     * Configures the {@link FoodComponent} of this item.
     * Allows customization of food-related properties such as nutrition,
     * saturation, or effects via the provided consumer.
     *
     * @param consumer modifier applied to the food component
     * @return this builder
     */
    @SuppressWarnings("UnstableApiUsage")
    public ItemBuilder editFood(Consumer<FoodComponent> consumer) {
        FoodComponent food = itemMeta.getFood();
        consumer.accept(food);
        itemMeta.setFood(food);
        return this;
    }

    /**
     * Configures the {@link ToolComponent} of this item.
     * Allows customization of tool-related properties such as mining speed,
     * correct blocks, or damage behavior via the provided consumer.
     *
     * @param consumer modifier applied to the tool component
     * @return this builder
     */
    @SuppressWarnings("UnstableApiUsage")
    public ItemBuilder editTool(Consumer<ToolComponent> consumer) {
        ToolComponent tool = itemMeta.getTool();
        consumer.accept(tool);
        itemMeta.setTool(tool);
        return this;
    }

    /**
     * Finalizes and returns a new {@link ItemStack} with all modifications applied.
     *
     * @return customized item stack
     */
    public ItemStack toItemStack() {
        applyEnchantments();
        applyAttributes();
        applyBannerPatterns();
        applyArmorTrim();
        applyLeatherColor();
        applySkullProperties();

        item.setItemMeta(itemMeta);
        return item;
    }

    /**
     * Returns a clone of the underlying ItemStack.
     *
     * @return cloned item stack
     */
    public ItemStack getItem() {
        return item.clone();
    }

    /**
     * Returns a clone of the current ItemMeta.
     *
     * @return cloned item meta
     */
    public ItemMeta getItemMeta() {
        return itemMeta.clone();
    }

    /**
     * Returns the display name of the item.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return itemMeta.getDisplayName();
    }

    /**
     * Returns an unmodifiable view of the enchantments.
     *
     * @return unmodifiable enchantments map
     */
    public Map<Enchantment, Integer> getEnchantments() {
        return Collections.unmodifiableMap(enchantments);
    }

    /**
     * Returns an unmodifiable view of the banner patterns list.
     *
     * @return unmodifiable banner patterns list
     */
    public List<Pattern> getBannerPatterns() {
        return Collections.unmodifiableList(bannerPatterns);
    }

    // ========================================
    // Private Helper Methods
    // ========================================

    private void applyEnchantments() {
        if (!enchantments.isEmpty()) {
            enchantments.forEach((enchantment, level) -> itemMeta.addEnchant(enchantment, level, true));
        }
    }

    private void applyAttributes() {
        if (!attributes.isEmpty()) {
            attributes.forEach((attr, mods) -> {
                if (itemMeta.getAttributeModifiers() != null) {
                    Collection<AttributeModifier> existingAttributeModifier = itemMeta.getAttributeModifiers().get(attr);
                    for (AttributeModifier oldAttributeModifier : new ArrayList<>(existingAttributeModifier)) {
                        itemMeta.removeAttributeModifier(attr, oldAttributeModifier);
                    }
                }
                mods.forEach(mod -> itemMeta.addAttributeModifier(attr, mod));
            });
        }
    }

    private void applyBannerPatterns() {
        if (bannerPatterns != null && itemMeta instanceof BannerMeta bannerMeta) {
            bannerMeta.setPatterns(bannerPatterns);
            item.setItemMeta(bannerMeta);
        }
    }

    private void applyArmorTrim() {
        if (armorTrim != null && itemMeta instanceof ArmorMeta armorMeta) {
            armorMeta.setTrim(armorTrim);
        }
    }

    private void applyLeatherColor() {
        if (isLeatherDyeable(item.getType()) && itemMeta instanceof LeatherArmorMeta lam && leatherColor != null) {
            lam.setColor(leatherColor);
        }
    }

    private void applySkullProperties() {
        if (item.getType() == Material.PLAYER_HEAD && itemMeta instanceof SkullMeta skullMeta) {
            if (skullOwnerUUID != null) {
                applyOwner(skullMeta, skullOwnerUUID);
            } else if (base64Texture != null) {
                applyTexture(skullMeta, base64Texture);
            }
        }
    }

    /**
     * Applies the skull owner to the provided SkullMeta.
     *
     * @param meta the skull meta to modify
     * @param uuid the player's UUID
     */
    private static void applyOwner(SkullMeta meta, UUID uuid) {
        OfflinePlayer target = Bukkit.getOfflinePlayer(uuid);
        meta.setOwningPlayer(target);
    }

    /**
     * Applies a custom texture to the provided SkullMeta from base64-encoded data.
     * Decodes the texture JSON and extracts the skin URL to create a player profile.
     *
     * @param meta          the skull meta to modify
     * @param base64Texture the base64-encoded texture JSON
     * @throws IllegalArgumentException if the texture URL is invalid
     */
    private static void applyTexture(SkullMeta meta, String base64Texture) {
        String json = new String(Base64.getDecoder().decode(base64Texture), StandardCharsets.UTF_8);
        JsonObject object = JsonParser.parseString(json)
            .getAsJsonObject().getAsJsonObject("textures")
            .getAsJsonObject("SKIN");

        String url = object.get("url").getAsString();

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

    /**
     * Parses a 6-digit hex string into a {@link Color}.
     *
     * @param hex hex string with or without leading "#"
     * @return Bukkit Color
     * @throws IllegalArgumentException if the hex is invalid
     */
    private static Color fromHex(String hex) {
        if (!hex.matches("^#?[0-9a-fA-F]{6}$")) {
            throw new IllegalArgumentException("Invalid hex color: " + hex);
        }
        String cleaned = hex.startsWith("#") ? hex.substring(1) : hex;
        return Color.fromRGB(Integer.parseInt(cleaned, 16));
    }

    private static boolean isLeatherDyeable(Material material) {
        return switch (material) {
            case LEATHER_HELMET, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, LEATHER_BOOTS, LEATHER_HORSE_ARMOR -> true;
            default -> false;
        };
    }
}
