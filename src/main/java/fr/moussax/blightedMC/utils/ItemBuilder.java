package fr.moussax.blightedMC.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.moussax.blightedMC.BlightedMC;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.banner.Pattern;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.components.*;
import org.bukkit.inventory.meta.components.consumable.ConsumableComponent;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.tag.DamageTypeTags;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

/**
 * Fluent builder for creating and customizing {@link ItemStack} instances.
 *
 * <p>Supports display name, item name, lore, durability, enchantments, item flags,
 * unbreakable state, glint, leather colors, armor trims, banner patterns,
 * attributes, player skulls with custom textures, damage resistance, item model,
 * tooltip style, use cooldown, use remainder, consumable, enchantable, and weapon components.</p>
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
@SuppressWarnings({"UnstableApiUsage", "UnusedReturnValue"})
public class ItemBuilder {
    private final ItemStack item;
    private final Map<Enchantment, Integer> enchantments = new HashMap<>();
    private final Map<Attribute, Collection<AttributeModifier>> attributes = new HashMap<>();
    private ItemMeta itemMeta;
    private boolean unstackable;
    private List<Pattern> bannerPatterns;

    private UUID skullOwnerUUID;
    private String base64Texture;
    private Color leatherColor;
    private ArmorTrim armorTrim;

    public ItemBuilder(@NonNull Material material) {
        this(new ItemStack(material));
    }

    public ItemBuilder(@NonNull Material material, int amount) {
        this(new ItemStack(material, validateAmount(material, amount)));
    }

    public ItemBuilder(@NonNull Material material, @NonNull String displayName) {
        this(new ItemStack(material));
        this.itemMeta.setDisplayName(displayName);
    }

    public ItemBuilder(@NonNull Material material, int amount, @NonNull String displayName) {
        this(new ItemStack(material, validateAmount(material, amount)));
        this.itemMeta.setDisplayName(displayName);
    }

    public ItemBuilder(@NonNull ItemStack itemStack) {
        this.item = itemStack.clone();
        ItemMeta meta = this.item.getItemMeta();

        if (meta == null) {
            throw new IllegalStateException("ItemMeta cannot be null for material: " + itemStack.getType());
        }

        this.itemMeta = meta;
        this.enchantments.putAll(itemStack.getEnchantments());

        if (this.itemMeta instanceof EnchantmentStorageMeta storageMeta) {
            this.enchantments.putAll(storageMeta.getStoredEnchants());
        }
    }

    private static int validateAmount(@NonNull Material material, int amount) {
        if (amount < 1 || amount > material.getMaxStackSize()) {
            throw new IllegalArgumentException("Amount must be between 1 and " + material.getMaxStackSize());
        }
        return amount;
    }

    private static void applyBase64Texture(SkullMeta meta, String base64Texture) {
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

    private static Color fromHex(String hex) {
        if (!hex.matches("^#?[0-9a-fA-F]{6}$")) {
            throw new IllegalArgumentException("Invalid hex color: " + hex);
        }

        String cleaned = hex.startsWith("#") ? hex.substring(1) : hex;
        return Color.fromRGB(Integer.parseInt(cleaned, 16));
    }

    private static boolean isLeatherDyeable(Material material) {
        return switch (material) {
            case LEATHER_HELMET, LEATHER_CHESTPLATE, LEATHER_LEGGINGS,
                 LEATHER_BOOTS, LEATHER_HORSE_ARMOR -> true;
            default -> false;
        };
    }

    public ItemBuilder setItemName(@NonNull String name) {
        this.itemMeta.setItemName(name);
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

    public ItemBuilder setLore(int index, String line) {
        List<String> lore = itemMeta.getLore();
        if (lore == null || index < 0 || index >= lore.size()) {
            throw new IndexOutOfBoundsException("Invalid lore index: " + index);
        }

        lore.set(index, line);
        itemMeta.setLore(lore);
        return this;
    }

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

    public ItemBuilder setMaxStackSize(int size) {
        itemMeta.setMaxStackSize(size);
        return this;
    }

    public ItemBuilder setDurabilityDamage(int damage) {
        if (itemMeta instanceof Damageable damageable) {
            damageable.setDamage(damage);
        }
        return this;
    }

    public ItemBuilder setMaxDurability(int maxDurability) {
        if (itemMeta instanceof Damageable damageable) {
            damageable.setMaxDamage(maxDurability);
        }
        return this;
    }

    public ItemBuilder setDurabilityPercent(double percent) {
        if (!(itemMeta instanceof Damageable damageable)) return this;

        int maxDurability = damageable.hasMaxDamage()
            ? damageable.getMaxDamage()
            : item.getType().getMaxDurability();

        int damage = (int) Math.round(maxDurability * (1.0 - percent));
        damageable.setDamage(Math.min(damage, maxDurability - 1));
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        itemMeta.setUnbreakable(unbreakable);
        return this;
    }

    public ItemBuilder setUnstackable(boolean unstackable) {
        this.unstackable = unstackable;
        return this;
    }

    public ItemBuilder setFireResistant(boolean fireResistant) {
        if (fireResistant) {
            itemMeta.setDamageResistant(DamageTypeTags.IS_FIRE);
        } else {
            itemMeta.setDamageResistant((Tag<DamageType>) null);
        }
        return this;
    }

    /**
     * Sets the damage type tag this item is resistant to when in entity (dropped item) form.
     * Use tags from {@link org.bukkit.tag.DamageTypeTags}.
     *
     * @param tag the damage type tag, or {@code null} to clear
     */
    public ItemBuilder setDamageResistant(@Nullable Tag<DamageType> tag) {
        itemMeta.setDamageResistant(tag);
        return this;
    }

    public ItemBuilder setGlider(boolean glider) {
        itemMeta.setGlider(glider);
        return this;
    }

    public ItemBuilder setHideTooltip(boolean hideTooltip) {
        itemMeta.setHideTooltip(hideTooltip);
        return this;
    }

    public ItemBuilder setHideTooltip() {
        return setHideTooltip(true);
    }

    public ItemBuilder setRarity(ItemRarity rarity) {
        itemMeta.setRarity(rarity);
        return this;
    }

    public ItemBuilder setItemModel(@NonNull NamespacedKey itemModel) {
        itemMeta.setItemModel(itemModel);
        return this;
    }

    public ItemBuilder clearItemModel() {
        itemMeta.setItemModel(null);
        return this;
    }

    public ItemBuilder setTooltipStyle(@NonNull NamespacedKey tooltipStyle) {
        itemMeta.setTooltipStyle(tooltipStyle);
        return this;
    }

    public ItemBuilder clearTooltipStyle() {
        itemMeta.setTooltipStyle(null);
        return this;
    }

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

    public ItemBuilder setEnchantmentGlint(boolean glint) {
        itemMeta.setEnchantmentGlintOverride(glint);
        return this;
    }

    public ItemBuilder addEnchantmentGlint() {
        return setEnchantmentGlint(true);
    }

    public ItemBuilder addItemFlag(ItemFlag flag) {
        itemMeta.addItemFlags(flag);
        return this;
    }

    public ItemBuilder addItemFlag(ItemFlag... flags) {
        itemMeta.addItemFlags(flags);
        return this;
    }

    public ItemBuilder addAttributeModifier(Attribute attribute, AttributeModifier modifier) {
        if (itemMeta.getAttributeModifiers() != null) {
            Collection<AttributeModifier> existing = itemMeta.getAttributeModifiers().get(attribute);
            for (AttributeModifier old : new ArrayList<>(existing)) {
                itemMeta.removeAttributeModifier(attribute, old);
            }
        }
        itemMeta.addAttributeModifier(attribute, modifier);
        return this;
    }

    public ItemBuilder addAttributeModifier(
        Attribute attribute,
        double amount,
        AttributeModifier.Operation operation,
        EquipmentSlotGroup slotGroup
    ) {
        NamespacedKey key = new NamespacedKey(BlightedMC.getInstance(), UUID.randomUUID().toString());
        AttributeModifier modifier = new AttributeModifier(key, amount, operation, slotGroup);
        return addAttributeModifier(attribute, modifier);
    }

    public ItemBuilder editEquippable(Consumer<EquippableComponent> consumer) {
        EquippableComponent equippable = itemMeta.getEquippable();
        consumer.accept(equippable);
        itemMeta.setEquippable(equippable);
        return this;
    }

    public ItemBuilder addNotEquippable() {
        return editEquippable(equippable -> {
            equippable.setSlot(EquipmentSlot.HAND);
            equippable.setDispensable(false);
        });
    }

    public ItemBuilder editFood(Consumer<FoodComponent> consumer) {
        FoodComponent food = itemMeta.getFood();
        consumer.accept(food);
        itemMeta.setFood(food);
        return this;
    }

    public ItemBuilder editTool(Consumer<ToolComponent> consumer) {
        ToolComponent tool = itemMeta.getTool();
        consumer.accept(tool);
        itemMeta.setTool(tool);
        return this;
    }

    public ItemBuilder editJukeboxPlayable(Consumer<JukeboxPlayableComponent> consumer) {
        JukeboxPlayableComponent jukebox = itemMeta.getJukeboxPlayable();
        consumer.accept(jukebox);
        itemMeta.setJukeboxPlayable(jukebox);
        return this;
    }

    public ItemBuilder editUseCooldown(Consumer<UseCooldownComponent> consumer) {
        UseCooldownComponent cooldown = itemMeta.getUseCooldown();
        consumer.accept(cooldown);
        itemMeta.setUseCooldown(cooldown);
        return this;
    }

    public ItemBuilder editConsumable(Consumer<ConsumableComponent> consumer) {
        ConsumableComponent consumable = itemMeta.getConsumable();
        consumer.accept(consumable);
        itemMeta.setConsumable(consumable);
        return this;
    }

    public ItemBuilder setUseRemainder(@Nullable ItemStack remainder) {
        itemMeta.setUseRemainder(remainder);
        return this;
    }

    public ItemBuilder setEnchantable(@Nullable Integer value) {
        itemMeta.setEnchantable(value);
        return this;
    }

    public ItemBuilder setLeatherColor(@NonNull String hex) {
        this.leatherColor = fromHex(hex);
        return this;
    }

    public ItemBuilder setArmorTrim(@NonNull TrimMaterial material, @NonNull TrimPattern pattern) {
        this.armorTrim = new ArmorTrim(material, pattern);
        return this;
    }

    public ItemBuilder addBannerPatterns(List<Pattern> patterns) {
        this.bannerPatterns = patterns;
        return this;
    }

    public ItemBuilder setSkullOwner(@NonNull UUID playerId) {
        item.setType(Material.PLAYER_HEAD);
        this.skullOwnerUUID = playerId;
        this.base64Texture = null;
        return this;
    }

    public ItemBuilder setCustomSkullTexture(@NonNull String base64Texture) {
        item.setType(Material.PLAYER_HEAD);
        this.base64Texture = base64Texture;
        this.skullOwnerUUID = null;
        return this;
    }

    public ItemBuilder asEnchantedBook() {
        if (item.getType() != Material.ENCHANTED_BOOK) {
            item.setType(Material.ENCHANTED_BOOK);
            this.itemMeta = this.item.getItemMeta();
        }
        return this;
    }

    public ItemStack toItemStack() {
        applyEnchantments();
        applyAttributes();
        applyBannerPatterns();
        applyArmorTrim();
        applyLeatherColor();
        applySkullProperties();

        if (unstackable) {
            itemMeta.setMaxStackSize(1);
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

    public ItemBuilder setItemMeta(Consumer<ItemMeta> consumer) {
        consumer.accept(this.itemMeta);
        return this;
    }

    public ItemBuilder setItemMeta(ItemMeta itemMeta) {
        this.itemMeta = itemMeta;
        return this;
    }

    public String getDisplayName() {
        return itemMeta.getDisplayName();
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    public ItemBuilder setDisplayName(@NonNull String displayName) {
        this.itemMeta.setDisplayName(displayName);
        return this;
    }

    public Map<Enchantment, Integer> getEnchantments() {
        return Collections.unmodifiableMap(enchantments);
    }

    public List<Pattern> getBannerPatterns() {
        return bannerPatterns != null
            ? Collections.unmodifiableList(bannerPatterns)
            : Collections.emptyList();
    }

    private void applyEnchantments() {
        if (enchantments.isEmpty()) return;

        if (itemMeta instanceof EnchantmentStorageMeta storageMeta) {
            enchantments.forEach((enchantment, level) ->
                storageMeta.addStoredEnchant(enchantment, level, true)
            );
            return;
        }
        enchantments.forEach((enchantment, level) ->
            itemMeta.addEnchant(enchantment, level, true)
        );
    }

    private void applyAttributes() {
        if (attributes.isEmpty()) return;
        attributes.forEach((attr, mods) -> {
            if (itemMeta.getAttributeModifiers() != null) {
                Collection<AttributeModifier> existing = itemMeta.getAttributeModifiers().get(attr);
                for (AttributeModifier old : new ArrayList<>(existing)) {
                    itemMeta.removeAttributeModifier(attr, old);
                }
            }
            mods.forEach(mod -> itemMeta.addAttributeModifier(attr, mod));
        });
    }

    private void applyBannerPatterns() {
        if (bannerPatterns != null && itemMeta instanceof BannerMeta bannerMeta) {
            bannerMeta.setPatterns(bannerPatterns);
            this.itemMeta = bannerMeta;
        }
    }

    private void applyArmorTrim() {
        if (armorTrim != null && itemMeta instanceof ArmorMeta armorMeta) {
            armorMeta.setTrim(armorTrim);
        }
    }

    private void applyLeatherColor() {
        if (leatherColor != null && isLeatherDyeable(item.getType())
            && itemMeta instanceof LeatherArmorMeta lam) {
            lam.setColor(leatherColor);
        }
    }

    private void applySkullProperties() {
        if (item.getType() == Material.PLAYER_HEAD && itemMeta instanceof SkullMeta skullMeta) {
            if (skullOwnerUUID != null) {
                skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(skullOwnerUUID));
            } else if (base64Texture != null) {
                applyBase64Texture(skullMeta, base64Texture);
            }
        }
    }
}
